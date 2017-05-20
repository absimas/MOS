package com.simas.processes;

import com.simas.resources.Element;
import com.simas.resources.IOPacket;
import com.simas.resources.Resource;
import com.simas.resources.StringElement;
import com.sun.istack.internal.Nullable;

/**
 * Process that write to screen.
 */
public class WriteInput extends Process {

  static final int PRIORITY = 10;

  WriteInput(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    // Wait for output packet resource
    final IOPacket packet = Resource.OUTPUT_PACKET.request(this);

    // Wait for 2nd channel resource // ToDo element type?
    final StringElement channel2 = Resource.CHANNEL_2.request(this);

    // ToDo write to channel2

    // Free 2nd channel
    channel2.free();

    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = "Writing output done";
    });
  }

}
