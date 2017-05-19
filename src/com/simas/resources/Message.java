package com.simas.resources;

import com.simas.processes.Process;

/**
 * Message element with a specific process that will act as a destination.
 */
public class Message extends Element<Message, Resource<Message>> {

  public Process destination;
  public String message;

  /**
   * Required constructor.
   */
  Message(Resource<Message> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public void free() {
    // Messages are consumed after each request
  }

}
