/*
 *  Copyright (c) 2012 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.ultrapower.umcs.video;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

public class ViERenderer {

    // View used for local rendering that Cameras can use for Video Overlay.
    private static SurfaceView g_localRenderer = null;

    private static String TAG = "ViERenderer";

    public static SurfaceView CreateRemoteRenderer(Context context) {
        return CreateRemoteRenderer(context, false);
    }

    public static SurfaceView CreateRemoteRenderer(Context context,
            boolean useOpenGLES2) {
        if(useOpenGLES2 == true && ViEAndroidGLES20.IsSupported(context)) {
            Log.d(TAG, "CreateRenderer use ViEAndroidGLES20");
            return new ViEAndroidGLES20(context);
        }
        else {
            Log.d(TAG, "CreateRenderer use SurfaceView");
            return new SurfaceView(context);
        }
    }

    // Creates a SurfaceView to be used by Android Camera
    // service to display a local preview.
    // This needs to be used on Android prior to version 2.1
    // in order to run the camera.
    // Call this function before ViECapture::StartCapture.
    // The created view needs to be added to a visible layout
    // after a camera has been allocated
    // (with the call ViECapture::AllocateCaptureDevice).
    // IE.
    // CreateLocalRenderer
    // ViECapture::AllocateCaptureDevice
    // LinearLayout.addview
    // ViECapture::StartCapture
    public static SurfaceView CreateLocalRenderer(Context context) {
        g_localRenderer = new SurfaceView(context);
        return g_localRenderer;
    }

    public static SurfaceView GetLocalRenderer() {
        return g_localRenderer;
    }

    public static void DeleteLocalRenderer() {
        g_localRenderer = null;
    }

}
