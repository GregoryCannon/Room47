package cryptography;

public class Packets {
	public static class EstablishCommunicationPacket{
		public String name; 
		public long tA;
		public byte[] enc;
			public byte[] params; 
			public byte[] signature;
			
		public EstablishCommunicationPacket(String name, long tA, byte[] enc, byte[] params, byte[] signature){
			this.name = name;
			this.tA = tA;
			this.enc = enc;
			this.params = params;
			this.signature = signature;
		}
	}
}
