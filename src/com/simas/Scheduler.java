package com.simas;

import com.simas.processes.Process;
import com.simas.processes.Root;
import com.simas.resources.Resource;
import com.sun.istack.internal.NotNull;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Created by Simas on 2017 May 21.
 */
public class Scheduler {

  /**
   * Last executed process.
   * Initially - {@link com.simas.processes.Root}.
   * Changed after each {@link #schedule()} call.
   */
  @NotNull
  public static Process currentProcess;

  public static synchronized void schedule() {
    // Current process loses CPU
    currentProcess.dropCPU();

    // Save registers
    currentProcess.save();

    // Running -> Ready
    if (currentProcess.getState() == Process.State.RUNNING) {
      currentProcess.setState(Process.State.READY);
    }

    final Stream<Process> readyProcesses = Process.PROCESSES.stream()
        .filter(process -> process.getState() == Process.State.READY);

    // No ready processes
    if (readyProcesses.count() == 0) {
      Log.e("Special situation! No ready resources available...");
      System.exit(1);
      return;
    }

    // Fetch first, highest priority process
    currentProcess = readyProcesses
        .sorted(Comparator.comparingInt(o -> o.priority))
        .findFirst()
        .get(); // Shouldn't be a warning, we've just checked the size

    // Restore process registers
    currentProcess.restore();

    // ? -> Running
    currentProcess.setState(Process.State.RUNNING);

    // Newly selected process gets the CPU
    Resource.CPU.create(Root.instance, element -> element.destination = currentProcess);
  }

  public static synchronized void resume() {
    Scheduler.class.notify();
  }

  public static synchronized void sleep() {
    try {
      Scheduler.class.wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
