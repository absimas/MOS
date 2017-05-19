package com.simas.resources;

import com.simas.Log;
import com.simas.processes.Process;

import java.lang.reflect.InvocationTargetException;

/**
 * Resource element is a wrapper on different types of elements.
 * @see Message
 * @see StringElement
 */
public abstract class Element<T extends Element, R extends Resource<T>> {

  protected final R resource;
  protected final Process creator;

  /**
   * Required constructor.
   */
  Element(R resource, Process creator) {
    this.resource = resource;
    this.creator = creator;
  }

  /**
   * Free this element.
   */
  public void free() {
    // Add to the wrapping resource class's element list
    resource.elements.add((T) this);

    // Notify wrapping resource about an available element
    synchronized (resource) {
      resource.notify();
    }
    // ToDo notify processes that have this resource as available?
  }

  /**
   * Remove this element in case it existed in the resource element list.
   */
  public void destroy() {
    resource.elements.remove(this);
  }

  public static <T extends Element> T instantiate(Class<T> type, Resource<T> resource, Process creator) {
    try {
      return type.getConstructor(Resource.class, Process.class).newInstance(resource, creator);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      Log.e("Resource element %s couldn't be instantiated! Is constructor (Resource, Process) missing?", type);
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public interface Modifier<T extends Element> {
    void modify(T element);
  }

}
