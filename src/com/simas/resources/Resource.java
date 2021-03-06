package com.simas.resources;

import com.simas.Log;
import com.simas.Scheduler;
import com.simas.Utils;
import com.simas.processes.Process;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Main resource class. This wraps multiple {@link Element}s of a specific resource.
 * @param <T> element type
 */
public class Resource<T extends Element> {

  public static final Resource<ProgramElement> PROGRAM_IN_MEMORY = new Resource<>(ProgramElement.class);
  public static final Resource<ConsumedElement> MOS_END = new Resource<>(ConsumedElement.class);
  public static final Resource<Element> CHANNEL_3 = new Resource<>(Element.class);
  public static final Resource<Element> CHANNEL_2 = new Resource<>(Element.class);
  public static final Resource<Element> CHANNEL_1 = new Resource<>(Element.class);
  public static final Resource<IOPacket> INPUT_PACKET = new Resource<>(IOPacket.class);
  public static final Resource<IOPacket> OUTPUT_PACKET = new Resource<>(IOPacket.class);
  public static final Resource<DiskPacket> DISK_READ_PACKET = new Resource<>(DiskPacket.class);
  public static final Resource<DiskPacket> DISK_WRITE_PACKET = new Resource<>(DiskPacket.class);
  public static final Resource<Interrupt> INTERRUPT = new Resource<>(Interrupt.class);
  public static final Resource<Element> INTERNAL_MEMORY = new Resource<>(Element.class);
  public static final Resource<Message> CPU = new Resource<>(Message.class);
  public static final Resource<Element> NON_EXISTENT = new Resource<>(Element.class);
  public static final Resource<Element> NO_TASK = new Resource<>(Element.class);
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
   */
  public synchronized void create(Process creator) {
    create(creator, null);
  }

  /**
   * Creates and adds a new element to this resource.
   * Before it is added, the given {@link Element.Modifier} will be called to modify the created element.
   */
  public synchronized void create(Process creator, @Nullable Element.Modifier<T> modifier) {
    if (this != Resource.CPU) Log.d("%s creates %s.", creator, toString());
    // Instantiate
    final T element = Element.instantiate(elementType, this, creator);

    // Modify the element
    if (modifier != null) modifier.modify(element);

    // Validate the element
    element.validate();

    // Add to process created resource list
    creator.createdResources.add(element);

    // Add to resource's element list
    elements.add(element);

    // Notify resource about the newly available element
    notifyAll();
  }

  /**
   * Request an element of this resource.
   * @return any element of this resource
   */
  @NotNull
  public synchronized T request(Process requester) {
    return request(requester, t -> true);
  }

  /**
   * Request a specific element of this resource.
   * @param predicate used to match elements of this resource
   * @return element that matches the given predicate
   */
  @NotNull
  public synchronized T request(Process requester, Predicate<? super T> predicate) {
    Log.v("%s waits for %s.", requester, toString());

    // Fetch first element that matches the given predicate
    Optional<T> optional = elements.stream()
        .filter(predicate)
        .findFirst();

    // In case resource is unavailable requester is blocked
    if (!optional.isPresent()) {
      // Process looses CPU
      requester.dropCPU();

      // Add process to the waiters list
      waitingProcesses.add(requester);

      if (this != CPU) {
        // Block the process
        requester.setState(Process.State.BLOCKED);
        // Schedule execution of another process
        Scheduler.schedule();
      }

      // Wait until a resource element is available
      while (!optional.isPresent()) {
        // Wait for a resource notification
        try {
          wait();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        // Search again
        optional = elements.stream()
            .filter(predicate)
            .findFirst();
      }

      Log.v("%s got %s.", requester, toString());

      // Remove element from list
      final T element = optional.get();
      elements.remove(element);

      // Add element to the requester's list of available resources
      requester.availableResources.add(element);

      // An element is now available, remove process from waiters list
      waitingProcesses.remove(requester);

      if (this != CPU) {
        // Unblock the process
        requester.setState(Process.State.READY);

        // Now we need the CPU again
        requester.requestCPU();
      }

      return element;
    } else {
      // Remove element from list
      final T element = optional.get();
      elements.remove(element);

      // Add element to the requester's list of available resources
      requester.availableResources.add(element);

      Log.v("%s got %s.", requester, toString());

      return element;
    }
  }

  @Override
  public String toString() {
    try {
      for (Field field : Utils.getStaticFields(Resource.class)) {
        if (field.get(null) != this) continue;
        return String.format("Resource %s", field.getName());
      }
    } catch (IllegalAccessException ignored) {}

    throw new IllegalStateException(String.format("Resource %s wasn't found!", getClass().getName()));
  }

}