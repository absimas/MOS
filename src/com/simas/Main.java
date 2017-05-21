package com.simas;

import com.simas.processes.Process;
import com.simas.processes.Root;
import com.simas.resources.Message;
import com.simas.resources.Resource;

public class Main {



  public static void main(String[] args) throws InterruptedException {
    final Root root = new Root();
    Scheduler.currentProcess = root;
    Scheduler.schedule();
  }

  /**
   * How processes A and B communicate with a message resource.
   */
  private static void abCommunication() throws InterruptedException {
    // Dummy processes
    final Process a = null, b = null;

    // b waits for a message // Blocking call
    final Message message = Resource.MESSAGE.request(b, element -> (element.destination == b));

    // a creates a message for b // Will unblock the previous wait
    Resource.MESSAGE.create(a, element -> element.destination = b);

    // b frees the message // This is unnecessary because messages are consumed after being returned by a request
    message.free();
  }

}
