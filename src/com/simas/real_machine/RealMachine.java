package com.simas.real_machine;

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
  public static int TI;

}
