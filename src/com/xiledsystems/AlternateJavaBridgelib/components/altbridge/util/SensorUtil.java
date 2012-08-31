package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

public class SensorUtil {
	
	private SensorUtil() {		
	}
	
	public static float[] Smooth(float[] input, float[] output, float alpha) {
		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + alpha * (input[i] - output[i]);
		}
		return output;
	}

}
