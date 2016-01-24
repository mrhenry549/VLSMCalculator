package vlsm;
import java.util.regex.MatchResult;

public class Address {
	private int availableHosts, slash, ip;
	
	public Address(MatchResult matchResult){
		this.slash = Integer.parseInt(matchResult.group(5));
		availableHosts = (int) Math.pow(2, 32 - this.slash) - 2;
		
		this.ip = 0;
		for (int i = 1; i < 5; i++){
			this.ip += Integer.parseInt(matchResult.group(i));
			if (i != 4){
				this.ip <<= 8;
			}
		}
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
	
	public static String addressToString(int address){
		int[] addressArray = new int[4];
		for (int i = 3; i != -1; i--){
			addressArray[i] = address & 0b11111111;
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
}
