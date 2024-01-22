package image_transformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PipelineImageProcessing {
  public static void runPipelineImageProcessing() {
    // Number of images to process
    int numImages = 10;
    // Number of threads to use for the processing (1 to the total number is tested)
    int totalNumThreads = 10;

    // Record the start time for each run (only one run for now)
    StringBuilder csvData = new StringBuilder();
    long startTime;
    long endTime;

    // Paths and directories
    String basePath = "C:/PhD/Code/GradleImageTransformationProject/src/main/resources/";

    for (int numThreads = 1; numThreads <= totalNumThreads; numThreads++) {
      // Create an executor with a variable number of threads for inversion and
      // grayscale
      ExecutorService processingExecutor = Executors.newFixedThreadPool(numThreads);
      startTime = System.currentTimeMillis();

      // Iterate through the images
      for (int i = 1; i <= numImages; i++) {
        String imagePath = basePath + "input/mountain" + i + ".png";
        String outputImagePath = basePath + "output/processed_mountain" + i + ".png";

        try {
          // Load the original image
          BufferedImage originalImage = ImageIO.read(new File(imagePath));
          processingExecutor.submit(() -> processImage(originalImage, outputImagePath));
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

    String csvFilePath = basePath + "pipelineTimings_ImageBuffered.csv";

    try (FileWriter writer = new FileWriter(csvFilePath)) {
      // Write the CSV data to the file
      writer.write(csvData.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Image processing completed.");
  }

  public static void processImage(BufferedImage originalImage, String outputImagePath) {
    BufferedImage rotatedImage = BufferedImageTransformations.rotateImage(originalImage);
    BufferedImage invertedImage = BufferedImageTransformations.invertImage(rotatedImage);
    BufferedImage grayscaleImage = BufferedImageTransformations.grayscaleImage(invertedImage);
    BufferedImageTransformations.saveImage(grayscaleImage, outputImagePath);
  }

}