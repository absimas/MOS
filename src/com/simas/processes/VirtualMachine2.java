package com.simas.processes;

import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

/**
 * User program executor 2.
 */
public class VirtualMachine2 extends VirtualMachine {

  public static final String NAME = "VM2";

  VirtualMachine2(Process parent, @Nullable Element... resources) {
    super(parent, resources);
  }

  /**
   * Executes program specific to this VM.
   */
  protected void execute() {

  }

}
