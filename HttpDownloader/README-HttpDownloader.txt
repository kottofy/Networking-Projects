Author: Kristin Ottofy
Class: CSCI 4760
Program Name: HttpDownloader
Due Date: February 16, 2012


----- ABOUT THE PROGRAM -----

HttpDownloader is a program that takes in input (on the command line) 
the URL of an object to be downloaded and the number of connections 
through which different parts of the object will be retrieved using 
"Range:". The downloaded parts are re-stitched to compose the original 
file.


-------- HOW TO RUN ---------

To run this program, type "javac *.java" from the project directory, 
then "java HttpDownloader http://www.link.com #"
where http://www.link.com is the URL of the page to download an object 
from and # is the number of connections to retrieve the object with.
For example, "java HttpDownloader http://www.cs.uga.edu/images/template
	/cs_template_r2_c2.gif 5"

The program will output each part labeled "part_#" where # is the part 
number in the project directory along with the re-stitched file under
the name of the file name provided in the link. 
For example, "cs_template_r2_c2.gif"


Please contact kottofy@gmail.com for any questions or comments
concerning this program.
