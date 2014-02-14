package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

public class SensorUtil {
	
	private SensorUtil() {		
	}
	
	/**
	 * Smooth out sensor data.
	 * 
	 * @param input - the float array of values from the sensor
	 * @param output - your float array you manage in the app
	 * @param alpha - a number from 0.0 to 1.0. The smaller the number, the more smoothing is applied
	 *  
	 * @return - the smoothed values.
	 */
	public static float[] Smooth(float[] input, float[] output, float alpha) {
		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + alpha * (input[i] - output[i]);
		}
		return output;
	}

}
