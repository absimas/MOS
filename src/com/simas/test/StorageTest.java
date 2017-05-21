package com.simas.test;

import com.simas.real_machine.Channel3;
import com.simas.real_machine.Memory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Simas on 2017 Mar 14.
 */
public class StorageTest {

  @Test
  public void writing() {
    final String output = "hello";
    Memory.getInstance().write(50, output);
    final String read = Memory.getInstance().read(50, output.length());
    Assert.assertEquals("Input wasn't written to memory!", output, read);
  }

  @Test
  public void readingNothing() {
    final String read = Memory.getInstance().read(999, 0);
    Assert.assertEquals("", read);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void writingOOB() {
    Memory.getInstance().write(995, "some_string");
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void writingOOB2() {
    Memory.getInstance().write(1002, "");
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void readingOOB() {
    Memory.getInstance().read(995, 6);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void readingOOB2() {
    Memory.getInstance().read(1000, 1);
  }

  @Test
  public void pointedWriting() {
    final String output = "hello";
    final Channel3 channel3 = Channel3.getInstance();
    // Write
    channel3.setPointer(5);            // Position at 5xx
    channel3.write(45, output); // Writing starts at 5 * 100 + 45 = 545

    channel3.setPointer(4);            // Position at 4xx

    // Read
    channel3.setPointer(5);           // Position at 5xx
    final String read = channel3.read(45, output.length());

    Assert.assertEquals("Input wasn't written to memory!", output, read);
  }

}
