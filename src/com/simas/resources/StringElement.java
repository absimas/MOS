package com.simas.resources;

import com.simas.processes.Process;

/**
 * Simple string wrapping element.
 */
public class StringElement extends Element<StringElement> {

  public String string;

  /**
   * Required constructor.
   */
  StringElement(Resource<StringElement> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("%s with string %s from resource %s", getClass().getName(), string, resource);
  }

}
