package de.lessvoid.nifty.batch;

import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.jglfont.BitmapFont;
import org.jglfont.BitmapFontFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

public class BatchRenderFont implements RenderFont {
  private final BatchRenderDevice batchRenderDevice;
  private final BitmapFont font;

  public BatchRenderFont(
      final BatchRenderDevice batchRenderDevice,
      @Nonnull final String name,
      @Nonnull final BitmapFontFactory factory,
      @Nonnull final NiftyResourceLoader resourceLoader) throws IOException {
    this.batchRenderDevice = batchRenderDevice;
    this.font = factory.loadFont(resourceLoader.getResourceAsStream(name), name);
  }

  @Override
  public int getHeight() {
    return font.getHeight();
  }

  @Override
  public int getWidth(@Nonnull final String text) {
    return font.getStringWidth(text);
  }

  @Override
  public int getWidth(@Nonnull final String text, final float size) {
    return font.getStringWidth(text, size);
  }

  @Override
  public int getCharacterAdvance(final char currentCharacter, final char nextCharacter, final float size) {
    return font.getCharacterWidth(currentCharacter, nextCharacter, size);
  }

  @Override
  public void dispose() {
    batchRenderDevice.disposeFont(this);
  }

  public BitmapFont getBitmapFont() {
    return font;
  }
}
