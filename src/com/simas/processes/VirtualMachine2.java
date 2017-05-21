package com.simas.processes;

import com.simas.exceptions.TIException;
import com.simas.real_machine.RealMachine;
import com.simas.resources.Element;
import com.sun.istack.internal.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * User program executor 2.
 */
public class VirtualMachine2 extends VirtualMachine {

  public static final String NAME = "VM2";
  /**
   * Command list. Program: fibonacci less than 5.
   */
  public static final List<String> PROGRAM = Arrays.asList(
      "PD013",
      "CR013",
      "AD012",
      "CP014",
      "JM015",
      "CM011",
      "CR013",
      "CM012",
      "CR011",
      "CM013",
      "JP000",
      "00000",
      "00000",
      "00001",
      "00005",
      "HALT "
  );

  private static final int INTERNAL_MEMORY_POSITION = 200;

  VirtualMachine2(Process parent, @Nullable Element... resources) {
    super(parent, INTERNAL_MEMORY_POSITION, resources);
  }

  @Override
  protected List<String> getCommands() {
    return PROGRAM;
  }

}
