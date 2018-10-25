package cryptography;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cryptography.Packets.EstablishCommunicationPacket;

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
	
	public static void main(String[] args){
		//public EstablishCommunicationPacket(String name, long tA, byte[] enc, byte[] params, byte[] signature){
		try{
			EstablishCommunicationPacket p = new EstablishCommunicationPacket("Test", 1, new byte[5], new byte[3], new byte[4]);
			System.out.println(p);
			System.out.println(p.name);
			System.out.println(p.tA);
			byte[] ser = serialize(p);
			System.out.println(ser);
			EstablishCommunicationPacket q = (EstablishCommunicationPacket) deserialize(ser);
			System.out.println(q);
			System.out.println(q.name);
			System.out.println(q.tA);
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
}
