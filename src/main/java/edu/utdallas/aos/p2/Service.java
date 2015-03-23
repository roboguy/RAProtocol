package edu.utdallas.aos.p2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;

import com.google.gson.Gson;

import edu.utdallas.aos.p2.config.Node;

public class Service {

	//IMportant :- Take care for Concurrency with respect to HashMaps for Have and Havenot ,Maps
	public void csEnter()
	{
		//UPDATE MY REQUEST.TIMESTAMP
		Shared.logicalClockTimeStamp++;
		Shared.isRequestedCS= true;
		
		// Check for Have and Have not keys
		//if (Has all keys)
			// isinCS=true
			// execute CriticalSection
			// call csLeave();
		synchronized (Shared.objForLock) {
		
			if(Shared.haveNotKeys.isEmpty())
			{
				Shared.isInCS=true;
				criticalSection();
				return;
				//csLeave();
			}
			else
			{
				//Else if (Has not is not null)
				//get corresponding keys from various Servers 
				// wait till response (while its not null)
				Shared.requestTimeStamp=Shared.logicalClockTimeStamp;
				Iterator<String> iSet=Shared.haveNotKeys.iterator();
				while(iSet.hasNext())
				{
					String key=iSet.next();
					String split[]=key.split(",");
					String sendNodeId="";
					if(!(split[0].equals(Shared.myInfo.getId().toString())))
					{
						sendNodeId=split[0];
						
					}
					else
					{
						sendNodeId=split[1];
						
					}
					//Calling the information from Shared List of all Node Info
					Node sendNode=getNodeInfo(sendNodeId);
					
					if(!sendNodeId.equals(""))
					{
						Request rNode=new Request();
						rNode.setKey(key);
						rNode.setNodeId(Shared.myInfo.getId());
						rNode.setTimeStamp(Shared.requestTimeStamp); //Check if it is correct
						rNode.setType("REQUEST");
						Gson gson=new Gson();
						String message=gson.toJson(rNode);
						sendKeyRequest(message,sendNode);
					}
				}
				Shared.logicalClockTimeStamp++;
				
				//WHILE all request is sent
			
			}
		}
		
		while(true)
		{
			if(Shared.haveNotKeys.isEmpty())
			{
				Shared.isInCS=true;
				Shared.isRequestedCS=false;
				criticalSection();
				return;
			}
			
		}
		
		//csLeave();
		
		
		
	}
	private Node getNodeInfo(String sendNodeId) {
		Node n =new Node();
		n.setId(1);
		n.setHost("localhost");
		n.setPort("5001");
		return n;
	}
	public void sendKeyRequest(String message, Node sendNode)
	{
		String hostName=sendNode.getHost();
		Integer port=Integer.parseInt(sendNode.getPort());
		try
		{
			Socket clientSocket = new Socket(hostName,port);
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			writer.println(message);
			writer.close();
			clientSocket.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	public void csLeave()
	{
		// Lock the Queue so that no entry is added further.
		//Process the entire buffered Queue and satisfy the requests by sending keys.
		Shared.isInCS=false;
		Shared.isRequestedCS=false;
		synchronized (Shared.objForLock) {
			while(!Shared.bufferingQueue.isEmpty())
			{
				Request request=Shared.bufferingQueue.poll();
				fulfillReq(request);
			}
			
		}
		
	}
	/*
	 * This will fulfill the buffered request
	 */
	private void fulfillReq(Request request) {
	
		RequestHandler reqhandler=new RequestHandler();
		reqhandler.giveUpKey(request);
		
		
	}
	public void criticalSection()
	{
		//read the file & increment it by 1 . This will be used for testing the mutual exclusion.
		File csfile=new File("csFile.txt");
		try {
			Scanner scanner=new Scanner(csfile);
			int value=scanner.nextInt();
			value++;
			scanner.close();
			FileWriter f2 = new FileWriter("csFile.txt");
            f2.write(value);
            f2.close();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
