package com.simas.processes;

import com.simas.resources.Element;
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

  }

}
