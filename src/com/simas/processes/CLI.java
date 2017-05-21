package com.simas.processes;

import com.simas.resources.Element;
import com.simas.resources.Message;
import com.simas.resources.Resource;
import com.sun.istack.internal.Nullable;

/**
 * Input manager.
 */
public class CLI extends Process {

  static final int PRIORITY = 10;

  /**
   * Internal memory destination (position) to where the initial command will be read.
   */
  private static final int INITIAL_INPUT_POSITION = 0;
  /**
   * Symbol count of the initial command.
   */
  private static final int INITIAL_INPUT_SIZE = 1;

  CLI(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    // Create input packet
    Resource.INPUT_PACKET.create(this, element -> {
      element.position = INITIAL_INPUT_POSITION;
      element.size = INITIAL_INPUT_SIZE;
    });

    // Wait for ReadInput response
    Message request = Resource.MESSAGE.request(this, message -> message.destination == CLI.this);

    final String program;

    switch (request.message) {
      case "-":
        // Create MOS end resource
        Resource.MOS_END.create(this);

        // Wait for a non-existent resource
        Resource.NON_EXISTENT.request(this);

        throw new IllegalStateException("CLI shouldn't reach this part!");
      case "0":
        // Wait for No task resource
        Resource.NO_TASK.request(this);

        // Loop CLI
        run();
        break;
      case VirtualMachine1.NAME:
        createProgramInMemory(VirtualMachine1.NAME);
        break;
      case VirtualMachine2.NAME:
        createProgramInMemory(VirtualMachine2.NAME);
        break;
      case VirtualMachine3.NAME:
        createProgramInMemory(VirtualMachine3.NAME);
        break;
      default:
        throw new IllegalStateException(String.format("Unexpected input read: '%s'", request.message));
    }
  }

  private void createProgramInMemory(String vmName) {
    // Create Program in memory resource
    Resource.PROGRAM_IN_MEMORY.create(this, element -> {
      // Non zero duration
      element.duration = vmName.length();
      element.program = vmName;
    });

    // Loop CLI
    run();
  }

}
