package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Process that read from disk.
 */
public class ReadDisk extends Process {

  static final int PRIORITY = 10;

  ReadDisk(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {

  }

}
