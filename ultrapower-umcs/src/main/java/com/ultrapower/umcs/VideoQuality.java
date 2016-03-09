package com.ultrapower.umcs;

//three common size:
//176×144
//352×288
//640x480
public class VideoQuality {
	int width;//<=0 means not change
	int height;//<=0 means not change
	int maxBitrate;//<=0 means not change
	int minBitrate;//<=0 means not change
	
	public VideoQuality(int width,int height, int maxBitrate, int minBitrate) {
		this.width = width;
		this.height = height;
		this.maxBitrate = maxBitrate;
		this.minBitrate = minBitrate;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getMaxBitrate(){
		return maxBitrate;
	}
	
	public int getMinBitrate(){
		return minBitrate;
	}
}
