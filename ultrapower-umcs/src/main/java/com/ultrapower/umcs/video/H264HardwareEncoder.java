package com.ultrapower.umcs.video;

import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class H264HardwareEncoder {

	final static String TAG = "H264HardwareEncoder";
	MediaCodec mediaCodec;
	MediaFormat mediaFormat;
	int currentBitrateIndex;
	int[] biteRateLevel = { 200*1000,300 * 1000,400 * 1000,500*1000,800 * 1000, 1200 * 1000,
			2000 * 1000,3000*1000 };
	int width, height;
	boolean updateRate = false;
	int nextBitrate = 500;
	byte[] data = new byte[1024 * 1024 * 3];
	byte[] ppsData;
	private final long native_encoder;
	Object syncObj = new Object();

	public H264HardwareEncoder(int id, long nativeEncoder) {
		this.native_encoder = nativeEncoder;
	}

	private void releaseInternal() {
		if (mediaCodec != null) {
			//mediaCodec.flush();
			mediaCodec.stop();
			mediaCodec.release();
			mediaCodec = null;
			Log.d("encode", "release encoder");

		}
	}
	
	public static int getSupportedFormat() {
		int codecCount = MediaCodecList.getCodecCount();
		for (int i = 0; i < codecCount; i++) {
			MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
			
			if(info.isEncoder()&&!info.getName().equals("OMX.google.h264.encoder"))
			{
				String[] types = info.getSupportedTypes();
				
				for (int j = 0; j < types.length; j++) {
					if (types[j].equals("video/avc")) {
					 CodecCapabilities cc =	info.getCapabilitiesForType("video/avc");
					 if (cc.colorFormats.length>0) {
						for (int k = 0; k < cc.colorFormats.length; k++) {
							int format = cc.colorFormats[k];
							if (format==19||format==21) {
								Log.d(TAG,"getFormat "+format);
								return format;
							}
						}
					}
					}
				}
			}
		}
		return -1;
	}
	
	private void setupInternal() {
		if (mediaCodec != null) {
			mediaCodec.flush();
			mediaCodec.stop();
			mediaCodec.release();
			mediaCodec = null;
		}
		try {
			
			int format = getSupportedFormat();
			
			mediaCodec = MediaCodec.createEncoderByType("video/avc");
			mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
			currentBitrateIndex = getRateIndex(nextBitrate);
			Log.d(TAG, "h264 bitrate="+biteRateLevel[currentBitrateIndex]+"next bit"+nextBitrate);
			mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,biteRateLevel[currentBitrateIndex]);
		   
			mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,12);
			mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,format);
			mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
			mediaCodec.configure(mediaFormat, null, null,
					MediaCodec.CONFIGURE_FLAG_ENCODE);
			mediaCodec.start();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public void setRates(int bitRate, int frameRate) {
		Log.d("encoder", String.format("request new bitrate %d", bitRate));
//		nextBitrate = bitRate;
//		updateRate = true;
		if (updateRate) {
			return;
		}
		if (mediaCodec == null || mediaFormat == null) {
			return;
		}
		if (getRateIndex(bitRate) == currentBitrateIndex) {
			return;
		}
		nextBitrate = bitRate;
		updateRate = true;
	}

	private int getRateIndex(int newBitRate) {
		for (int i = biteRateLevel.length - 1; i >= 0; i--) {
			if (biteRateLevel[i] < (newBitRate * 1000)) {
				return i;
			}
		}
		return 0;
	}

	public void initEncoder(int width, int height, int bitrate) {
		this.width = width;
		this.height = height;
		this.nextBitrate = bitrate;
		setupInternal();
	}

	int count = 0;
	int encodeCount = 0;

	public void encode(byte[] dataForEncode, long timeMS) {
		if (mediaCodec == null) {
			setupInternal();
		}
		// updateRate = true;
		if (updateRate) {
			Log.d("encode",String.format("updateRate next bitrate %d", nextBitrate));
			//setupInternal();
			updateRate = false;
		}
		ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
		ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
		int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(dataForEncode, 0, dataForEncode.length);
			mediaCodec.queueInputBuffer(inputBufferIndex, 0,
					dataForEncode.length, timeMS * 1000, 0);
		}
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		while (outputBufferIndex >= 0) {
			ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
			int ppsLen = 0;
			if (bufferInfo.flags == 2) {
				ppsData = new byte[bufferInfo.size];
				outputBuffer.get(ppsData, 0, bufferInfo.size);
			} else {
				if ((bufferInfo.flags == 1||bufferInfo.flags==9) && ppsData != null
						&& ppsData.length != 0) {
					for (int i = 0; i < ppsData.length; i++) {
						data[i] = ppsData[i];
					}
					ppsLen = ppsData.length;
				}
				outputBuffer.get(data, ppsLen, bufferInfo.size);
				
				OnEncoded(data, ppsLen + bufferInfo.size,
						bufferInfo.presentationTimeUs / 1000,
						this.native_encoder);
			}
			mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
			outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		}
	}

	private native void OnEncoded(byte[] encodedData, int len, long timeMs,
			long context);

	public void release() {
		releaseInternal();
	}
}
