package com.simas.processes;

import com.simas.Log;
import com.simas.real_machine.Channel1;
import com.simas.real_machine.Channel3;
import com.simas.real_machine.Memory;
import com.simas.resources.Element;
import com.simas.resources.IOPacket;
import com.simas.resources.Resource;
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
    super.run();

    // Wait for input packet resource
    final IOPacket packet = Resource.INPUT_PACKET.request(this);

    // Wait for 1st channel resource
    Element resource = Resource.CHANNEL_1.request(this);

    // Read from 1st channel
    final String input = Channel1.read(packet.size);
    Log.v("%s read '%s' when asked for %d.", toString(), input, packet.size);

    // Free 1st channel
    resource.free();

    // Wait for internal memory resource
    resource = Resource.INTERNAL_MEMORY.request(this);

    // Write to memory
    Memory.getInstance().write(packet.position, input);

    // Free internal memory resource
    resource.free();


    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = input;
    });
  }

}
