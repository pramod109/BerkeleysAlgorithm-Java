import sun.net.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class Slave{
	public static int master = 0;
	static boolean syncComplete = false;
	public static void main(String args[]) throws Exception
	{
		SlaveThread t = new SlaveThread();
		t.ReceiveTime();
		Thread Synchronization = new Thread()
		{
			public void run()
			{
				try{
					t.Sync();
				}
				catch (Exception Ex)
				{
					
				}
			}
		};
		Thread Send = new Thread()
		{
				public void run()
			{
				try{
					t.SendMsg();
				}
				catch(Exception ex)
				{
				
				}
			}
		};
			
		Thread Receive = new Thread()
		{
			public void run()
			{
				try{
					t.ReceiveMsg();
				}
				catch(Exception ex)
				{
					
				}				
			}
		};
		
		Synchronization.start();
			//System.out.println("Part two started.");
			Send.start();
			Receive.start();		
		
	}
}
class SlaveThread {
                            
	  				
	String msg = new String();	
	//Intializing count
	Random rand = new Random();
	int count = rand.nextInt(49)+1;
	int adjust = 0;
	boolean synched = false;
	static boolean syncComplete = false;
	
	int nodes = 0;
	//generating ID
	int ID = 0;
	
	public void Sync() throws Exception{
		
	InetAddress group = InetAddress.getByName("228.5.6.7");
	MulticastSocket r = new MulticastSocket(6789);
	byte[] buf = new byte[1000];
	DatagramPacket recv = new DatagramPacket(buf, buf.length);
	r.joinGroup(group);
	while(!syncComplete)
	{
		r.receive(recv);
		String message = new String(recv.getData(),0,recv.getLength());
		String[] sync = message.split(":");
		if(sync.length==4){
			
		
			if(!synched){

				System.out.println("New offset received");
				System.out.println("Synchronizing with the time daemon....");
				count = Integer.valueOf(sync[1])+count;
				ID = Integer.valueOf(sync[2]);
				nodes = Integer.valueOf(sync[3]);
				System.out.println("New count: "+count);
				System.out.println("ID assigned: "+ID);
				synched = true;
				System.out.println();
				if(ID==nodes)
				{
					syncComplete = true;
				}
			}
			else{

				System.out.println("New offset received. Offset: "+sync[0]);
				System.out.println("Synchronizing to the new average....");
				count = Integer.valueOf(sync[0]) + count;
				System.out.println("Synchronization complete. New count: "+count);	
				System.out.println();
				if(Integer.valueOf(sync[2])==nodes)
				{
					syncComplete = true;
				}					
			}
		}
		
	}
	}
	
	public void ReceiveTime() throws Exception{
		
	System.out.println("count: "+count);
	InetAddress group = InetAddress.getByName("228.5.6.7");
	MulticastSocket r = new MulticastSocket(6789);
	MulticastSocket s = new MulticastSocket(6788);
	r.joinGroup(group);
	byte[] buf = new byte[1000];
	DatagramPacket recv = new DatagramPacket(buf, buf.length);
	r.receive(recv);
	String message = new String(recv.getData(),0,recv.getLength());
	int daemon_count = Integer.parseInt(message);
	System.out.println("daemon_count:"+daemon_count);
	int diff = count - daemon_count;
	System.out.println("offset of "+diff+" sent to the time daemon");
	msg = Integer.toString(diff);
	
	DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),group, 6788);
	s.send(hi);
		
	}
	
	public void SendMsg() throws Exception{
		String msg = "";
		InetAddress group = InetAddress.getByName("228.5.6.7");
		MulticastSocket s = new MulticastSocket(6788);
		while(!syncComplete)
			Thread.sleep(5000);
		int[] vector = new int[nodes];
		System.out.println("Sending Messages....");
		System.out.println();
			for(int i=0;i<2;i++)
			{
	
					int temp = rand.nextInt(3000)+1000;
					Thread.sleep(temp);
					msg = "message "+i+" from process "+ID;
					DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),group, 6788);
					s.send(hi);
					
			}			
	}
	
	public void ReceiveMsg() throws Exception{
		String message = "";
		InetAddress group = InetAddress.getByName("228.5.6.7");
		MulticastSocket r = new MulticastSocket(6788);
		r.joinGroup(group);
		byte[] buf = new byte[1000];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		while(!syncComplete)
			Thread.sleep(5000);
		//System.out.println("Receiving messages");	
	
		while(true){

				r.receive(recv);
				message = new String(recv.getData(),0,recv.getLength());
				if(message.length()>15){
					System.out.println("Received "+message);
				}
							
		}
		
	}
}
