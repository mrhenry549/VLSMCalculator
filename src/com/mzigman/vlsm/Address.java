package com.mzigman.vlsm;

import com.mzigman.vlsm.util.VLSMUtilities;

public class Address {
	private int availableHosts, slash, ip;
	
	public Address(String address){
		String[] addressAndSlash = address.split("/");
		String[] addressParts = addressAndSlash[0].split("\\.");
		
		this.slash = Integer.parseInt(addressAndSlash[1]);
		if (slash > 32){
			throw new IndexOutOfBoundsException();
		}
		this.availableHosts = VLSMUtilities.slashToHosts(slash);
		this.ip = VLSMUtilities.stringToAddress(addressParts);
	}
		
	public int getAvailableHosts(){
		return availableHosts;
	}
	
	public int getSlash(){
		return slash;
	}
	
	public int getIp(){
		return ip;
	}
}
