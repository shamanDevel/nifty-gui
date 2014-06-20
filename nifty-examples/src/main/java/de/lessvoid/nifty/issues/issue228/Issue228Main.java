/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lessvoid.nifty.issues.issue228;

import de.lessvoid.nifty.issues.issue225.*;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.examples.LwjglInitHelper;
import de.lessvoid.nifty.renderer.lwjgl.render.LwjglRenderDevice;
import de.lessvoid.nifty.sound.openal.OpenALSoundDevice;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;

/**
 *
 * @author cris
 */
public class Issue228Main {
    
    public static void main(final String[] args) {
    if (!LwjglInitHelper.initSubSystems("New ImageMode")) {
      System.exit(0);
    }
    LwjglRenderDevice lwjglRenderDevice = new LwjglRenderDevice();
  
    // create nifty
    Nifty nifty = new Nifty(
        lwjglRenderDevice,
        new OpenALSoundDevice(),
        LwjglInitHelper.getInputSystem(),
        new AccurateTimeProvider());
   
    nifty.fromXml("issues/issue228/example.xml", "GScreen0");
    nifty.gotoScreen("GScreen0");
    LwjglInitHelper.renderLoop(nifty, null);
    LwjglInitHelper.destroy();
  }
    
}