package de.lessvoid.nifty.internal.render.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.lessvoid.nifty.api.BlendMode;
import de.lessvoid.nifty.api.NiftyColor;
import de.lessvoid.nifty.api.NiftyLinearGradient;
import de.lessvoid.nifty.internal.math.Mat4;
import de.lessvoid.nifty.spi.NiftyRenderDevice;
import de.lessvoid.nifty.spi.NiftyTexture;

public class BatchManager {
  private static Logger log = Logger.getLogger(BatchManager.class.getName());

  private List<Batch> activeBatches = new ArrayList<Batch>();

  public void begin() {
    activeBatches.clear();

    // we always start with blend mode enabled
    changeBlendMode(BlendMode.BLEND);
  }

  public void changeBlendMode(final BlendMode blendMode) {
    activeBatches.add(new ChangeBlendModeBatch(blendMode));
  }

  public void addTextureQuad(final NiftyTexture niftyTexture, final Mat4 mat, final NiftyColor color) {
    TextureBatch batch = textureBatch(niftyTexture);
    if (batch.add(
        0.0, 0.0,
        niftyTexture.getWidth(), niftyTexture.getHeight(),
        niftyTexture.getU0(), niftyTexture.getV0(),
        niftyTexture.getU1(), niftyTexture.getV1(),
        mat,
        color)) {
      return;
    }
    batch = newTextureBatch(niftyTexture);
    if (batch.add(
        0.0, 0.0,
        niftyTexture.getWidth(), niftyTexture.getHeight(),
        niftyTexture.getU0(), niftyTexture.getV0(),
        niftyTexture.getU1(), niftyTexture.getV1(),
        mat,
        color)) {
      return;
    }
    // WTF?
    throw new RuntimeException("Created new texture batch but couldn't add any data to it. This should never happen!");
  }

  public void addTextureQuad(
      final NiftyTexture niftyTexture,
      final Mat4 mat,
      final double x,
      final double y,
      final int width,
      final int height,
      final double u0,
      final double v0,
      final double u1,
      final double v1,
      final NiftyColor color) {
    TextureBatch batch = textureBatch(niftyTexture);
    if (batch.add(x, y, width, height, u0, v0, u1, v1, mat, color)) {
      return;
    }
    batch = newTextureBatch(niftyTexture);
    if (batch.add(x, y, width, height, u0, v0, u1, v1, mat, color)) {
      return;
    }
    // WTF?
    throw new RuntimeException("Created new texture batch but couldn't add any data to it. This should never happen!");
  }

  public void addLinearGradientQuad(final double x0, final double y0, final double x1, final double y1, final NiftyLinearGradient fillLinearGradient) {
    float dx = (float) x1 - (float) x0;
    float dy = (float) y1 - (float) y0;
    NiftyLinearGradient gradient = new NiftyLinearGradient(
        x0 + fillLinearGradient.getX0() * dx,
        y0 + fillLinearGradient.getY0() * dy,
        x0 + fillLinearGradient.getX1() * dx,
        y0 + fillLinearGradient.getY1() * dy);
    gradient.addColorSteps(fillLinearGradient.getColorStops());

    LinearGradientQuadBatch batch = linearGradientQuadBatch(gradient);
    if (batch.add(x0, y0, x1, y1)) {
      return;
    }
    batch = newLinearGradientQuadBatch(gradient);
    if (batch.add(x0, y0, x1, y1)) {
      return;
    }

    // WTF?
    throw new RuntimeException("Created new linear gradient batch but couldn't add any data to it. This should never happen!");
  }

  public void addColorQuad(final double x0, final double y0, final double x1, final double y1, final NiftyColor c) {
    ColorQuadBatch batch = colorQuadBatch();
    if (batch.add(x0, y0, x1, y1, c, c, c, c)) {
      return;
    }
    batch = newColorQuadBatch();
    if (batch.add(x0, y0, x1, y1, c, c, c, c)) {
      return;
    }
    // WTF?
    throw new RuntimeException("Created new color batch but couldn't add any data to it. This should never happen!");
  }

  public void addCustomShader(final String shaderId) {
    activeBatches.add(new CustomShaderBatch(shaderId));
  }

  public void end(final NiftyRenderDevice renderDevice) {
    renderDevice.begin();
    for (int i=0; i<activeBatches.size(); i++) {
      activeBatches.get(i).render(renderDevice);
    }
    renderDevice.end();
  }

  private TextureBatch textureBatch(final NiftyTexture niftyTexture) {
    if (activeBatches.isEmpty()) {
      return newTextureBatch(niftyTexture);
    }
    Batch lastBatch = activeBatches.get(activeBatches.size() - 1);
    if (!(lastBatch instanceof TextureBatch)) {
      return newTextureBatch(niftyTexture);
    }
    TextureBatch textureBatch = (TextureBatch) lastBatch; 
    if (textureBatch.needsNewBatch(niftyTexture)) {
      return newTextureBatch(niftyTexture);
    }
    return textureBatch;
  }

  private TextureBatch newTextureBatch(final NiftyTexture niftyTexture) {
    TextureBatch batch = new TextureBatch(niftyTexture);
    activeBatches.add(batch);
    log.fine("new texture batch added. total batch count now: " + activeBatches.size());
    return batch;
  }

  private ColorQuadBatch colorQuadBatch() {
    if (activeBatches.isEmpty()) {
      return newColorQuadBatch();
    }
    Batch lastBatch = activeBatches.get(activeBatches.size() - 1);
    if (!(lastBatch instanceof ColorQuadBatch)) {
      return newColorQuadBatch();
    }
    return (ColorQuadBatch) lastBatch; 
  }

  private ColorQuadBatch newColorQuadBatch() {
    ColorQuadBatch batch = new ColorQuadBatch();
    activeBatches.add(batch);
    log.fine("new color quad batch added. total batch count now: " + activeBatches.size());
    return batch;
  }

  private LinearGradientQuadBatch linearGradientQuadBatch(final NiftyLinearGradient params) {
    if (activeBatches.isEmpty()) {
      return newLinearGradientQuadBatch(params);
    }
    Batch lastBatch = activeBatches.get(activeBatches.size() - 1);
    if (!(lastBatch instanceof LinearGradientQuadBatch)) {
      return newLinearGradientQuadBatch(params);
    }
    LinearGradientQuadBatch batch = (LinearGradientQuadBatch) lastBatch;
    if (!batch.requiresNewBatch(params)) {
      return batch;
    }
    return newLinearGradientQuadBatch(params);
  }

  private LinearGradientQuadBatch newLinearGradientQuadBatch(final NiftyLinearGradient params) {
    LinearGradientQuadBatch batch = new LinearGradientQuadBatch(params);
    activeBatches.add(batch);
    log.fine("new linear gradient quad batch added. total batch count now: " + activeBatches.size());
    return batch;
  }
}
