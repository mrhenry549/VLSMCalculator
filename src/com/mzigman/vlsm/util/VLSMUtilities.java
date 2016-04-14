package com.mzigman.vlsm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VLSMUtilities {

	public static int slashToMask(int slash){
		if (slash > 32){
			throw new IndexOutOfBoundsException();
		}

		int mask = 0;
		for (int i = 0; i < slash; i++){
			mask <<= 1;
			mask += 1;
		}
		for (int i = slash; i < 32; i++){
			mask <<= 1;
		}
		return mask;
	}
	
	public static int slashToHosts(int slash){
		return (int) Math.pow(2, 32 - slash) - 2;
	}
	
	public static int hostsToSlash(int hosts){
		int networkBits;
		for (networkBits = 2; networkBits < 32; networkBits++){
			if (hosts <= Math.pow(2, networkBits) - 2){
				break;
			}
		}
		return 32 - networkBits;
	}

	public static String addressToString(int address){
		int[] addressArray = new int[4];
		for (int i = 3; i != -1; i--){
			addressArray[i] = address & 0xFF;
			address >>= 8;
		}

		String ret = "";
		for (int i = 0; i < 4; i++){
			ret += addressArray[i];
			if (i != 3){
				ret += ".";
			}
		}
		return ret;
	}
	
	public static List<String> addressToString(List<Integer> addresses){
		List<String> addressStrings = new ArrayList<>();
		for (int address : addresses){
			addressStrings.add(addressToString(address));
		}
		return addressStrings;
	}
	
	public static int stringToAddress(String[] input){
		if (input.length != 4){
			throw new IndexOutOfBoundsException();
		}
		int address = 0;
		for (int i = 0; i < 4; i++){
			address += Integer.parseInt(input[i]);
			if (i != 3){
				address <<= 8;
			}
		}
		return address;
	}

	public static boolean validateIPV4Address(String address){
		return Pattern.matches("(\\d+).(\\d+).(\\d+).(\\d+)/(\\d+)", address);
	}
}
