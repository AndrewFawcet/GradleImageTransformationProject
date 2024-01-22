package image_transformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PipelineImageProcessingUsingArraysAndSharding_IORemoved {

  // initial int array with sharding
  // placing shard in hashmap, if shards for image complete
  // IO operations for reading the raw image and writing to disk after processing
  // have been removed from the timing
  // the image, then deleting the shards from the hashmap. (no synchronisation)
  // pipelining 10 images putting them directly in an array. No wrapper class.

  // All Working
  public static void runPipelineImageProcessingUsingArraysAndSharding_IORemoved(boolean saveImages) {
    // Number of images to process (keep below 100, otherwise adjust hashmap keys.)
    int numImages = 10;
    // Number of shards to break the images into for processing
    int totalNumShards = 10;
    // Number of threads to use for the processing (1 to the total number is tested)
    int totalNumThreads = 10;

    // Record the start time for each run (only one run for now)
    StringBuilder csvData = new StringBuilder();
    long startTime;
    long endTime;

    // Paths and directories
    String basePath = "C:/PhD/Code/GradleImageTransformationProject/src/main/resources/";

    // read the one image in, transfer to an int Array, use the int array for all
    // image transfomations
    String inputImagePath = basePath + "input/mountain1.png";
    int[] imageArrayWithDimensions = new int[0];

    // int originalImageHeight = 0;
    int originalImageWidth = 0;
    int originalImageHeight = 0;

    //create an array of an image for all image transformations to be sourced from
    try {
      BufferedImage originalImage = ImageIO.read(new File(inputImagePath));
      imageArrayWithDimensions = imageToArrayWithDimensions(originalImage);
      originalImageHeight = originalImage.getHeight();
      originalImageWidth = originalImage.getWidth();
    } catch (IOException e) {
      e.printStackTrace();
    }

    final int originalImageHeightFinal = originalImageHeight;
    final int[] imageArrayWithDimensionsFinal = imageArrayWithDimensions; 
    
    // Collect shards data in a map
    Map<Integer, int[]> shardsMap = new HashMap<>();
    Map<Integer, Integer> shardsCountMap = new HashMap<>();

    // iterating through different thread counts
    for (int numThreads = 1; numThreads <= totalNumThreads; numThreads++) {
      ExecutorService processingExecutor = Executors.newFixedThreadPool(numThreads);
      startTime = System.currentTimeMillis();

      for (int i = 1; i <= numImages; i++) {
        // diagnose image transformation problems. Save all images.
        final int numImage = i;
        String outputImagePath = basePath + "output/processed_mountain_fromIntArrayWithSharding" + numImage + " " + numThreads + ".png";        
        int shardHeight = originalImageHeightFinal / totalNumShards;

        for (int j = 0; j < totalNumShards; j++) {
          final int numShard = j;
          int startY = j * shardHeight;
          int endY = (j == totalNumShards - 1) ? originalImageHeight : startY + shardHeight;

          int[] shard = getShard(imageArrayWithDimensionsFinal, originalImageWidth, startY, endY);

          processingExecutor.submit(() -> {
            int[] processedShard = processImageShard(shard);
            int shardsKey = totalNumShards * 10000 + numImage * 100 + numShard;
            shardsMap.put(shardsKey, processedShard);
            int countKey = totalNumShards * 100 + numImage;
            int shardCount = shardsCountMap.getOrDefault(countKey, 0);
            shardCount++;
            shardsCountMap.put(countKey, shardCount);
            if (shardCount == totalNumShards) {
              // All shards for this image number are present, collate and save the image,
              // then remove shards from Map(s)
//              collateAndSaveImage(numImage, shardsMap, outputImagePath, originalImageHeightFinal, totalNumShards);
              collateAndSaveImage(numImage, shardsMap, outputImagePath, originalImageHeightFinal, totalNumShards, saveImages);
              removeShards(numImage, shardsMap, shardsCountMap, totalNumShards);
            }

          });
        }
      }

      // Shutdown the processing executor when all tasks are submitted
      processingExecutor.shutdown();

      try {
        // Wait for all tasks to complete or until the specified timeout
        if (!processingExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
          System.err.println("Some tasks did not complete within the timeout.");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // Record the end time
      endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;

      System.out.println("Total time taken with " + numThreads + " processing threads: " + totalTime + " milliseconds");

      // Append data to the CSV string
      csvData.append(numThreads).append(",").append(totalTime).append("\n");

    }

    String csvFilePath = basePath + "pipelineTimings_intArraysWithSharding.csv";

    try (FileWriter writer = new FileWriter(csvFilePath)) {
      // Write the CSV data to the file
      writer.write(csvData.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Image processing completed.");
  }

  // returns an integer array representing a shard of an image, when given the int
  // array of the image, the width, and the start and end y coordinates for the
  // shard.
  public static int[] getShard(int[] pixelDataWithDimensions, int width, int startY, int endY) {
    int shardHeight = endY - startY;
    int[] shard = new int[2 + width * shardHeight];
    shard[0] = width;
    shard[1] = shardHeight;

    for (int y = 0; y < shardHeight; y++) {
      System.arraycopy(pixelDataWithDimensions, (startY + y) * width + 2, shard, y * width + 2, width);
    }

    return shard;
  }

  // returns a int array with the first two numbers representing the image width
  // and height.
  public static int[] imageToArrayWithDimensions(BufferedImage originalImage) {

    int width = originalImage.getWidth();
    int height = originalImage.getHeight();

    int[] pixelDataWithDimensions = new int[2 + width * height]; // Increase the size by 2 for width and height

    // Store width and height as the first two elements
    pixelDataWithDimensions[0] = width;
    pixelDataWithDimensions[1] = height;

    // Retrieve pixel data starting from index 2 (after width and height) and store
    // it to array PixelDataWithDimensions
    originalImage.getRGB(0, 0, width, height, pixelDataWithDimensions, 2, width);

    return pixelDataWithDimensions;
  }

  public static int[] processImageShard(int[] pixelDataWithDimensions) {
    int width = pixelDataWithDimensions[0];
    int height = pixelDataWithDimensions[1];

    int shardHeight = height; // Full image initially

    // Check if it's a shard by checking the height
    if (height < pixelDataWithDimensions.length / width) {
      shardHeight = height;
    }

    int[] pixelDataWithDimensionsRotated = ArrayShardImageTransformations.rotateImageArray(pixelDataWithDimensions, width, 0, shardHeight);
    int[] pixelDataWithDimensionsInverted = ArrayShardImageTransformations.invertImageArray(pixelDataWithDimensionsRotated);
    int[] pixelDataWithDimensionsGreyscaled = ArrayShardImageTransformations.grayscaleImageArray(pixelDataWithDimensionsInverted);

    return pixelDataWithDimensionsGreyscaled;
  }

  public static void collateAndSaveImage(int numImage, Map<Integer, int[]> shardsMap, String outputImagePath,
      int originalImageHeight, int totalNumShards, boolean saveImages) {

    // Collect all shards for the given image number into a single array
    int[] assembledImageArray = null;
    boolean allShardsPresent = true;

    for (int j = 0; j < totalNumShards; j++) {
      int key = totalNumShards * 10000 + numImage * 100 + j;
      if (shardsMap.containsKey(key)) {
        int[] shard = shardsMap.get(key);
        int shardWidth = shard[0]; // Extract shard width
        int shardHeight = shard[1]; // Extract shard height

        if (assembledImageArray == null) {
          // Initialize assembled image array considering width and original image height
          assembledImageArray = new int[2 + shardWidth * originalImageHeight];
          assembledImageArray[0] = shardWidth; // Width
          assembledImageArray[1] = originalImageHeight; // Total height of the image
        }

        // Copy the shard data into the assembled image array, taking into account
        // 180-degree rotation
        int startIndex = 2 + (totalNumShards - j - 1) * shardHeight * shardWidth;
        System.arraycopy(shard, 2, assembledImageArray, startIndex, shard.length - 2);
      } else {
        System.out.println("All shards are not present, error in collating to save image. ");
        allShardsPresent = false;
        break;
      }
    }
    // only save the collated image if the input from the ImageTransformation menu is yes.  
    if (allShardsPresent && assembledImageArray != null && saveImages) {
      // Save the assembled image
      ArrayImageTransformations.saveArrayWithDimensions(assembledImageArray, outputImagePath);
    }
  }

  public static void removeShards(int numImage, Map<Integer, int[]> shardsMap,
      Map<Integer, Integer> shardsCountMap, int totalNumShards) {
    // Remove image shards from the map for this image number
    for (int j = 0; j < totalNumShards; j++) {
      shardsMap.remove(totalNumShards * 10000 + numImage * 100 + j);
    }
    // remove count from shardsCountMap
    shardsCountMap.remove(totalNumShards * 100 + numImage);
  }

}
 
