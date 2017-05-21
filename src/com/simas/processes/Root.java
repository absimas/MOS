package com.simas.processes;

import com.simas.resources.Resource;

/**
 * The first created process.
 */
public class Root extends Process {

  /**
   * Singleton instance.
   */
  public static Root instance;

  static final int PRIORITY = 100;

  public Root() {
    super(null, PRIORITY);

    // Initially running
    setState(State.RUNNING);

    // Prevent multiple roots
    if (instance != null) {
      throw new IllegalStateException("Root process was already created!");
    }
    instance = this;
  }

  @Override
  public void run() {
    // Don't call super.run()! Root process doesn't request the CPU resource - it creates it.

    // Create system resource elements
    Resource.CPU.create(this, element -> element.destination = this); // ToDo prevent multiples
    Resource.INTERNAL_MEMORY.create(this);
    Resource.CHANNEL_1.create(this);
    Resource.CHANNEL_2.create(this);
    Resource.CHANNEL_3.create(this);

    // Create system processes
    final CLI cli = new CLI(this);
    final MainProc mainProc = new MainProc(this);
    final ReadDisk readDisk = new ReadDisk(this);
    final WriteDisk writeDisk = new WriteDisk(this);
    final ReadInput readInput = new ReadInput(this);
    final WriteOutput writeOutput = new WriteOutput(this);

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
