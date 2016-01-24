package vlsm;

public class Subnet {
	private int requestedHosts, option, slash, hostPoolSize, 
	networkAddress, subnetMask, broadcastAddress, hostPoolBegin, hostPoolEnd;

	public Subnet(int requestedHosts, int option){
		this.requestedHosts = requestedHosts;
		this.option = option;
		this.networkAddress = this.subnetMask = this.broadcastAddress = 
				this.hostPoolBegin = this.hostPoolEnd = 0;
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
	
	public int assignAddresses(int baseIp){
		int networkBitsNeeded = hostsToNetworkBits(requestedHosts);
		slash = 32 - networkBitsNeeded;
		subnetMask = Address.slashToMask(slash);
		networkAddress = baseIp & subnetMask;
		hostPoolSize = (int) Math.pow(2, networkBitsNeeded) - 2;
		hostPoolBegin = networkAddress + 1;
		hostPoolEnd = networkAddress + hostPoolSize;
		broadcastAddress = hostPoolEnd + 1;
		return broadcastAddress + 1;
	}

	// returns incorrect bits
	// h = 2^x - 2
	// e.g. 126 should return 7, while 127 should return 8
	private int hostsToNetworkBits(int hosts){
		int length = Integer.toBinaryString(hosts).length();
		int actualHosts = 0;
		for (int i = 0; i < length; i++){
			actualHosts <<= 1;
			actualHosts += 1;
		}
		int networkBits = (int)Math.ceil((Math.log(actualHosts) / Math.log(2)));
		return networkBits;
	}

	private void printClass(){
		System.out.println("Requested Hosts: " + requestedHosts);
		System.out.println("Option: " + option);
		System.out.println("Slash: " + slash);
		System.out.println("Host Pool Size: " + hostPoolSize);
		System.out.println("Network Address: " + Address.addressToString(networkAddress));
		System.out.println("Subnet Mask: " + Address.addressToString(subnetMask));
		System.out.println("Broadcast Address: " + Address.addressToString(broadcastAddress));
		System.out.println("Host Pool Begin: " + Address.addressToString(hostPoolBegin));
		System.out.println("Host Pool End: " + Address.addressToString(hostPoolEnd));
	}
}
