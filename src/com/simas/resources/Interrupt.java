package com.simas.resources;

import com.simas.Utils;
import com.simas.processes.Process;
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
  public String failingCommand;

  /**
   * Required constructor.
   */
  Interrupt(Resource<Interrupt> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("Interrupt %s from %s", type, creator);
  }

  @Override
  protected void validate() throws IllegalStateException {
    super.validate();
    if (type == null) {
      throw new IllegalStateException("Interrupt message must have a type!");
    } else if (Utils.isEmpty(failingCommand)) {
      throw new IllegalStateException(String.format("Interrupt message's failing command is empty: '%s'!", failingCommand));
    }
  }

}
