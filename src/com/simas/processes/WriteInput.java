package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Process that write to screen.
 */
public class WriteInput extends Process {

  static final int PRIORITY = 10;

  WriteInput(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {

  }

}
