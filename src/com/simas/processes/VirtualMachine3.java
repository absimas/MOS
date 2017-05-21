package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * User program executor 3.
 */
public class VirtualMachine3 extends VirtualMachine {

  public static final String NAME = "VM3";
  private static final int INTERNAL_MEMORY_POSITION = 30;

  VirtualMachine3(Process parent, @Nullable Element... resources) {
    super(parent, INTERNAL_MEMORY_POSITION, resources);
  }

  /**
   * Executes program specific to this VM.
   */
  protected void execute() {

  }

}
