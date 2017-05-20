package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Main process that creates multiple {@link JobGovernor}s.
 */
public class MainProc extends Process {

  MainProc(Process parent, int priority, @Nullable Element... resources) {
    super(parent, priority, resources);
  }

  @Override
  public void run() {

  }

}
