package com.simas.processes;

import com.simas.resources.Element;
import com.simas.resources.ProgramElement;
import com.simas.resources.Resource;
import com.sun.istack.internal.Nullable;

/**
 * Main process that creates multiple {@link JobGovernor}s.
 */
public class MainProc extends Process {

  static final int PRIORITY = 10;

  MainProc(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    super.run();

    // Fetch program in memory resource
    final ProgramElement element = Resource.PROGRAM_IN_MEMORY.request(this);

    // Check program duration
    if (element.duration > 0) {
      // Create JobGovernor // This will perform the work on the processes worker thread
      new JobGovernor(this, element);

      // Repeat
      run();
      return;
    }

    // Destroy creator of the element
    element.creator.destroy();

    // If there are no more children ask CLI what to do next
    if (children.size() == 0) {
      // When MainProc has no more children send NO_TASK resource received by CLI process
      Resource.NO_TASK.create(this);
    }

    // Repeat
    run();
  }

}
