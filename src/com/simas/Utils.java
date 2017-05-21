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

}
