package com.simas.processes;

import com.simas.exceptions.TIException;
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
    try {
      execute();
    } catch (TIException ignored) {
      interruptParent(Interrupt.Type.TI);
    } catch (Exception e) {
      // ToDo rethrow to parent
      interruptParent(Interrupt.Type.HALT);
    }

    // ToDo call scheduler
  }

  /**
   * Send interrupt message to the parent (JobGovernor) process.
   * @param type interruption type
   */
  protected void interruptParent(Interrupt.Type type) {
    Resource.INTERRUPT.create(this, element -> {
      element.destination = parent;
      element.type = type;
    });
  }

  /**
   * Executes program specific to this VM.
   */
  protected abstract void execute() throws TIException;

}
