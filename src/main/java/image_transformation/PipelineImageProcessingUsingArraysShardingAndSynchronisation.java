package image_transformation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class PipelineImageProcessingUsingArraysShardingAndSynchronisation {
// initial int array with sharding
  // placing shard in concurrent hashmap, if shards for image complete, collating and saving
  // the image, then deleting the shards from the concurrent hashmap. 
  //(Synchronisation maintained through concurrent hashmap)
  // pipelining 10 images putting them directly in an array. No wrapper class.

  // All Working
  public static void runPipelineImageProcessingUsingArraysShardingAndSynchronisation() {
    // Number of images to process (keep below 100, otherwise adjust hashmap keys)
    int numImages = 10;
    //Number of shards to break the images into for processing
    int totalNumShards = 10;
    //Number of threads to use for the processing (1 to the total number is tested)
    int totalNumThreads = 10;

    // Record the start time for each run (only one run for now)
    StringBuilder csvData = new StringBuilder();
    long startTime;
    long endTime;

    // Paths and directories
    String basePath = "C:/PhD/Code/GradleImageTransformationProject/src/main/resources/";

    // Collect shards data in a map
    Map<Integer, int[]> shardsMap = new ConcurrentHashMap<>();
    Map<Integer, Integer> shardsCountMap = new ConcurrentHashMap<>();

    // iterating through different thread counts
    for (int numThreads = 1; numThreads <= totalNumThreads; numThreads++) {
      ExecutorService processingExecutor = Executors.newFixedThreadPool(numThreads);
      startTime = System.currentTimeMillis();

      for (int i = 1; i <= numImages; i++) {
        String inputImagePath = basePath + "input/mountain" + i + ".png";
        // String outputImagePath = basePath +
        // "output/processed_mountain_fromIntArrayWithSharding" + i + ".png";
        // diagnose image transformation problems. Save all images.
        String outputImagePath = basePath + "output/processed_mountain_fromIntArrayWithSharding" + i + " " + numThreads + ".png";
        final int numImage = i;

        try {
          BufferedImage originalImage = ImageIO.read(new File(inputImagePath));
          int[] imageArrayWithDimensions = imageToArrayWithDimensions(originalImage);

          int originalImageHeight = originalImage.getHeight();
          int shardHeight = originalImageHeight / totalNumShards;
  
          for (int j = 0; j < totalNumShards; j++) {
            final int numShard = j;
            int startY = j * shardHeight;
            int endY = (j == totalNumShards - 1) ? originalImageHeight : startY + shardHeight;

            int[] shard = getShard(imageArrayWithDimensions, originalImage.getWidth(), startY, endY);

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
                collateAndSaveImage(numImage, shardsMap, outputImagePath, originalImageHeight, totalNumShards);
                removeShards(numImage, shardsMap, shardsCountMap, totalNumShards);
              }

            });
          }
        } catch (IOException e) {
          e.printStackTrace();
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

    String csvFilePath = basePath + "pipelineTimings_intArraysWithShardingAndSynchronisationIORemoved.csv";

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
      int originalImageHeight, int totalNumShards) {

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

    if (allShardsPresent && assembledImageArray != null) {
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
