package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * Input manager.
 */
public class CLI extends Process {

  static final int PRIORITY = 10;

  CLI(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {

  }

}
