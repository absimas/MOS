package com.simas.resources;

import com.simas.Log;
import com.simas.processes.Process;
import com.sun.istack.internal.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Main resource class. This wraps multiple {@link Element}s of a specific resource.
 * @param <T> element type
 */
public class Resource<T extends Element> {

  public static final Resource<StringElement> PROGRAM_IN_MEMORY = new Resource<>(StringElement.class);
  public static final Resource<StringElement> CHANNEL_3 = new Resource<>(StringElement.class);
  public static final Resource<StringElement> CHANNEL_2 = new Resource<>(StringElement.class);
  public static final Resource<StringElement> CHANNEL_1 = new Resource<>(StringElement.class);
  public static final Resource<StringElement> INPUT_PACKET = new Resource<>(StringElement.class);
  public static final Resource<StringElement> OUTPUT_PACKET = new Resource<>(StringElement.class);
  public static final Resource<StringElement> DISK_WRITE_PACKET = new Resource<>(StringElement.class);
  public static final Resource<StringElement> INTERRUPT = new Resource<>(StringElement.class);
  public static final Resource<StringElement> INTERNAL_MEMORY = new Resource<>(StringElement.class);
  public static final Resource<StringElement> CPU = new Resource<>(StringElement.class);
  public static final Resource<StringElement> NON_EXISTENT = new Resource<>(StringElement.class);
  public static final Resource<StringElement> NO_TASK = new Resource<>(StringElement.class);
  public static final Resource<Message> MESSAGE = new Resource<>(Message.class);

  /**
   * For internal use only.
   */
  private static final List<Resource> RESOURCES = new ArrayList<>();

  /**
   * Elements of this resource.
   */
  final List<T> elements = new ArrayList<>();
  /**
   * List of processes waiting for this resource.
   */
  private final List<Process> waitingProcesses = new ArrayList<>();

  private final Class<T> elementType;

  /**
   * Private c-tor
   */
  private Resource(Class<T> elementType) {
    this.elementType = elementType;
  }

  /**
   * Creates and adds a new element to this resource.
   * Before it is added, the given {@link Element.Modifier} will be called to modify the created element.
   */
  public synchronized void create(Process creator, Element.Modifier<T> modifier) {
    // Instantiate
    final T element = Element.instantiate(elementType, this, creator);

    // Modify the element
    modifier.modify(element);

    // Add to process created resource list
    creator.createdResources.add(element);

    // Add to resource's element list
    elements.add(element);

    // Notify resource about the newly available element
    notify();
  }

  /**
   * Request an element of this resource.
   * @return any element of this resource
   */
  @NotNull
  public synchronized T request(Process requester) throws InterruptedException {
    return request(requester, t -> true);
  }

  /**
   * Request a specific element of this resource.
   * @param predicate used to match elements of this resource
   * @return element that matches the given predicate
   */
  @NotNull
  public synchronized T request(Process requester, Predicate<? super T> predicate) throws InterruptedException {
    // Fetch first element that matches the given predicate
    Optional<T> optional = elements.stream()
        .filter(predicate)
        .findFirst();

    // In case resource is unavailable, switch requester's state and wait for it
    if (!optional.isPresent()) {
      // Block the process
      requester.setState(Process.State.BLOCKED);

      // Add process to the waiters list
      waitingProcesses.add(requester);

      // Wait until a resource element is available
      while (!optional.isPresent()) {
        Log.v("%s asked for an element of %s but it wasn't found.", requester, toString());
        // Wait for a resource notification
        wait();

        // Search again
        optional = elements.stream()
            .filter(predicate)
            .findFirst();
      }

      // An element is now available, remove process from waiters list
      waitingProcesses.remove(requester);

      // Unblock the process
      requester.setState(Process.State.READY);
    }

    return optional.get();
  }

}