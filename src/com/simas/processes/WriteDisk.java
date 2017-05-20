package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Process that writes to disk.
 */
public class WriteDisk extends Process {

  static final int PRIORITY = 10;

  WriteDisk(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {

  }

}
