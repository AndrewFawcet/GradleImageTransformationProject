-The Code:

	The code currently measures the time taken to perform a number of image transformations. (Greyscale, inversion and 180 rotation.) Each test performs the same three image transformation on each image, and then saves the result to the hard drive.
	When an image transformation method is selected the image transformations are run and timed. The method is run using 1 to 10 threads, giving a total of 10 results in a csv file.
	Each test case in the method (using 1 to 10 threads) transfoms 10 large png images.
	
	The methods employed to perform the image transformations are as follows:
	-Storing the image data to an image buffer in Java (BufferedImage). Each transformation pulling the data from the image buffer, changing it as required and storing it back to an Image Buffer then passing the buffered image onto the next operation.
	-Storing the image data into arrays (int Arrays) without a wrapper. Performing operations as required and saving it to the array. (Note that image data is converted into BufferedImage, before being converted to an array, and is converted back into BufferedImage before saving as PNG image to hard drive. This system of image data conversion is used for all int Array methods.)
	-Storing the image data into arrays (int Arrays) without a wrapper. Breaking the int arrays (representing the image) into 10 shards for processing. The shards are stored in a hashmap for operations, and at image collation and saving the appropriate sahrds are removed frm the hashmap. Note no synchronisation is used for the hashmap!
	-Same as method above, but instead of storing the shards in a standard hashmap, a hashmap controlling synchronisation of reading and writing is used (ConcurrentHashMap). (Basically write operations are serialised.)

	The code should be run and tested from the jar file 'GradleImageTransformation Project.jar' to ensure efficient thread use. Running the code from an editor may cause inconsistent results.
