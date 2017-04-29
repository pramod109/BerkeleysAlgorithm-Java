import sun.net.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Master{
	
	public static void main(String[] args) throws Exception
	{
		Scanner in = new Scanner(System.in);
		MasterThread daemon = new MasterThread();
		System.out.println("Please enter the number of the nodes you are going to create.");
		int num = in.nextInt();
		daemon.nodeCount(num);
		Thread Send = new Thread()
		{
				public void run()
			{
				try{
					daemon.ReceiveMsg();
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
					daemon.SendMsg();
				}
				catch(Exception ex)
				{
					
				}				
			}
		};
		Send.start();
		Receive.start();
	}
}
class MasterThread{ 
                            
	  			
	String msg = new String();
	Random rand = new Random();
	int count = rand.nextInt(49)+1;
	int adjust = 0;
	int nodes = 1;
	int offset = 0;
	int nodeCheck = 0;
	boolean syncComplete = false;

	
	//for receiving the offset from the slave nodes and calculating the average
	public void ReceiveMsg() throws Exception {
		
	InetAddress group = InetAddress.getByName("228.5.6.7");	
	MulticastSocket r = new MulticastSocket(6788);
	MulticastSocket s = new MulticastSocket(6789);
	byte[] buf = new byte[1000];
	DatagramPacket recv = new DatagramPacket(buf, buf.length);
	
	r.joinGroup(group);
	while(!syncComplete)
	{
		r.receive(recv);
		String message = new String(recv.getData(),0,recv.getLength());
		System.out.println("New node generated");
		//System.out.println("nodes: "+nodes);
		int ID = nodes;
		if(nodes==nodeCheck)
		{
			syncComplete = true;
		}
		nodes++;
		int diff = Integer.valueOf(message);
		System.out.println("Offset of "+diff+" received. Calculating average....");
		adjust = diff/nodes;
		count = count + adjust;	
		offset = adjust - diff;	
		msg = adjust+":"+offset+":"+ID+":"+nodeCheck;
		System.out.println("Average calculated. New count: "+count);
		System.out.println("Sending the nodes the new offset....");
		System.out.println();
		Thread.sleep(1000);
		DatagramPacket sync = new DatagramPacket(msg.getBytes(), msg.length(),group, 6789);
		s.send(sync);
	
	}
	}
	
	public void SendMsg() throws Exception{
	System.out.println("count: "+count);
	InetAddress group = InetAddress.getByName("228.5.6.7");	
	MulticastSocket s = new MulticastSocket(6789);
	while(!syncComplete)
	{
		msg = Integer.toString(count);	
		DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),group, 6789);
		s.send(hi);		
		Thread.sleep(3000);
				
	}
				
	}
	
	public void nodeCount(int num ) throws Exception{
		nodeCheck  = num;
		//System.out.println(nodeCheck);
	}

  
}
