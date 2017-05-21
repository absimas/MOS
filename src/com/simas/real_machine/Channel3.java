package com.simas.real_machine;

/**
 * Disk for I/O.
 */
public class Channel3 extends Storage<Channel3> {

  /**
   * Memory size: 10000 words.
   */
  private static final int SIZE = 10000 * RealMachine.WORD_SIZE;
  private static final Channel3 INSTANCE = new Channel3();

  /**
   * Private c-tor to prevent other instances than {@link #INSTANCE}.
   */
  private Channel3() {}

  @Override
  protected int getSize() {
    return SIZE;
  }

  public static Channel3 getInstance() {
    return INSTANCE;
  }

}