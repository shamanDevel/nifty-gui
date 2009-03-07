package de.lessvoid.nifty.examples.multiplayer;

import java.util.Properties;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.ControllerEventListener;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;

/**
 * A BoxThing.
 * @author void
 */
public class MultiplayerPanelControl implements Controller {
  private Nifty nifty;
  private Screen screen;
  private Element element;

  public void bind(
      final Nifty niftyParam,
      final Screen screenParam,
      final Element newElement,
      final Properties properties,
      final ControllerEventListener newListener,
      final Attributes controlDefinitionAttributes) {
    nifty = niftyParam;
    screen = screenParam;
    element = newElement;
    setDifficulty("easy");
  }

  public void onStartScreen() {
  }

  public void onFocus(final boolean getFocus) {
  }

  public void inputEvent(final NiftyInputEvent inputEvent) {
  }

  public void removePanel() {
    nifty.removeElement(screen, element);
  }

  public void setDifficulty(final String mode) {
    element.findElementByName("easy").setStyle("unselected");
    element.findElementByName("medium").setStyle("unselected");
    element.findElementByName("hard").setStyle("unselected");
    element.findElementByName("expert").setStyle("unselected");

    if ("easy".equals(mode)) {
      element.findElementByName("easy").setStyle("selected");
    } else if ("medium".equals(mode)) {
      element.findElementByName("medium").setStyle("selected");
    } else if ("hard".equals(mode)) {
      element.findElementByName("hard").setStyle("selected");
    } else if ("expert".equals(mode)) {
      element.findElementByName("expert").setStyle("selected");
    }
  }
}
