package org.image.viewer.advancers;

import org.image.viewer.core.Logic;
import org.image.viewer.core.NamedImage;

/**
 * Advances to the next {@code NamedImage} by calling the {@code forward()} 
 * method of the underlying {@code Logic} Object.
 * @author EvanStefan
 */
public class ForwardAdvancer implements Advancer {
  private final Logic logic;
  public ForwardAdvancer(Logic logic) {
    this.logic = logic;
  }
  @Override
  public NamedImage advance() {
    return logic.forward();
  }
}
