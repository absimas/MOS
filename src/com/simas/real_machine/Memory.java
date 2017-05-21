package com.simas.real_machine;

/**
 * Internal memory. I/O operations require a position.
 */
public class Memory extends Storage {

  private static final Memory INSTANCE = new Memory();
  /**
   * Memory size: 1000 words.
   */
  private static final int SIZE = 1000 * RealMachine.WORD_SIZE;

  /**
   * Private c-tor to prevent other instances than {@link #INSTANCE}.
   */
  private Memory() {}

  @Override
  protected int getSize() {
    return SIZE;
  }

  public static Storage getInstance() {
    return INSTANCE;
  }

}