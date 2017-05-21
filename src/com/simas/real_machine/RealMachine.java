package com.simas.real_machine;

import com.simas.exceptions.TIException;

/**
 * Created by Simas on 2017 May 21.
 */
public class RealMachine {

  public static final int MAX_TI = 10;

  /**
   * Temporary value of 1 word (5 symbols).
   */
  public static String TMP;
  /**
   * Instruction counter (represents the current command index).
   */
  public static int IC;
  /**
   * Comparison bit flag.
   * 0 - EQUAL, 1 - LESS, 2 - MORE.
   */
  public static int C;
  /**
   * Timer interrupt value (max value: {@link #MAX_TI}.
   */
  private static int TI;

  /**
   * Increments TI.
   * @throws TIException when TI reaches {@link #MAX_TI}
   */
  public static void incrementTI() throws TIException {
    if (++TI < MAX_TI) return;
    throw new TIException();
  }

}
