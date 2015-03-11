AOS Project 1 - Sudhanshu Iyer

The projects implements a distributed node discovery protocol within the constraints
given in project description.

The program utilizes SCTP Server/Client for socket communication. 

****** TO COMPILE ******
The project uses Apache Maven for dependency management. Hence to compile maven is required on the Path.
To do a clean compile issue the following command in the project root folder:

	mvn clean compile assembly:single

This commands creates a JAR file under target/ directory with all dependencies packaged 
inside for execution.

****** TO RUN ******
To run the program issue the following command:

	java -jar target/<jar-name>.jar <node_id>
	
The program expects a configuration file named "node.conf" at the project root directory.
The program was tested with Java 7.