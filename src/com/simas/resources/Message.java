package com.simas.resources;

import com.simas.processes.Process;

/**
 * Message element with a specific process that will act as a destination.
 */
public class Message<T extends Message> extends ConsumedElement<T> {

  public Process destination;
  public String message;

  /**
   * Required constructor.
   */
  public Message(Resource<T> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("Message '%s' from %s to %s", message, creator, destination);
  }

}
