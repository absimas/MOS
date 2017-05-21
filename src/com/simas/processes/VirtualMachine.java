package com.simas.processes;

import com.simas.real_machine.Memory;
import com.simas.resources.Element;
import com.simas.resources.Interrupt;
import com.simas.resources.Resource;
import com.sun.istack.internal.Nullable;

/**
 * User program executor.
 */
public abstract class VirtualMachine extends Process {

  static final int PRIORITY = 10;

  /**
   * {@link Memory} position of this VM.
   */
  private final int internalMemoryPosition;

  VirtualMachine(Process parent, int internalMemoryPosition, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
    this.internalMemoryPosition = internalMemoryPosition;
  }

  @Override
  public void run() {
    /* ToDo do we need PTR?
    // Wait for internal memory resource
    final Element memory = Resource.INTERNAL_MEMORY.request(this);

    // Fill PTR

    // Free internal memory resource
    memory.free();
    */

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
