package org.image.viewer.advancers;

import org.image.viewer.core.NamedImage;

/**
 * Implementing classes must provide a way to advance. Advancing updates the
 * underlying state to point to the next {@code NamedImage} and returns it.
 * @author EvanStefan
 */
public interface Advancer {
  public NamedImage advance();
}
