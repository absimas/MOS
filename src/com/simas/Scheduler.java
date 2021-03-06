package com.simas;

import com.simas.processes.Process;
import com.simas.processes.Root;
import com.simas.resources.Resource;
import com.sun.istack.internal.NotNull;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Simas on 2017 May 21.
 */
public class Scheduler {

  private static Runnable listener;

  /**
   * Last executed process.
   * Initially - {@link com.simas.processes.Root}.
   * Changed after each {@link #schedule()} call.
   */
  @NotNull
  public static Process currentProcess;

  public static synchronized void schedule() {
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    final Process oldProcess = currentProcess;
    // Current process loses CPU
    currentProcess.dropCPU();

    // Save registers
    currentProcess.save();

    // Running -> Ready
    if (currentProcess.getState() == Process.State.RUNNING) {
      currentProcess.setState(Process.State.READY);
    }

    final Optional<Process> optional = Process.PROCESSES.stream()
        .filter(process -> process.getState() == Process.State.READY)
        .sorted((o1, o2) -> o2.priority - o1.priority)
        .findFirst();

    // No ready processes
    if (!optional.isPresent()) {
      Log.e("Special situation! No ready processes available...");
      Log.e("Process list: " + Arrays.toString(Process.PROCESSES.toArray()));
      System.exit(1);
      return;
    }

    // Select the found process
    currentProcess = optional.get();

    // Move the found process to the end of the process list
    Process.PROCESSES.remove(currentProcess);
    Process.PROCESSES.add(currentProcess);

    // Restore process registers
    currentProcess.restore();

    // ? -> Running
    currentProcess.setState(Process.State.RUNNING);
    if (listener != null) listener.run();
    sleep();

    // Newly selected process gets the CPU
    Log.v("CPU %s -> %s", oldProcess, currentProcess);
    Resource.CPU.create(Root.instance, element -> element.destination = currentProcess);
  }

  /**
   * Resumes the scheduler class.
   */
  public static synchronized void resume() {
    Scheduler.class.notify();
  }

  /**
   * Sleeps the scheduler class.
   */
  public static synchronized void sleep() {
    try {
      Scheduler.class.wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Set listener that's called each time the Scheduler is about to go to sleep.
   * @see #sleep()
   * @see #resume()
   * @param runnable runnable that will be run when the event occurs
   */
  public static void setListener(Runnable runnable) {
    Scheduler.listener = runnable;
  }

}
