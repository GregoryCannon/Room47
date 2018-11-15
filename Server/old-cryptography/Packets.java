package cryptography;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.SecretKey;

public class Packets {
	public static class EstablishCommPacket implements java.io.Serializable{
		public String name;
		public long time;
		public byte[] enc;
		public byte[] signature;

		public EstablishCommPacket(String name, long time, byte[] enc, byte[] signature){
			this.name = name;
			this.time = time;
			this.enc = enc;
			this.signature = signature;
		}
	}

	public static class BytePacket implements java.io.Serializable{
		public String hMacStr;
		public String iv;
		public String cipherText;

		public BytePacket(byte[] bytes, SecretKey sessionKey){
			try{
				byte[] hMac = Crypto.calculateHmac(sessionKey, bytes);
				String hMacStr = Base64.toBase64String(hMac);
				this.hMacStr = hMacStr.substring(0, 24);
				byte[][] ivAndCipher = Crypto.cbcEncrypt(sessionKey, bytes);
				this.iv = Base64.toBase64String(ivAndCipher[0]);
				this.cipherText = Base64.toBase64String(ivAndCipher[1]);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static class StringMessage implements java.io.Serializable{
		public String str;

		public StringMessage(String str){
			this.str = str;
		}
	}

	/*
	public static class GetStatusPacket extends MessagePacket{

		public GetStatusPacket(SecretKey sessionKey) {
			super(sessionKey);
		}

		@Override
		byte[] getBytes() {
			// TODO Auto-generated method stub
			return null;
		}

	}*/


	public static class EstablishPacket implements java.io.Serializable{
		public String name;
		public long time;
		public byte[] enc;
		public byte[] signature;

		// Number of bytes for each in the packet
		final int nameL = 32;
		final int timeL = 8;
		final int encL = 256;
		final int sigL = 256;

		public EstablishPacket(String name, long time, byte[] enc, byte[] signature){
			this.name = name;
			this.time = time;
			this.enc = enc;
			this.signature = signature;
		}

		public EstablishPacket(byte[] encoded){
			decode(encoded);
		}

		private void decode(byte[] packet){
			//System.out.println("Decoding: " + packet);
			System.out.println("ClientPacket length: " + packet.length);
			byte[] nameB = Arrays.copyOfRange(packet, 0, nameL);
			name = new String(nameB);
			byte[] timeB = Arrays.copyOfRange(packet, nameL, nameL+timeL);
			time = Serializer.bytesToLong(timeB);
			enc = Arrays.copyOfRange(packet, nameL+timeL, nameL+timeL+encL);
			signature = Arrays.copyOfRange(packet, nameL+timeL+encL, nameL+timeL+encL+sigL);
		}

		public byte[] encode(){
			byte[] nameB = name.getBytes();
			byte[] timeB = Serializer.longToBytes(time);
			byte[] packet = new byte[nameL + timeL + encL + sigL];
			System.arraycopy(nameB, 0, packet, 0, Math.min(nameB.length, nameL));
			System.arraycopy(timeB, 0, packet, nameL, Math.min(timeB.length, timeL));
			System.arraycopy(enc, 0, packet, nameL+timeL, Math.min(enc.length, encL));
			System.arraycopy(signature, 0, packet, nameL+timeL+encL, Math.min(signature.length, sigL));
			System.out.println("ClientPacket length: " + packet.length);
			return packet;
		}
	}
}
