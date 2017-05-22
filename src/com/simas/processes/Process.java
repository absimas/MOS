package com.simas.processes;

import com.simas.Scheduler;
import com.simas.real_machine.Comparison;
import com.simas.real_machine.RealMachine;
import com.simas.resources.Element;
import com.simas.resources.Resource;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Base process class.
 */
public abstract class Process implements Runnable {

  public static final List<Process> PROCESSES = new ArrayList<>();

  public enum State {
    RUNNING, READY, READY_STOPPED, BLOCKED, BLOCKED_STOPPED
  }

  /**
   * Process id counter. Used by {@link #id}.
   */
  private static int ids = 0;

  /**
   * Resources elements created by this process.
   */
  public final List<Element> createdResources = new ArrayList<>();
  /**
   * Resources elements available to this process. Unique list.
   */
  public final Set<Element> availableResources = new HashSet<>();
  /**
   * Processes created by this process.
   */
  public final List<Process> children = new ArrayList<>();
  /**
   * Process priority.
   */
  public final int priority;
  /**
   * Process that created this process.
   */
  protected final Process parent;
  /**
   * Internal name.
   */
  private final int id = ids++;
  private final Thread thread;

  /**
   * Saved {@link com.simas.real_machine.RealMachine#TMP}.
   */
  private String savedTMP;
  /**
   * Saved {@link com.simas.real_machine.RealMachine#IC}.
   */
  private int savedIC;
  /**
   * Saved {@link com.simas.real_machine.RealMachine#C}.
   */
  private Comparison savedC;
  @NotNull
  private State state = State.READY;

  /**
   * Create a process.
   * @param parent    parent process that created this one
   * @param priority  priority of this process
   * @param resources initial resources
   */
  Process(Process parent, int priority, @Nullable Element... resources) {
    this(parent, priority, (resources == null) ? null : Arrays.asList(resources));
  }

  /**
   * Create a process.
   * @param parent    parent process that created this one
   * @param priority  priority of this process
   * @param resources initial resources
   */
  Process(@Nullable Process parent, int priority, @Nullable List<Element> resources) {
    this.parent = parent;
    this.priority = priority;
    this.thread = new Thread(this);

    // Add initially available resources
    if (resources != null) {
      availableResources.addAll(resources);
    }

    // Add to global process list
    PROCESSES.add(this);

    // Add as parent's child
    if (parent != null) parent.children.add(this);

    // Start process thread
    thread.start();
  }

  /**
   * Save registers.
   */
  public void save() {
    savedTMP = RealMachine.TMP;
    savedIC = RealMachine.IC;
    savedC = RealMachine.C;
  }

  /**
   * Restore registers.
   */
  public void restore() {
    RealMachine.TMP = savedTMP;
    RealMachine.IC = savedIC;
    RealMachine.C = savedC;
  }

  @Override
  public void run() {
    // Everyone needs the CPU
    requestCPU();
  }

  /**
   * Destroys this process and all of its children recursively.
   */
  public void destroy() {
    // Destroy children
    for (Process child : children) {
      child.destroy();
    }

    // Clear created resource elements
    final List<Element> created = new ArrayList<>(createdResources);
    createdResources.clear();
    // Destroy created items // Separating action because destroy may cause concurrent modifications
    created.forEach(Element::destroy);

    // Clear available resource elements
    final List<Element> available = new ArrayList<>(availableResources);
    availableResources.clear();
    // Free available resources // Creates ones will already be removed by destroy
    available.forEach(Element::free);

    // Remove from parent's child list
    parent.children.remove(this);

    // Remove from global process list
    PROCESSES.remove(this);

    if (state == State.RUNNING) {
      // When a running process is destroyed, change its state to BLOCKED_STOPPED
      state = State.BLOCKED_STOPPED;
      // And call the scheduler
      Scheduler.schedule();
    }
  }

  /**
   * Stops a process.
   */
  public void stop() {
    if (state == State.BLOCKED) {
      setState(State.BLOCKED_STOPPED);
    } else if (state == State.READY) {
      setState(State.READY_STOPPED);
    } else {
      throw new IllegalStateException(String.format("Tried to stop process %s that's in %s state.", toString(), state));
    }
  }

  /**
   * Resumes a stopped process.
   */
  public void resume() {
    if (state == State.BLOCKED_STOPPED) {
      setState(State.BLOCKED);
    } else if (state == State.READY_STOPPED) {
      setState(State.READY);
    } else {
      throw new IllegalStateException(String.format("Tried to resume process %s that's in %s state.", toString(), state));
    }
  }

  /**
   * Process drops the CPU resource. If found, Will also call {@link Element#destroy()}.
   * Thus it will be removed from {@link #availableResources} and {@link #createdResources}.
   */
  public void dropCPU() {
    // Remove all CPU-related resources
    final Iterator<Element> iterator = availableResources.iterator();
    final List<Element> expendables = new ArrayList<>();
    while (iterator.hasNext()) {
      final Element element = iterator.next();
      if (element.resource != Resource.CPU) continue;
      expendables.add(element);
      iterator.remove();
    }

    // Destroy all CPU-related resources
    expendables.forEach(Element::destroy);
  }

  /**
   * Places a CPU request for this process.
   * No request is made if this process already has the CPU (e.g. when process's run iterates recursively).
   * This call will block until CPU is given and {@link Resource#CPU} is notified.
   */
  public void requestCPU() {
    for (Element availableResource : availableResources) {
      if (availableResource.resource == Resource.CPU) return;
    }
    Resource.CPU.request(this, message -> message.destination == this);
  }

  @NotNull
  public final State getState() {
    return state;
  }

  public final void setState(@NotNull State state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return String.format("%s (%s-%d)", getClass().getSimpleName(), getState(), priority);
  }

  public String getId() {
    return Integer.toHexString(hashCode());
  }

  public String getName() {
    return getClass().getSimpleName();
  }

  public int getPriority() {
    return priority;
  }

}