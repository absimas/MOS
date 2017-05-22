package com.simas.resources;

import com.simas.processes.Process;
import com.simas.processes.VirtualMachine;
import com.simas.real_machine.Command;
import com.sun.istack.internal.NotNull;

/**
 * Interrupt message that announces a specific interruption within the creator.
 */
public class Interrupt extends Message<Interrupt> {

  public enum Type {
    GD, PD, WD, RD, SD, TI, PROGRAM_INTERRUPT, HALT
  }

  /**
   * Interruption type.
   */
  @NotNull
  public Type type;
  /**
   * Command whose execution caused the interruption.
   */
  @NotNull
  public Command command;
  /**
   * VM that tried executing the command.
   */
  @NotNull
  public VirtualMachine vm;

  /**
   * Required constructor.
   */
  public Interrupt(Resource<Interrupt> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("Interrupt %s when executing %s in %s", type, command, creator);
  }

  @Override
  protected void validate() throws IllegalStateException {
    super.validate();
    if (type == null) {
      throw new IllegalStateException("Interrupt message must have a type!");
    } else if (command == null) {
      throw new IllegalStateException("Interrupt message command must be set!");
    } else if (vm == null) {
      throw new IllegalStateException("Interrupt message vm must be set!");
    }
  }

}
