package com.simas.processes;

import com.simas.resources.Resource;

/**
 * The first created process.
 */
public class Root extends Process {

  static final int PRIORITY = 100;

  public Root() {
    super(null, PRIORITY);

    // Prevent multiple roots
    if (PROCESSES.stream().anyMatch(process -> process instanceof Root)) {
      throw new IllegalStateException("Root process was already created!");
    }
  }

  @Override
  public void run() {
    // Create system processes
    final CLI cli = new CLI(this);
    final MainProc mainProc = new MainProc(this);
    final ReadDisk readDisk = new ReadDisk(this);
    final WriteDisk writeDisk = new WriteDisk(this);
    final ReadInput readInput = new ReadInput(this);
    final WriteOutput writeOutput = new WriteOutput(this);

    // Create system resource elements
    Resource.INTERNAL_MEMORY.create(this);
    Resource.CHANNEL_1.create(this);
    Resource.CHANNEL_2.create(this);
    Resource.CHANNEL_3.create(this);

    // Wait for MOS_END resource
    Resource.MOS_END.request(this);

    // Destroy system processes
    writeOutput.destroy();
    readInput.destroy();
    writeDisk.destroy();
    readDisk.destroy();
    mainProc.destroy();
    cli.destroy();

    // Destroy resource elements // ToDo necessary?
  }

}
