package com.simas.processes;

import com.simas.resources.Element;
import com.simas.resources.ProgramElement;
import com.simas.resources.Resource;
import com.simas.resources.StringElement;
import com.sun.istack.internal.Nullable;

/**
 * User program executor 1.
 */
public class VirtualMachine1 extends VirtualMachine {

  public static final String NAME = "VM1";

  VirtualMachine1(Process parent, @Nullable Element... resources) {
    super(parent, resources);
  }

  /**
   * Executes program specific to this VM.
   */
  protected void execute() {
    int a = 5;
    int b = 0;
    int c = a / b;
    int d = 2;
  }

}
