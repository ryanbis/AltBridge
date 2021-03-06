package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.security.SecureRandom;
import java.util.Random;


public class AltRandom {

	private static long random1seed;
	private static long random2seed;
	private static final Random random = new SecureRandom();
	private static final Random random2 = new SecureRandom();
	private static char[] lowChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
	  'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	private static char[] upperChars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private static char[] nums = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
	private static char[] specialChars = { '/', '@', '#', '!', '$', '%', '^', '&', '*' };
	private static boolean seedSet = false;
	
	/**
	 * Generates a random integer from the specified start, to the end. This is an expensive operation. 
	 * This method is designed to be more "random" than just calling Random.nextInt().
	 * 
	 * It uses two instances of the SecureRandom class with their seeds set to
	 * different seed numbers (the seeds are generated through a basic algorithm
	 * which is generated when this method, or RndDbl is first called).
	 * 
	 * 20 Numbers are then generated from these two SecureRandom instances, then
	 * it chooses an integer from 0 to 1. If it's 0, it uses the second set of 
	 * integers, otherwise it uses the first. Then it randomly picks one of those
	 * integers to return. This is an effort to reduce like numbers when calling
	 * for a random number in sequence.
	 * 
	 * @param from 
	 * @param to
	 * @return
	 */
	public static int RndInt(int from, int to) {
	  if (!seedSet) {
		random1seed = (long) ((((System.currentTimeMillis() * .4458 ) * 1118 ) / 9) * random2.nextDouble());
		random2seed = (long) ((((System.currentTimeMillis() * .7114 ) * 5427 ) / 12) * random.nextDouble());
		seedSet = true;
	  }
		int range = ( to - from ) + 1;
		random.setSeed(random1seed);
		random2.setSeed(random2seed);
		int[] random1 = new int[10];
		int[] rand2 = new int[10];
		for (int i = 0; i < 10; i++) {
			random1[i] = random.nextInt(range) + from;
			rand2[i] = random2.nextInt(range) + from;
		}
		if (random.nextInt(2)==1) {
			return rand2[random.nextInt(10)];
		} else {
			return random1[random2.nextInt(10)];
		}
	}
	
	/**
	 * This method isn't as heavy as RndInt. It takes the two instances of SecureRandom,
	 * and runs nextDouble. The two results are multiplied together, then the 
	 * square root of that is returned.
	 * 
	 * @return
	 */
	public static double RndDbl() {
	  if (!seedSet) {
		random1seed = (long) ((((System.currentTimeMillis() * .4458 ) * 1118 ) / 9) * random2.nextDouble());
		random2seed = (long) ((((System.currentTimeMillis() * .7114 ) * 5427 ) / 12) * random.nextDouble());
		seedSet = true;
	  }
		random.setSeed(random1seed);
		random2.setSeed(random2seed);
		double x,y;
		x = random.nextDouble();
		y = random2.nextDouble();
		return Math.sqrt(x * y);
	}
	
	/**
	 * Generates a random string which may contain lower, and upper case
	 * characters, in addition to numbers, and any special characters
	 * supplied in this method. If the char[] is null, then you will only
	 * get upper/lower case letters, and numbers.
	 * 
	 * @param length
	 * @param specialChars
	 * @return
	 */
	public static String RndString(int length, char[] specialChars) {
		StringBuilder builder = new StringBuilder();
		  for (int i = 0; i < length; i++) {
			  int types;
			  if (specialChars == null || specialChars.length == 0) {
				  types = 3;
			  } else {
				  types = 4;
			  }
		    int type = random.nextInt(types);	    
		    int index;
		    switch (type) {	      
		      case 0:
		        index = random.nextInt(lowChars.length);
		        builder.append(lowChars[index]);
		        break;
		      case 1:
		        index = random.nextInt(upperChars.length);
	            builder.append(upperChars[index]);
	            break;
		      case 2:
	            index = random.nextInt(nums.length);
	            builder.append(nums[index]);
	            break;
		      case 3:
	            index = random.nextInt(specialChars.length);
	            builder.append(specialChars[index]);
	            break;
		    }
		  }
		  return builder.toString();
	}
	
	/**
	 * Generates a random string which may contain lower, and upper
	 * case characters, in addition to numbers, and some special
	 * characters ( /, @, #, !, $, %, &, ^, * )
	 * 
	 * @param length
	 * @return
	 */
	public static String RndString(int length) {
	  StringBuilder builder = new StringBuilder();
	  for ( int i = 0; i < length; i++) {
	    int type = random.nextInt(4);	    
	    int index;
	    switch (type) {	      
	      case 0:
	        index = random.nextInt(lowChars.length);
	        builder.append(lowChars[index]);
	        break;
	      case 1:
	        index = random.nextInt(upperChars.length);
           builder.append(upperChars[index]);
           break;
	      case 2:
           index = random.nextInt(nums.length);
           builder.append(nums[index]);
           break;
	      case 3:
           index = random.nextInt(specialChars.length);
           builder.append(specialChars[index]);
           break;
	    }
	  }
	  return builder.toString();
	}
}
