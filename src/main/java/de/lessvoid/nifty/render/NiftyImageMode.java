package de.lessvoid.nifty.render;

import java.util.logging.Logger;

import de.lessvoid.nifty.layout.Box;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;

/**
 * RenderImageMode supports the following modes.
 *
 * - "normal" (render the given image to the given image size)
 * - "subImage:" (render the given subImage of the image to the given size)
 * - "resize:" (special mode that allows scaling of bitmaps with a predefined schema)
 * - "repeat:" (special mode that repeating of texture)
 *
 * @author void
 */
public class NiftyImageMode {
  private static Logger log = Logger.getLogger(NiftyImageMode.class.getName());

  private static final String NORMAL_STRING = "normal";
  private static final String RESIZE_STRING = "resize:";
  private static final String SUB_IMAGE_STRING = "subImage:";
  private static final String REPEAT_STRING = "repeat:";

  private static final int SUBIMAGE_ARGS_COUNT = 4;
  private static final int REPEAT_ARGS_COUNT = 4;

  /**
   * supported modes.
   */
  private enum Mode { NORMAL, SUBIMAGE, RESIZE, REPEAT };

  /**
   * actual mode.
   */
  private Mode mode;

  /**
   * sub image box to scale.
   */
  private Box subImageBox;

  /**
   * repeat box to scale.
   */
  private Box repeatBox;

  /**
   * resize string.
   */
  private String resizeString;

  /**
   * normal rendering.
   * @return RenderImageMode for NORMAL mode
   */
  public static NiftyImageMode normal() {
    return new NiftyImageMode();
  }

  /**
   * scale a sub image.
   * @param x x
   * @param y y
   * @param w w
   * @param h h
   * @return RenderImageMode for SUBIMAGE mode
   */
  public static NiftyImageMode subImage(final int x, final int y, final int w, final int h) {
    return new NiftyImageMode(new Box(x, y, w, h));
  }

  /**
   * use the resize hint.
   * @param resizeString resize information
   * @return RenderImageMode for RESIZE mode
   */
  public static NiftyImageMode resize(final String resizeString) {
    return new NiftyImageMode(resizeString);
  }

  /**
   * standard constructor.
   */
  NiftyImageMode() {
    this.mode = Mode.NORMAL;
  }

  /**
   * subImage constructor.
   * @param box subImageBox
   */
  NiftyImageMode(final Box box) {
    this.mode = Mode.SUBIMAGE;
    this.subImageBox = box;
  }

  /**
   * resize constructor.
   * @param newResizeString string with resize information
   */
  NiftyImageMode(final String newResizeString) {
    this.mode = Mode.RESIZE;
    this.resizeString = newResizeString;
  }

  public NiftyImageMode(final Box box, final boolean b) {
    this.mode = Mode.REPEAT;
    this.repeatBox = box;
  }

  /**
   * create a RenderImageMode from the given String.
   * @param imageMode imageMode String
   * @return a RenderImageMode
   */
  public static NiftyImageMode valueOf(final String imageMode) {
    if (imageMode == null || NORMAL_STRING.equals(imageMode)) {
      return NiftyImageMode.normal();
    } else if (imageMode.startsWith(SUB_IMAGE_STRING)) {
      return handleSubImage(imageMode);
    } else if (imageMode.startsWith(RESIZE_STRING)) {
      return handleResize(imageMode);
    } else if (imageMode.startsWith(REPEAT_STRING)) {
      return handleRepeat(imageMode);
    } else {
      return NiftyImageMode.normal();
    }
  }

  /**
   * handle subImage.
   * @param imageMode image mode
   * @return RenderImageMode
   */
  private static NiftyImageMode handleSubImage(final String imageMode) {
    String parameters = imageMode.replaceFirst(SUB_IMAGE_STRING, "");
    if (parameters == null || parameters.length() == 0) {
      log.warning(
          "trying to parse imageMode [" + imageMode
          + "] but missing sub image definition! using default RenderImageMode normal.");
      return NiftyImageMode.normal();
    }
    String[] args = parameters.split(",");
    if (args.length != SUBIMAGE_ARGS_COUNT) {
      log.warning(
          "expecting exactly 4 parameters but got only " + args.length
          + " using default RenderImageMode normal.");
      return NiftyImageMode.normal();
    }
    int index = 0;
    int x = Integer.valueOf(args[index++]);
    int y = Integer.valueOf(args[index++]);
    int w = Integer.valueOf(args[index++]);
    int h = Integer.valueOf(args[index++]);
    return NiftyImageMode.subImage(x, y, w, h);
  }

  /**
   * handle repeat
   * @param imageMode image mode
   * @return RenderImageMode
   */
  private static NiftyImageMode handleRepeat(final String imageMode) {
    String parameters = imageMode.replaceFirst(REPEAT_STRING, "");
    if (parameters == null || parameters.length() == 0) {
      log.warning("trying to parse imageMode [" + imageMode + "] but missing repeat definition! using default RenderImageMode normal.");
      return NiftyImageMode.normal();
    }
    String[] args = parameters.split(",");
    if (args.length != REPEAT_ARGS_COUNT) {
      log.warning("expecting exactly 4 parameters but got only " + args.length + " using default RenderImageMode normal.");
      return NiftyImageMode.normal();
    }
    int index = 0;
    int x = Integer.valueOf(args[index++]);
    int y = Integer.valueOf(args[index++]);
    int w = Integer.valueOf(args[index++]);
    int h = Integer.valueOf(args[index++]);
    return NiftyImageMode.repeat(x, y, w, h);
  }

  private static NiftyImageMode repeat(final int x, final int y, final int w, final int h) {
    return new NiftyImageMode(new Box(x, y, w, h), true);
  }

  /**
   * handle resize.
   * @param imageMode image mode
   * @return RenderImageMode
   */
  private static NiftyImageMode handleResize(final String imageMode) {
    String parameters = imageMode.replaceFirst(RESIZE_STRING, "");
    if (parameters == null || parameters.length() == 0) {
      log.warning(
          "trying to parse imageMode [" + imageMode
          + "] but missing resize definition! using default RenderImageMode normal.");
      return NiftyImageMode.normal();
    }
    return NiftyImageMode.resize(parameters);
  }

  /**
   * Render image.
   * @param renderImage RenderImage
   * @param x x
   * @param y y
   * @param width width
   * @param height height
   * @param color color
   * @param scale scale
   */
  public void render(
      final RenderDevice renderDevice,
      final RenderImage renderImage,
      final int x,
      final int y,
      final int width,
      final int height,
      final Color color,
      final float scale) {
    int centerX = x + width/2;
    int centerY = y + height/2;

    if (mode == Mode.SUBIMAGE) {
      renderDevice.renderImage(
          renderImage,
          x, y, width, height,
          subImageBox.getX(), subImageBox.getY(), subImageBox.getWidth(), subImageBox.getHeight(),
          color,
          scale,
          centerX,
          centerY);
    } else if (mode == Mode.REPEAT) {
      int countX = width / repeatBox.getWidth();
      int countY = height / repeatBox.getHeight();

      for (int j=0; j<countY; j++) {
        renderRepeatLine(renderDevice, renderImage, x, y, width, color, scale, centerX, centerY, countX, j, repeatBox.getHeight());
      }
      int partly = height % repeatBox.getHeight();
      if (partly != 0) {
        renderRepeatLine(renderDevice, renderImage, x, y, width, color, scale, centerX, centerY, countX, countY, partly);
      }
    } else if (mode == Mode.RESIZE) {
      ResizeHelper resizeHelper = new ResizeHelper(renderDevice, renderImage, this.resizeString);
      resizeHelper.performRender(x, y, width, height, color, scale, centerX, centerY);
    } else {
      renderDevice.renderImage(renderImage, x, y, width, height, color, scale);
    }
  }

  private void renderRepeatLine(final RenderDevice renderDevice, final RenderImage renderImage, final int x, final int y, final int width, final Color color, final float scale, int centerX, int centerY, int countX, int j, int height) {
    for (int i=0; i<countX; i++) {
      renderDevice.renderImage(
          renderImage,
          x + i*repeatBox.getWidth(),
          y + j*repeatBox.getHeight(),
          repeatBox.getWidth(),
          height,
          repeatBox.getX(),
          repeatBox.getY(),
          repeatBox.getWidth(),
          height,
          color,
          scale,
          centerX,
          centerY);
    }
    int partlx = width % repeatBox.getWidth();
    if (partlx != 0) {
      renderDevice.renderImage(
          renderImage,
          x + width - partlx,
          y + j*repeatBox.getHeight(),
          partlx,
          height,
          repeatBox.getX(),
          repeatBox.getY(),
          partlx,
          height,
          color,
          scale,
          centerX,
          centerY);
    }
  }
}
