package com.simas.real_machine;

import com.sun.istack.internal.NotNull;

/**
 * Keyboard for input.
 */
public class Channel1 {

  private static Runnable listener;

  @NotNull
  private static String buffer = "";

  /**
   * Read the given length string from buffer.
   * The string is consumed so it's no longer available in the buffer.
   *
   * If the buffer is too small, this method will wait until it is and this class is notified.
   * @param size requested size
   */
  @NotNull
  public synchronized static String read(int size) {
    // Wait for a buffer with an adequate length
    while (buffer.length() < size) {
      try {
        Channel1.class.wait();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    final String string = buffer.substring(0, size);
    buffer = buffer.substring(size, buffer.length());

    if (listener != null) listener.run();

    return string;
  }

  public synchronized static void setBuffer(@NotNull String string) {
    buffer = string;
    Channel1.class.notify();
  }

  public static String getBuffer() {
    return buffer;
  }

  /**
   * Set listener that's called each time {@link #read(int)} is called and a part of {@link #buffer} is consumed.
   * @see #read
   * @param runnable runnable that will be run when the event occurs
   */
  public static void setListener(Runnable runnable) {
    Channel1.listener = runnable;
  }

}
