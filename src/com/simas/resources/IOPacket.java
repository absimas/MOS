package com.simas.resources;

import com.simas.processes.Process;

/**
 * I/O packet
 */
public class IOPacket extends ConsumedElement<IOPacket> {

  /**
   * Source/Destination position in internal memory.
   */
  public int position;
  /**
   * How many symbols will be Written/Read.
   */
  public int size;

  /**
   * Required constructor.
   */
  public IOPacket(Resource<IOPacket> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("%s position: %d, size: %d from resource %s", getClass().getName(), position, size, resource);
  }

}
