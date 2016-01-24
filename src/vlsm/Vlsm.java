package vlsm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class Vlsm {

	public Vlsm(){
		Scanner scanner = new Scanner(System.in);
		Address networkPrefix = getNetworkPrefix(scanner);
		Subnet[] subnets = getSubnets(networkPrefix, scanner);
		assignSubnets(subnets, networkPrefix);
		printToFile(subnets, networkPrefix);
		scanner.close();
	}

	private Address getNetworkPrefix(Scanner scanner){
		MatchResult result = null;
		boolean invalid;
		do{
			invalid = false;
			System.out.print("Insert an IPv4 address in CIDR notation: ");
			try{
				scanner.findInLine("(\\d+).(\\d+).(\\d+).(\\d+)/(\\d+)");
				result = scanner.match();
				checkResult(result);
			}
			catch(IllegalStateException e){
				System.out.println("Error, please enter a valid IPv4 address.");
				scanner.nextLine();
				invalid = true;
			}
		} while (invalid);

		Address networkPrefix = new Address(result);
		System.out.println("This allows for a total of " + networkPrefix.getAvailableHosts()
		+ " host addresses.");

		return networkPrefix;
	}

	private Subnet[] getSubnets(Address networkPrefix, Scanner scanner){
		boolean invalid;
		int numOfSubnets = -1;
		Subnet[] subnets;
		do{
			do{
				invalid = false;
				System.out.print("Insert number of network subnets to create: ");
				numOfSubnets = scanner.nextInt();
				if (numOfSubnets < 1){
					System.out.println("Error, please enter a valid number of subnets.");
					scanner.nextLine();
					invalid = true;
				}
			} while (invalid);

			subnets = new Subnet[numOfSubnets];
			int totalHosts = 0;
			System.out.println("Parameters for each subnet.");
			for (int i = 0; i < numOfSubnets; i++){
				int size = -1;
				do{
					invalid = false;
					System.out.println("Subnet " + (i + 1));
					System.out.print("Size: ");
					size = scanner.nextInt();
					if (size < 1){
						System.out.println("Error, please enter a valid size for subnet.");
						scanner.nextLine();
						invalid = true;
					}
				} while (invalid);

				totalHosts += size;
				//System.out.print("Minimum (1), Maximum (2) or Balanced (3): ");
				//int option = scan.nextInt();
				subnets[i] = new Subnet(size, 0); //change to (size, option)
			} // for loop

			if (totalHosts > networkPrefix.getAvailableHosts()){
				System.out.println("Too many hosts needed, please revise your numbers.");
				invalid = true;
				subnets = null;
			}
		} while (invalid);
		return subnets;
	}

	private void assignSubnets(Subnet[] subnets, Address networkPrefix){
		//sort subnet size requirements in descending order
		for (int i = 0; i < subnets.length - 1; i++){
			if (subnets[i].getRequestedHosts() < subnets[i + 1].getRequestedHosts()){
				Subnet temp = subnets[i];
				subnets[i] = subnets[i + 1];
				subnets[i + 1] = temp;
			}
		}

		//change extra maximum subnets to balanced?
		int baseIp = networkPrefix.getIp();
		for (int i = 0; i < subnets.length; i++){
			baseIp = subnets[i].assignAddresses(baseIp);			
		}
	}

	private void printToFile(Subnet[] subnets, Address networkPrefix){
		PrintWriter writer = null;
		try{
			writer = new PrintWriter("VLSM.txt", "UTF-8");
			writer.println("Original Network Address: " + Address.addressToString(networkPrefix.getIp())
			+ "/" + networkPrefix.getSlash());
			for (int i = 0; i < subnets.length; i++){
				writer.println("SUBNET " + (i + 1));
				writer.println("Network Address: " + Address.addressToString(subnets[i].getNetworkAddress())
				+ "/" + subnets[i].getSlash());
				writer.println("Subnet Mask: " + Address.addressToString(subnets[i].getSubnetMask()));
				writer.println("Broadcast Address: " + Address.addressToString(subnets[i].getBroadcastAddress()));
				writer.println("Host Pool Size: " + subnets[i].getHostPoolSize());
				writer.println("Host Pool Range: " + Address.addressToString(subnets[i].getHostPoolBegin())
				+ " - " + Address.addressToString(subnets[i].getHostPoolEnd()));
			}
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}
		catch(UnsupportedEncodingException e){
			System.out.println(e);
		}
		writer.close();
	}

	private void checkResult(MatchResult result){
		if (result.groupCount() != 5 || Integer.parseInt(result.group(5)) > 31 ||
				Integer.parseInt(result.group(5)) < 0){
			throw new IllegalStateException();
		}
		for (int i = 1; i < 5; i++){
			if (Integer.parseInt(result.group(i)) > 255 || Integer.parseInt(result.group(i)) < 0){
				throw new IllegalStateException();
			}
		}
	}
}