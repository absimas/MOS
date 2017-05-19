package com.simas.resources;

import com.simas.processes.Process;

/**
 * Simple string wrapping element.
 */
public class StringElement extends Element<StringElement, Resource<StringElement>> {

  public String string;

  /**
   * Required constructor.
   */
  StringElement(Resource<StringElement> resource, Process creator) {
    super(resource, creator);
  }

}
