package cryptography;

public class Configuration {
	
	
	public static int setConfiguration(String config) {
		switch(config) {
		case "NoCrypto":
			return 1;
		case "SymmetricOnly":
			return 2;
		case "MACs":
			return 3;
		case "SymmetricMAC":
			return 4;
		}
		return -1;	
	}

}
