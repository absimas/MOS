package com.simas;

/**
 * Created by Simas on 2017 May 20.
 */
public class Log {

  /**
   * Private c-tor
   */
  private Log() {}

  /**
   * Verbose
   */
  public static void v(String string, Object... format) {
    System.out.println(String.format(string, format));
  }

  /**
   * Debug
   */
  public static void d(String string, Object... format) {
    System.out.println(String.format(string, format));
  }

  /**
   * Warning
   */
  public static void w(String string, Object... format) {
    System.out.println(String.format(string, format));
  }

  /**
   * Error
   */
  public static void e(String string, Object... format) {
    System.err.println(String.format(string, format));
  }

}
