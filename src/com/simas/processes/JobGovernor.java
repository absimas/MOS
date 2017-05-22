package com.simas.processes;

import com.simas.Log;
import com.simas.Scheduler;
import com.simas.real_machine.Channel3;
import com.simas.real_machine.RealMachine;
import com.simas.resources.Element;
import com.simas.resources.Interrupt;
import com.simas.resources.Message;
import com.simas.resources.ProgramElement;
import com.simas.resources.Resource;
import com.sun.istack.internal.Nullable;

/**
 * Program governor to overlook the work of a single {@link VirtualMachine}.
 */
public class JobGovernor extends Process {

  /**
   * Priority is higher than VM's.
   */
  static final int PRIORITY = VirtualMachine.PRIORITY + 1;

  /**
   * Passed when creating the governor.
   */
  private final ProgramElement programResource;

  /**
   * Create a process.
   * @param parent          parent process that created this one
   * @param programResource initial program resource
   * @param resources       other initial resources
   */
  JobGovernor(Process parent, ProgramElement programResource, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
    if (programResource.program == null) {
      throw new IllegalStateException("Governed program name not set!");
    }
    this.programResource = programResource;
  }

  @Override
  public void run() {
    super.run();

    // Start VM
    final VirtualMachine virtualMachine;
    switch (programResource.program) {
      case VirtualMachine1.NAME:
        virtualMachine = new VirtualMachine1(this);
        break;
      case VirtualMachine2.NAME:
        virtualMachine = new VirtualMachine2(this);
        break;
      case VirtualMachine3.NAME:
        virtualMachine = new VirtualMachine3(this);
        break;
      default:
        throw new IllegalStateException(String.format("Unexpected program name encountered: %s", programResource.program));
    }

    afterCreation(virtualMachine);
  }

  /**
   * Called after {@link #run()} has created a virtual machine.
   * Used for iterating.
   * @param virtualMachine virtual machine that was created
   */
  private void afterCreation(VirtualMachine virtualMachine) {
    requestCPU();

    // Wait for an interrupt message
    final Interrupt interrupt = Resource.INTERRUPT.request(this, i -> {
      // From child VM to this JobGovernor
      return i.creator == virtualMachine && i.destination == JobGovernor.this;
    });

    // Stop child VM
    virtualMachine.stop();

    // Handle critical interruptions
    switch (interrupt.type) {
      case PROGRAM_INTERRUPT: case HALT:
        Log.w("Encountered interruption %s in %s!", interrupt.type, interrupt.creator);
        // Destroy VM
        virtualMachine.destroy();

        // Create program in memory resource with 0 duration to get this governor destroyed
        Resource.PROGRAM_IN_MEMORY.create(this);

        Scheduler.schedule();
        return;
    }

    // Handle I/O interrupts
    final int position = interrupt.command.getArgument() + interrupt.vm.getMemoryPosition();
    switch (interrupt.type) {
      case SD:
        Channel3.getInstance().setPointer(position);
        break;
      case TI:
        Scheduler.schedule();
        break;
      case GD:
        // Create input packet
        Resource.INPUT_PACKET.create(this, element -> {
          element.position = position;
          element.size = RealMachine.WORD_SIZE;
        });

        // Wait for a message from ReadInput
        requestMessage(ReadInput.class);
        break;
      case PD:
        // Create output packet
        Resource.OUTPUT_PACKET.create(this, element -> {
          element.position = position;
          element.size = RealMachine.WORD_SIZE;
        });

        // Wait for a message from WriteOutput
        requestMessage(WriteOutput.class);
        break;
      case RD:
        // Create disk read packet
        Resource.DISK_READ_PACKET.create(this, element -> {
          element.internalPosition = position;
          element.externalPosition = Channel3.getInstance().getPointer();
        });

        // Wait for a message from ReadDisk
        requestMessage(ReadDisk.class);
        break;
      case WD:
        // Create disk write packet
        Resource.DISK_WRITE_PACKET.create(this, element -> {
          element.internalPosition = position;
          element.externalPosition = Channel3.getInstance().getPointer();
        });

        // Wait for a message from WriteDisk
        requestMessage(WriteDisk.class);
        break;
      default:
        throw new IllegalStateException(String.format("Unexpected interrupt type %s!", interrupt.type));
    }

    // Activate VM
    virtualMachine.resume();

    // Repeat // Waits for another interrupt resource
    afterCreation(virtualMachine);
  }

  /**
   * Request a message from the given class.
   * @param source class whose message we're asking for
   */
  private Message requestMessage(Class<? extends Process> source) {
    return Resource.MESSAGE.request(this, message -> {
      if (message.destination == this && source.isInstance(message.creator.getClass())) {
        Log.v("%s waited for a message from %s but found a message from %s to %s which does not match.",
            toString(), source.getName(), message.creator, message.destination);
        return false;
      } else {
        Log.v("%s found a valid message.", toString());
        return true;
      }
    });
  }

}
