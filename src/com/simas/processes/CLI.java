package com.simas.processes;

import com.simas.resources.Element;
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
    Resource.MESSAGE.request(this, message -> message.destination == CLI.this);

    // ToDo Process input
    int input;
    final String program;

    switch (input) {
      case 0:
        // Create Program in memory resource
        Resource.PROGRAM_IN_MEMORY.create(this, element -> {
          // Non zero duration
          element.duration = program.length();
          element.program = program;
        });

        // Loop CLI
        run();
        break;
      case 1:
        // Wait for No task resource
        Resource.NO_TASK.request(this);

        // Loop CLI
        run();
        break;
      case 2:
        // Create MOS end resource
        Resource.MOS_END.create(this);

        // Wait for a non-existent resource
        Resource.NON_EXISTENT.request(this);

        throw new IllegalStateException("CLI shouldn't reach this part!");
        break;
      default:
        throw new IllegalStateException(String.format("Unexpected input read: %d", input));
    }
  }

}
