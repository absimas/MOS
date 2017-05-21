package com.simas;

import com.simas.resources.Resource;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simas on 2017 May 20.
 */
public class Utils {

  /**
   * Private c-tor
   */
  private Utils() {}

  public static List<Field> getStaticFields(Class type) {
    final List<Field> fields = new ArrayList<>();
    for (Field field : type.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers())) continue;
      fields.add(field);
    }

    return fields;
  }

  /**
   * Check if a string is empty or null.
   */
  public static boolean isEmpty(@Nullable String string) {
    return string == null || string.isEmpty();
  }

  /**
   * Precedes the given string with zeroes until the wanted size is met.
   * Note that if the given string is longer, it will be trimmed to meet the size and no zeroes will be added.
   * E.g.
   * <code>
   *   precedeZeroes("abc", 5);
   * </code>
   * will return 00abc.
   * @param string  string that will be preceded by zeroes
   * @param size    total size of the resulting string
   * @return string preceded with zeroes to meet the wanted size.
   */
  public static String precedeZeroes(String string, int size) {
    string = "00000000000000" + string;
    return string.substring(string.length()-size);
  }

  /**
   * @see #precedeZeroes(String, int)
   */
  public static String precedeZeroes(int integer, int size) {
    return precedeZeroes(String.valueOf(integer), size);
  }

}
