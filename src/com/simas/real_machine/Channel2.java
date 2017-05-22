package com.simas.real_machine;

/**
 * Screen for output.
 */
public class Channel2 {

  private static String output;

  public static void write(String output) {
    Channel2.output = output;
  }

  public static String getOutput() {
    return output;
  }

}
