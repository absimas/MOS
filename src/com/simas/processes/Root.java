package com.simas.processes;

import com.simas.resources.Element;
import com.simas.resources.Resource;

/**
 * The first created process.
 */
public class Root extends Process {

  public Root(int priority) {
    super(null, priority);

    // Prevent multiple roots
    if (PROCESSES.stream().anyMatch(process -> process instanceof Root)) {
      throw new IllegalStateException("Root process was already created!");
    }
  }

  @Override
  public void run() {
    // Create system processes
    final CLI cli = new CLI(this, 10);
    final MainProc mainProc = new MainProc(this, 10);
    final ReadDisk readDisk = new ReadDisk(this, 10);
    final WriteDisk writeDisk = new WriteDisk(this, 10);
    final ReadInput readInput = new ReadInput(this, 10);
    final WriteInput writeInput = new WriteInput(this, 10);

    // Wait for MOS_END resource
    Resource.MOS_END.request(this);

    // Destroy system processes
    writeInput.destroy();
    readInput.destroy();
    writeDisk.destroy();
    readDisk.destroy();
    mainProc.destroy();
    cli.destroy();

    // Destroy resource elements // ToDo necessary?
  }

}
