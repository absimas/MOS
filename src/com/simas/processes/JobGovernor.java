package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Program governor to overlook the work of a single {@link VirtualMachine}.
 */
public class JobGovernor extends Process {

  static final int PRIORITY = VirtualMachine.PRIORITY + 1;

  JobGovernor(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {

  }

}
