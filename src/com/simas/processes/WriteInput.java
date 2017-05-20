package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Process that write to screen.
 */
public class WriteInput extends Process {

  WriteInput(Process parent, int priority, @Nullable Element... resources) {
    super(parent, priority, resources);
  }

  @Override
  public void run() {

  }

}
