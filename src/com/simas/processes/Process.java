package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
   * Process that created this process.
   */
  private final Process parent;
  /**
   * Internal name.
   */
  private final int id = ids++;
  /**
   * Process priority.
   */
  private final int priority;
  private final Thread thread;

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
   * Destroys this process and all of its children recursively.
   */
  public void destroy() {
    // Destroy children
    for (Process child : children) {
      child.destroy();
    }

    // Destroy created resource elements
    for (Element element : createdResources) {
      element.destroy();
    }

    // Free available resource elements, except the ones we have created because they've just been destroyed
    availableResources.stream()
        .filter(element -> !createdResources.contains(element)) // Skip elements we have created
        .forEach(Element::free);                                // Free all other elements

    // Remove from parent's child list
    parent.children.remove(this);

    // Remove from global process list
    PROCESSES.remove(this);

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