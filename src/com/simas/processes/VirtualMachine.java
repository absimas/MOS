package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * User program executor.
 */
public class VirtualMachine extends Process {

  VirtualMachine(Process parent, int priority, @Nullable Element... resources) {
    super(parent, priority, resources);
  }

  @Override
  public void run() {

  }

}
