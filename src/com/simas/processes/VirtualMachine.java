package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * User program executor.
 */
public class VirtualMachine extends Process {

  static final int PRIORITY = 10;

  VirtualMachine(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {

  }

}
