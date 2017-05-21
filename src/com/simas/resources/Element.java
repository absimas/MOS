package com.simas.resources;

import com.simas.Log;
import com.simas.processes.Process;
import java.lang.reflect.InvocationTargetException;

/**
 * Resource element is a wrapper on different types of elements.
 * @see Message
 * @see StringElement
 */
public class Element<T extends Element> {

  public final Resource<T> resource;
  public final Process creator;

  /**
   * Required constructor.
   */
  Element(Resource<T> resource, Process creator) {
    this.resource = resource;
    this.creator = creator;
  }

  /**
   * Free this element.
   */
  public void free() {
    // Add to the wrapping resource class's element list
    resource.elements.add((T) this);

    // Remove from owners
    removeFromAvailable();

    // Notify wrapping resource about an available element
    synchronized (resource) {
      resource.notify();
    }
  }

  /**
   * Remove this element in case it existed in the resource element list.
   */
  public void destroy() {
    // Remove from resource list
    resource.elements.remove(this);

    // Remove from creator
    creator.createdResources.remove(this);

    // Remove from owners
    removeFromAvailable();
  }

  /**
   * Removes this element from available resource lists ({@link Process#availableResources}) in all processes ({@link Process#PROCESSES}).
   */
  private void removeFromAvailable() {
    Process.PROCESSES.forEach(process -> process.availableResources.remove(this));
  }

  @Override
  public String toString() {
    return String.format("Basic element of %s created by %s.", resource, creator);
  }

  public static <T extends Element> T instantiate(Class<T> type, Resource<T> resource, Process creator) {
    try {
      return type.getConstructor(resource.getClass(), Process.class).newInstance(resource, creator);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      Log.e("Resource element %s couldn't be instantiated! Is constructor (Resource, Process) missing?", type);
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public interface Modifier<T extends Element> {
    void modify(T element);
  }

  /**
   * Validate this element after creation.
   * @throws IllegalStateException if the element is invalid
   */
  protected void validate() throws IllegalStateException {
    if (resource == null || creator == null) {
      throw new IllegalStateException("Wrapping resource and creator must be set for a resource element!");
    }
  }

}
