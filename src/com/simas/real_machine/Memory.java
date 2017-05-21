package com.simas.real_machine;

import java.util.Arrays;

/**
 * Internal memory. I/O operations require a position.
 */
public class Memory {

  private static final int SIZE = 1000;
  private static char[] MEMORY = new char[SIZE];

  /**
   * Reads a value from memory.
   * @param position starting memory position
   * @param size     value length
   * @return value that's been read
   */
  public static String read(int position, int size) throws IndexOutOfBoundsException {
    if (position + size >= SIZE) {
      throw new IndexOutOfBoundsException("Reading outside the bounds of memory!");
    }

    final char[] read = Arrays.copyOfRange(MEMORY, position, position + size);
    return new String(read);
  }

  /**
   * Write value to memory starting at position.
   * @param position starting memory position
   * @param value    value to be written
   */
  public static void write(int position, String value) throws IndexOutOfBoundsException {
    if (position + value.length() >= SIZE) {
      throw new IndexOutOfBoundsException("Writing beyond the bounds of memory!");
    }

    final char[] chars = value.toCharArray();
    System.arraycopy(chars, 0, MEMORY, position, chars.length);
  }

}
