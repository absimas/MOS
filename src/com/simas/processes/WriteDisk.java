package com.simas.processes;

import com.simas.resources.DiskPacket;
import com.simas.resources.Element;
import com.simas.resources.Resource;
import com.simas.resources.StringElement;
import com.sun.istack.internal.Nullable;

/**
 * Process that writes to disk.
 */
public class WriteDisk extends Process {

  static final int PRIORITY = 10;

  WriteDisk(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    // Wait for disk write resource
    final DiskPacket packet = Resource.DISK_WRITE_PACKET.request(this);

    // Wait for 3rd channel resource // ToDo element type?
    final StringElement channel3 = Resource.CHANNEL_3.request(this);

    // ToDo write to channel3

    // Free 3rd channel
    channel3.free();

    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = "Writing done";
    });
  }

}
