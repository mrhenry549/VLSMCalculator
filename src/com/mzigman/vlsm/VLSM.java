package com.mzigman.vlsm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

import com.mzigman.vlsm.util.VLSMUtilities;

public class VLSM {

	private Address networkPrefix;
	private List<Subnet> subnetList;
	
	public boolean assignSubnets(List<Subnet> subnets) {
		if (!canFitInNetwork(subnets)){
			return false;
		}
		
		subnetList = subnets;
		
		subnetList.sort(new Comparator<Subnet>() {
			@Override
			public int compare(Subnet left, Subnet right) {
				return right.getRequestedHosts() - left.getRequestedHosts();
			}
		});

		int baseIp = VLSMUtilities.slashToMask(networkPrefix.getSlash()) & networkPrefix.getIp();
		for (int i = 0; i < subnetList.size(); i++) {
			baseIp = subnetList.get(i).assignAddresses(baseIp);
		}
		
		subnetList.sort(new Comparator<Subnet>() {
			@Override
			public int compare(Subnet left, Subnet right) {
				return left.getSequence() - right.getSequence();
			}
		});
		
		return true;
	}

	public boolean setAddress(String address){
		if (address != null && VLSMUtilities.validateIPV4Address(address)){
			try{
				networkPrefix = new Address(address);
				return true;
			} catch(Exception e){
				networkPrefix = null;
			}
		}
		return false;
	}

	public void reset(){
		networkPrefix = null;
		subnetList = null;
	}

	public Address getNetworkPrefix(){
		return networkPrefix;
	}
	
	public List<Subnet> getSubnetList(){
		return subnetList;
	}
	
	public void exportToFile(File file){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
			pw.println("Network: " + VLSMUtilities.addressToString(networkPrefix.getIp()) + "/" + networkPrefix.getSlash());
			pw.println();
			for (int i = 0; i < subnetList.size(); i++){
				Subnet s = subnetList.get(i);
				pw.println("SUBNET #" + s.getSequence());
				pw.println("Network Address  : " + VLSMUtilities.addressToString(s.getNetworkAddress()) + "/" + s.getSlash());
				pw.println("Subnet Mask      : " + VLSMUtilities.addressToString(s.getSubnetMask()));
				pw.println("Broadcast Address: " + VLSMUtilities.addressToString(s.getBroadcastAddress()));
				pw.println("Host Pool Size   : " + s.getHostPoolSize());
				pw.println("Host Pool Range  : " + VLSMUtilities.addressToString(s.getHostPoolBegin()) + 
						" - " + VLSMUtilities.addressToString(s.getHostPoolEnd()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (pw != null){
				pw.close();
			}
		}
	}
	
	private boolean canFitInNetwork(List<Subnet> subnetList){
		int totalHostsNeeded = 0;
		for (int i = 0; i < subnetList.size(); i++){
			int slash = subnetList.get(i).getSlash();
			totalHostsNeeded += VLSMUtilities.slashToHosts(slash) + 2;
		}
		return networkPrefix.getAvailableHosts() >= totalHostsNeeded;
	}
}