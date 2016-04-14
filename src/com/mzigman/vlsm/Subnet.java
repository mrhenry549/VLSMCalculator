package com.mzigman.vlsm;

import com.mzigman.vlsm.util.VLSMUtilities;

public class Subnet {
	private int sequence,  // The order in which the user enters the subnet. (Subnet #1, etc.)
	requestedHosts,        // The amount of hosts requested for this subnet.
	slash,                 // The slash needed to accomodate the requested hosts.
	networkAddress,        // The base address for the subnet.
	subnetMask,            // Subnet mask for the subnet.
	broadcastAddress,      // Broadcast address for the subnet.
	hostPoolBegin,         // The first node address within the subnet (networkAddress + 1)
	hostPoolEnd,           // The final node address within the subnet (broadcastAddress - 1)
	hostPoolSize;          // The amount of nodes that can be fit within the subnet.

	public Subnet(int sequence, int requestedHosts){
		this.sequence = sequence;
		this.requestedHosts = requestedHosts;
		slash = VLSMUtilities.hostsToSlash(requestedHosts);
	}
	
	public int assignAddresses(int baseIp){
		subnetMask = VLSMUtilities.slashToMask(slash);
		networkAddress = baseIp & subnetMask;
		hostPoolSize = VLSMUtilities.slashToHosts(slash);
		hostPoolBegin = networkAddress + 1;
		hostPoolEnd = networkAddress + hostPoolSize;
		broadcastAddress = hostPoolEnd + 1;
		return broadcastAddress + 1;
	}

	public int getRequestedHosts(){
		return requestedHosts;
	}
	
	public int getNetworkAddress(){
		return networkAddress;
	}
	
	public int getSlash(){
		return slash;
	}
	
	public int getSubnetMask(){
		return subnetMask;
	}

	public int getBroadcastAddress(){
		return broadcastAddress;
	}
	
	public int getHostPoolSize(){
		return hostPoolSize;
	}
	
	public int getHostPoolBegin(){
		return hostPoolBegin;
	}
	
	public int getHostPoolEnd(){
		return hostPoolEnd;
	}
	
	public int getSequence(){
		return sequence;
	}

	public void setSlash(int i) {
		slash += i;
	}
}
