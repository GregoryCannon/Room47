package cryptography;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cryptography.Packets.EstablishCommPacket;

public class Serializer {
	public static byte[] serialize(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    return out.toByteArray();
	}
	
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}
	
	public static byte[] longToBytes(long l) {
	    byte[] result = new byte[8];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}

	public static long bytesToLong(byte[] b) {
	    long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}
	
	/*
	public static void main(String[] args){
		//public EstablishCommunicationPacket(String name, long tA, byte[] enc, byte[] params, byte[] signature){
		try{
			EstablishCommPacket p = new EstablishCommPacket("Test", 1, new byte[5], new byte[4]);
			System.out.println(p);
			System.out.println(p.name);
			System.out.println(p.time);
			byte[] ser = serialize(p);
			System.out.println(ser);
			EstablishCommPacket q = (EstablishCommPacket) deserialize(ser);
			System.out.println(q);
			System.out.println(q.name);
			System.out.println(q.time);
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}*/
}
