package com.simas.processes;

import com.simas.resources.Element;
import com.simas.resources.IOPacket;
import com.simas.resources.Resource;
import com.simas.resources.StringElement;
import com.sun.istack.internal.Nullable;

/**
 * Process that reads from keyboard
 */
public class ReadInput extends Process {

  static final int PRIORITY = 10;

  ReadInput(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    // Wait for input packet resource
    final IOPacket packet = Resource.INPUT_PACKET.request(this);

    // Wait for 1st channel resource // ToDo element type?
    final StringElement channel1 = Resource.CHANNEL_1.request(this);

    // ToDo read from channel1

    // Free 1st channel
    channel1.free();

    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = "Reading input done";
    });
  }

}
