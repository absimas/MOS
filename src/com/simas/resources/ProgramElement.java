package com.simas.resources;

import com.simas.processes.Process;

/**
 * {@link Resource#PROGRAM_IN_MEMORY} element.
 */
public class ProgramElement extends Element<ProgramElement> {

  public String program;
  public int duration;

  /**
   * Required constructor.
   */
  ProgramElement(Resource<ProgramElement> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("%s with duration %d from resource %s", getClass().getName(), duration, resource);
  }

}
