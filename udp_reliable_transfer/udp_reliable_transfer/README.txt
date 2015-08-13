Author: Kristin Ottofy
Author: Kendal Brown
Date: March 25, 2012
Class: CSCI 4760 - Networks


			udp_reliable_transfer

----------------- ABOUT  ------------------
This program consists of a client application and a server application that
implement reliable data transfer of a file over UDP using a stop-and-wait
protocol. 


-------------- FILES -----------------
UrftClient.java
UrftServer.java
UrftHelper.java

---------------- RUN  ---------------------
Complile all files in the project directory: "javac *.java"


Run this program in 2 seperate Terminals.

Terminal 1: "java UrftServer 1234" 
	 where 1234 can be replaced by a desired port number

Terminal 2: "java UrftClient localhost 1234 file.txt"
	 where localhost can be replaced by a desired IP Address (ex: 127.0.0.1)
	 where 1234 can be replaced by a desired port number
	 where file.txt can be replaced by the name of the file to transfer

NOTES: 
       *The port numbers must be the same.
       *The file to copy must be saved in the directory "client_dir"
       *The copied file will be saved in the directory "server_dir"


Please contact kottofy@gmail.com for comments or concerns about this program.
