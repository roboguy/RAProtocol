AOS Project 2 

Sudhanshu Iyer	- sxi120530
Gaurav Dey 	- gxd130330
Nischal Colluru	- nxc130530

The projects implements Roucairol Carvalho protocol - distributed mutual exclusion protocol 
within the constraints given in project description.

The program utilizes TCP Server/Client for socket communication. 

****** TO COMPILE ******
The project uses Apache Maven for dependency management. Hence to compile maven is required on the Path.
To do a clean compile issue the following command in the project root folder:

	mvn clean compile assembly:single

This commands creates a JAR file under target/ directory with all dependencies packaged 
inside for execution.

****** TO RUN ******
To run the program issue the following command:

	java -jar target/<jar-name>.jar <node_id>
	
The program expects a configuration file named "AOS_P2_CONF.json" at the project root directory &
a keys file named "KEYS". The program also expects a critical section shared file named "csFile.txt"
which is used later in the Testing Framework to ensure that the CS executed mutually exclusively.
The csFile.txt contains an integer value - initial value, which is updated (+1) by each process.
Specific formats for all files are shown in the example files.

****** TESTING FRAMEWORK ******
The output of csFile.txt is verified by tester program which takes "csFile.txt" & "AOS_P2_CONF.json" files
as input. Output is printed to screen / logged if a violation did/did not occour.
	To run the testing framework:
 
	java -jar testProgram.jar <path/to/AOS_P2_CONF.json> <path/to/csFile.txt>

The program was tested with Java 7.
