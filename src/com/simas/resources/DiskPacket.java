package com.simas.resources;

import com.simas.processes.Process;

/**
 * Disk Write/Read packet.
 * Writing: internal -> external
 * Reading: external -> internal
 */
public class DiskPacket extends Element<DiskPacket> {

  /**
   * External memory source/destination position.
   */
  public int externalPosition;
  /**
   * Internal memory source/destination position
   */
  public int internalPosition;
  /**
   * Size of the transfer.
   */
  public int size;

  /**
   * Required constructor.
   */
  DiskPacket(Resource<DiskPacket> resource, Process creator) {
    super(resource, creator);
  }

  @Override
  public String toString() {
    return String.format("%s external: %d, internal: %d from resource %s", getClass().getName(), externalPosition, internalPosition, resource);
  }

}
