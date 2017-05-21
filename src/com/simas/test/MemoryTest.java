package com.simas.test;

import com.simas.real_machine.Memory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simas on 2017 Mar 14.
 */
public class MemoryTest {

  @Test
  public void writing() {
    final String output = "hello";
    Memory.write(50, output);
    final String read = Memory.read(50, output.length());
    Assert.assertEquals("Input wasn't written to memory!", output, read);
  }

  @Test
  public void readingNothing() {
    final String read = Memory.read(999, 0);
    Assert.assertEquals("", read);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void writingOOB() {
    Memory.write(995, "some_string");
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void writingOOB2() {
    Memory.write(1002, "");
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void readingOOB() {
    Memory.read(995, 6);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void readingOOB2() {
    Memory.read(1000, 1);
  }

}
