package com.simas.processes;

import com.simas.real_machine.Channel3;
import com.simas.real_machine.Memory;
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

    // Wait for 3rd channel resource
    final Element resource = Resource.CHANNEL_3.request(this);

    // Read from 3rd channel
    final Channel3 channel3 = Channel3.getInstance();
    final int pointer = packet.externalPosition / Channel3.POINTER_SHIFT;
    channel3.setPointer(pointer);
    final int position = packet.externalPosition - pointer;
    final String string = channel3.read(position, packet.size);

    // Write to memory
    Memory.getInstance().write(packet.internalPosition, string);

    // Free 3rd channel
    resource.free();

    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = string;
    });
  }

}
