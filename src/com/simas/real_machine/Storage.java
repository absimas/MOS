package com.simas.real_machine;

import java.util.Arrays;

/**
 * Created by Simas on 2017 May 21.
 */
public abstract class Storage<T extends Storage> {

  private final char[] STORAGE = new char[getSize()];
  public static final int POINTER_SHIFT = 100;

  /**
   * Position shift. Each shift equals to {@link #POINTER_SHIFT} positions
   */
  private int pointer;

  /**
   * Return size of the storage.
   */
  protected abstract int getSize();

  /**
   * Reads a value from memory.
   *
   * @param position starting memory position
   * @param size     value length
   * @return value that's been read
   */
  public String read(int position, int size) throws IndexOutOfBoundsException {
    if (pointer * POINTER_SHIFT + position + size >= getSize()) {
      throw new IndexOutOfBoundsException("Reading outside the bounds of storage!");
    }

    final char[] read = Arrays.copyOfRange(STORAGE, pointer * POINTER_SHIFT + position, pointer * POINTER_SHIFT + position + size);
    return new String(read);
  }

  /**
   * Write value to memory starting at position.
   *
   * @param position starting memory position
   * @param value    value to be written
   */
  public void write(int position, String value) throws IndexOutOfBoundsException {
    if (pointer * POINTER_SHIFT + position + value.length() >= getSize()) {
      throw new IndexOutOfBoundsException("Writing beyond the bounds of storage!");
    }

    final char[] chars = value.toCharArray();
    System.arraycopy(chars, 0, STORAGE, pointer * POINTER_SHIFT + position, chars.length);
  }

  /**
   * Position shift. Will affect {@link #read} and {@link #write} calls.
   * @see #pointer
   */
  public void setPointer(int pointer) {
    this.pointer = pointer;
  }

  public int getPointer() {
    return pointer;
  }

}