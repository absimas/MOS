package com.simas.processes;

import com.simas.resources.Element;
import com.simas.resources.Interrupt;
import com.simas.resources.Message;
import com.simas.resources.ProgramElement;
import com.simas.resources.Resource;
import com.simas.resources.StringElement;
import com.sun.istack.internal.Nullable;

import java.util.function.Predicate;

/**
 * User program executor.
 */
public abstract class VirtualMachine extends Process {

  static final int PRIORITY = 10;

  VirtualMachine(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    // Wait for internal memory resource // ToDo element type?
    final StringElement memory = Resource.INTERNAL_MEMORY.request(this);

    // ToDo fill PTR?

    // Execute user program
    execute(); // ToDo how to execute step-by-step?

    // Send interrupt message to the parent (JobGovernor) process
    Resource.INTERRUPT.create(this, element -> {
      element.destination = parent;
      // ToDo determine interrupt type
      element.type = Interrupt.Type.HALT;
    });

    // ToDo call scheduler
  }

  /**
   * Executes program specific to this VM.
   */
  protected abstract void execute();

}
