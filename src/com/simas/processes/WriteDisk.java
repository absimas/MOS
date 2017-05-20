package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Process that writes to disk.
 */
public class WriteDisk extends Process {

  WriteDisk(Process parent, int priority, @Nullable Element... resources) {
    super(parent, priority, resources);
  }

  @Override
  public void run() {

  }

}
