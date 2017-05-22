package com.simas.resources;

import com.simas.processes.Process;

/**
 * An element that cannot be freed. In other words this is consumed after being returned by {@link Resource#request}.
 */
public class ConsumedElement<T extends Element> extends Element <T> {

  /**
   * Required constructor.
   */
  public ConsumedElement(Resource<T> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public void free() {
    // Consumed
  }

}
