package edu.utdallas.aos.p2;

import com.google.gson.Gson;

public class RequestHandler extends Thread {

	String message="";
	public RequestHandler(String message) {
		this.message=message;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public void Message()
	{
		Gson gson=new Gson();
		gson.fromJson(message, Request.class);
		Request request=new Request();
		if(request.getType().equals("REQUEST"))
		{
			//check whether the node is in Critical Section
			if(Shared.isInCS)
			{
				//Set the value in Queue.
				//1.a If Yes -> Buffer the request in queue (Object of Request Class)
			}
			//2. Else -> Check the Timestamp -> request.getTimeStamp()
			else
			{
				if(Shared.isRequestedCS)
				{
					
					//check my requested timestamp with the request.getTImestamp()
					//If(request.getTimestamp > Shared.myRequestTS)
						//Add it to the buffer queue
					//else
						// Give up the key and update have and have not sets.
				}
				else
				{
					//I have not requested for CS.
					//give key to requesting Process & update Have & have not hashmaps
				}
			}
			//
				
		}
		else if(request.getType().equals("RESPONSE"))
		{
			
		}
	}
}
