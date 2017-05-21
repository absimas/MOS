package com.simas.resources;

import com.simas.Utils;
import com.simas.processes.Process;
import com.simas.real_machine.Command;
import com.sun.istack.internal.NotNull;

/**
 * Interrupt message that announces a specific interruption within the creator.
 */
public class Interrupt extends Message<Interrupt> {

  public enum Type {
    GD, PD, WD, RD, SD, TI, PROGRAM_INTERRUPT, HALT
  }

  @NotNull
  public Type type;
  @NotNull
  public Command failingCommand;

  /**
   * Required constructor.
   */
  Interrupt(Resource<Interrupt> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("Interrupt %s when executing %s in %s", type, failingCommand, creator);
  }

  @Override
  protected void validate() throws IllegalStateException {
    super.validate();
    if (type == null) {
      throw new IllegalStateException("Interrupt message must have a type!");
    } else if (failingCommand == null) {
      throw new IllegalStateException("Interrupt message's failing command isn't set!");
    }
  }

}
