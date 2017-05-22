package com.simas.real_machine;

import java.util.Arrays;

/**
 * Created by Simas on 2017 May 21.
 */
public abstract class Storage<T extends Storage> {

  public final char[] STORAGE = new char[getSize()];
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
   * @param position absolute position
   * @param size     value length
   * @return value that's been read
   */
  public String read(int position, int size) throws IndexOutOfBoundsException {
    if (getPointerPosition() + getWordPosition(position) + size >= getSize()) {
      throw new IndexOutOfBoundsException("Reading outside the bounds of storage!");
    }

    final char[] read = Arrays.copyOfRange(STORAGE, getPointerPosition() + getWordPosition(position), getPointerPosition() + getWordPosition(position) + size);
    return new String(read);
  }

  /**
   * Write value to memory starting at position.
   * @param position absolute position
   * @param value    value to be written
   */
  public void write(int position, String value) throws IndexOutOfBoundsException {
    if (getPointerPosition() + getWordPosition(position) + value.length() >= getSize()) {
      throw new IndexOutOfBoundsException("Writing beyond the bounds of storage!");
    }

    final char[] chars = value.toCharArray();
    System.arraycopy(chars, 0, STORAGE, getPointerPosition() + getWordPosition(position), chars.length);
  }

  private static int getWordPosition(int position) {
    return position * RealMachine.WORD_SIZE;
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

  private int getPointerPosition() {
    return pointer * POINTER_SHIFT;
  }

}