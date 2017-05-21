package com.simas.processes;

import com.simas.real_machine.Comparison;
import com.simas.real_machine.RealMachine;
import com.simas.resources.Element;
import com.simas.resources.Message;
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
    READY, RUNNING, BLOCKED, READY_STOPPED, BLOCKED_STOPPED
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
  Process(Process parent, int priority, @Nullable List<Element> resources) {
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
    parent.children.add(this);

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
  public void  run() {
    // Everyone needs the CPU
    Resource.CPU.request(this, message -> message.destination == this);
  }

  /**
   * Destroys this process and all of its children recursively.
   */
  public void destroy() {
    // Destroy children
    for (Process child : children) {
      child.destroy();
    }

    // Destroy created resource elements // Use iterator because destroy may access the list
    Iterator<Element> iterator = createdResources.iterator();
    while (iterator.hasNext()) {
      iterator.next().destroy();
    }
    createdResources.clear();

    // Free other available resource elements
    // The created resources will already be removed from this list
    // Use iterator because free may access the list
    iterator = availableResources.iterator();
    while (iterator.hasNext()) {
      iterator.next().free();
    }
    availableResources.clear();

    // Remove from parent's child list
    parent.children.remove(this);

    // Remove from global process list
    PROCESSES.remove(this);

    // ToDo running process destroyed => change its state?
    if (state == State.RUNNING) {
      // ToDo call scheduler
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
  @SuppressWarnings("WhileLoopReplaceableByForEach")
  public void dropCPU() {
    final Iterator<Element> iterator = availableResources.iterator();
    while (iterator.hasNext()) {
      Element element = iterator.next();
      if (element.resource != Resource.CPU) continue;
      element.destroy();
    }
  }

  /**
   * Places a CPU request for this process.
   * This call will block until CPU is given and {@link Resource#CPU} is notified.
   */
  public void requestCPU() {
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
    return String.format("Process {%s, %s}", super.toString(), getState());
  }

}