package com.simas.processes;

import com.simas.resources.DiskPacket;
import com.simas.resources.Element;
import com.simas.resources.Message;
import com.simas.resources.Resource;
import com.simas.resources.StringElement;
import com.sun.istack.internal.Nullable;

/**
 * Process that reads from disk.
 */
public class ReadDisk extends Process {

  static final int PRIORITY = 10;

  ReadDisk(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    // Wait for disk read resource
    final DiskPacket packet = Resource.DISK_READ_PACKET.request(this);

    // Wait for 3rd channel resource // ToDo element type?
    final StringElement channel3 = Resource.CHANNEL_3.request(this);

    // ToDo read from channel3

    // Free 3rd channel
    channel3.free();

    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = "Reading done";
    });
  }

}
