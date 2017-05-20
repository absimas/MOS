package com.simas.processes;

/**
 * The first created process.
 */
public class Root extends Process {

  public Root(int priority) {
    super(null, priority);

    // Prevent multiple roots
    if (PROCESSES.stream().anyMatch(process -> process instanceof Root)) {
      throw new IllegalStateException("Root process was already created!");
    }
  }

  @Override
  public void run() {
    final MainProc proc = new MainProc(this, 1);
  }

}
