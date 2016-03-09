package com.ultrapower.umcs.video;

import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class H264HardwareDecoder implements Callback  {
	final static String TAG = "H264HardwareDecoder";
    MediaCodec mediaCodec;
	Object surface;
	boolean hasInited;
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void initDecoder(Object surface) {
		this.surface = surface;
		this.hasInited = false;
		setupInternal();
	}

	private void setupInternal() {
		try {
			if (mediaCodec != null) {
				mediaCodec.flush();
				mediaCodec.stop();
				mediaCodec.release();
				mediaCodec = null;
			}
			SurfaceView sv = (SurfaceView) surface;
			
		
			if(sv.getHolder().getSurface()!=null&&sv.getHolder().getSurface().isValid())
			{
				mediaCodec = MediaCodec.createDecoderByType("video/avc");
				MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 1280, 720);
				mediaCodec.configure(mediaFormat, sv.getHolder().getSurface(),null, 0);
				mediaCodec.start();
				hasInited = true;
			}
			else 
			{
				sv.getHolder().addCallback(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void releaseInternal() {
		if (mediaCodec != null) {
			mediaCodec.stop();
			mediaCodec.release();
			mediaCodec = null;
			Log.d("decode", "release decoder");

		}
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void decode(byte[] data, long timeMS) {
		try {
			if (!hasInited) {
				return;
			}
			if (mediaCodec == null) {
				setupInternal();
			}
			ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
			ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
			int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
			if (inputBufferIndex >= 0) {
				// Log.d("encoder", "put a data");
				ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
				inputBuffer.clear();
				inputBuffer.put(data, 0, data.length);
				mediaCodec.queueInputBuffer(inputBufferIndex, 0,
						data.length, timeMS * 1000, 0);
			}

			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

			while (outputBufferIndex >= 0) {
				// Log.d("encoder",String.format( "out a data %d",outCount++));
				mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
				outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
			}
		} catch (Exception e) {
			Log.e("H264HardwareDecoder", "decode error");
			// TODO: handle exception
		}

	}

	public void release() {
		releaseInternal();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG,"h264 surface create");
		try{
			SurfaceView sv = (SurfaceView) surface;
			mediaCodec = MediaCodec.createDecoderByType("video/avc");
			MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 1280, 720);
			mediaCodec.configure(mediaFormat, sv.getHolder().getSurface(),null, 0);
			mediaCodec.start();
			hasInited = true;
		}
		catch(Exception e)
		{
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}
