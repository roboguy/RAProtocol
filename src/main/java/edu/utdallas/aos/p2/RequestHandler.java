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
			// Latch to BLOCK CS ENTER
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
						// Latch release and free to enter CS
					//else
						// Give up the key and update have and have not sets.
						//RUCHIR - Again send the request with old timestamp to enter CS
				}
				else
				{
					//I have not requested for CS.
					//give key to requesting Process & update Have & have not hashmaps for my Process.
					
				}
			}
			
			//Update my latest timestamp.
				
		}

		else if(request.getType().equals("RESPONSE"))
		{
			//Update have & have not set
			// Check Have not set is empty
					//If it is empty then release Latch
		}
	}
}
