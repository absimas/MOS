package com.simas.processes;

import com.simas.Log;
import com.simas.real_machine.Channel2;
import com.simas.real_machine.Memory;
import com.simas.resources.Element;
import com.simas.resources.IOPacket;
import com.simas.resources.Resource;
import com.sun.istack.internal.Nullable;

/**
 * Process that write to screen.
 */
public class WriteOutput extends Process {

  static final int PRIORITY = 10;

  WriteOutput(Process parent, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
  }

  @Override
  public void run() {
    // Wait for output packet resource
    final IOPacket packet = Resource.OUTPUT_PACKET.request(this);

    // Wait for 2nd channel resource
    final Element channel2 = Resource.CHANNEL_2.request(this);

    // Read from memory
    final String output = Memory.getInstance().read(packet.position, packet.size);

    // Write to 2nd channel
    Channel2.write(output);
    Log.v("%s wrote '%s'.", toString(), output);

    // Free 2nd channel
    channel2.free();

    // Send message to packet creator
    Resource.MESSAGE.create(this, element -> {
      element.destination = packet.creator;
      element.message = "Writing output done";
    });
  }

}
