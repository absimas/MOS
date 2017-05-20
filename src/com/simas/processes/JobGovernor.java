package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Program governor to overlook the work of a single {@link VirtualMachine}.
 */
public class JobGovernor extends Process {

  JobGovernor(Process parent, int priority, @Nullable Element... resources) {
    super(parent, priority, resources);
  }

  @Override
  public void run() {

  }

}
