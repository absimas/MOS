package com.simas.processes;

import com.simas.real_machine.Channel3;
import com.simas.real_machine.Memory;
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

    // Wait for internal memory resource
    Element resource = Resource.INTERNAL_MEMORY.request(this);

    // Read from memory
    final String string = Memory.getInstance().read(packet.internalPosition, packet.size);

    // Free internal memory resource
    resource.free();

    // Wait for 3rd channel resource
    resource = Resource.CHANNEL_3.request(this);

    // Write to 3rd channel
    final Channel3 channel3 = Channel3.getInstance();
    final int pointer = packet.externalPosition / Channel3.POINTER_SHIFT;
    channel3.setPointer(pointer);
    final int position = packet.externalPosition - pointer;
    channel3.write(position, string);

    // Free 3rd channel
    resource.free();

    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = string;
    });
  }

}
