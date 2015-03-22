package edu.utdallas.aos.p2;

public class Service {

	//IMportant :- Take care for Concurreny with respect to HashMaps for Have and Havenot ,Maps
	public void csEnter()
	{
		//isrequestedForCS= true;
		
		// Check for Have and Have not keys
		//if (Has all keys)
			// isinCS=true
			// execute CriticalSection
			// call csLeave();
		//Else if (Has not is not null)
			//get corresponding keys from various Servers 
			// wait till response (while its not null)
		
	}
	public void csLeave()
	{
		// Lock the Queue so that no entry is added further.
		//Process the entire buffered Queue and satisfy the requests by sending keys.
		
	}
	public void criticalSection()
	{
		//read the file & increment it by 1 . This will be used for testing the mutual exclusion.
	}
}
