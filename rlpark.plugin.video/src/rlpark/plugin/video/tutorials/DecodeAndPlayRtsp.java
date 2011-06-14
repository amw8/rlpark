/*******************************************************************************
 * Copyright (c) 2008, 2010 Xuggle Inc.  All rights reserved.
 *  
 * This file is part of Xuggle-Xuggler-Main.
 *
 * Xuggle-Xuggler-Main is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Xuggle-Xuggler-Main is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Xuggle-Xuggler-Main.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package rlpark.plugin.video.tutorials;

import java.awt.image.BufferedImage;

import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IContainerParameters;
import com.xuggle.xuggler.IError;

public class DecodeAndPlayRtsp {
  public static void main(String[] args) {
    // String source = "rtsp://127.0.0.1:4000/mpeg4";
    String source = "/Users/thomas/Movies/CritterbotRandomNoMusic.m4v";
    IContainerParameters parameters = IContainerParameters.make();
    parameters.setInitialPause(false);
    IMediaReader mediaReader = ToolFactory.makeReader(source);
    mediaReader.getContainer().setParameters(parameters);
    mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
    final VideoImage mScreen = new VideoImage();
    IMediaListener mediaListener = new MediaListenerAdapter() {
      @Override
      public void onVideoPicture(IVideoPictureEvent event) {
        mScreen.setImage(event.getJavaData());
      }
    };
    mediaReader.addListener(mediaListener);
    IError e = null;
    while (e == null)
      e = mediaReader.readPacket();
    System.err.println(e.getDescription());
    mScreen.dispose();
    mediaReader.close();
  }
}
