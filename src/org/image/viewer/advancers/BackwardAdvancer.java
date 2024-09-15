package org.image.viewer.advancers;

import org.image.viewer.core.Logic;
import org.image.viewer.core.NamedImage;

/**
 * Advances to the previous {@code NamedImage} by calling the {@code backward()}
 * method of the underlying {@code Logic} Object.
 * @author EvanStefan
 */
public class BackwardAdvancer implements Advancer {
  private final Logic logic;
  public BackwardAdvancer(Logic logic) {
    this.logic = logic;
  }
  @Override
  public NamedImage advance() {
    return logic.backward();
  }
}
