package image_transformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PipelineImageProcessingUsingArrays_IORemoved {

  // initial int array
  // pipelining 10 images putting them directly in an array. No wrapper class. All
  // Working
  public static void runPipelineImageProcessingUsingArrays_IORemoved(boolean saveImages) {
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

    // read the one image in, transfer to an int Array, use the int array for all
    // image transfomations
    String inputImagePath = basePath + "input/mountain1.png";
    int[] imageArrayWithDimensions = new int[0];

    try {
      BufferedImage originalImage = ImageIO.read(new File(inputImagePath));
      imageArrayWithDimensions = imageToArrayWithDimensions(originalImage);
    } catch (IOException e) {
      e.printStackTrace();
    }
    final int[] imageArrayWithDimensionsFinal = imageArrayWithDimensions;

    // iterating through different thread counts
    for (int numThreads = 1; numThreads <= totalNumThreads; numThreads++) {
      // Create an executor with a variable number of threads for inversion, rotation and grayscale
      ExecutorService processingExecutor = Executors.newFixedThreadPool(numThreads);
      startTime = System.currentTimeMillis();

      // Iterate through the images
      for (int i = 1; i <= numImages; i++) {
        String outputImagePath = basePath + "output/processed_mountain" + i + ".png";
        processingExecutor.submit(() -> processImage(imageArrayWithDimensionsFinal, outputImagePath, saveImages));
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

    String csvFilePath = basePath + "pipelineTimings_intArraysIORemoved.csv";

    try (FileWriter writer = new FileWriter(csvFilePath)) {
      // Write the CSV data to the file
      writer.write(csvData.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Image processing completed.");
  }

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

  public static void processImage(int[] pixelDataWithDimensions, String outputImagePath, boolean saveImages) {
    int[] pixelDataWithDimensionsRotated = rotateImageArray(pixelDataWithDimensions);
    int[] pixelDataWithDimensionsInverted = invertImageArray(pixelDataWithDimensionsRotated);
    int[] pixelDataWithDimensionsGreyscaled = grayscaleImageArray(pixelDataWithDimensionsInverted);
    if (saveImages){
      saveArrayWithDimensions(pixelDataWithDimensionsGreyscaled, outputImagePath);
    }
  }

  public static void saveArrayWithDimensions(int[] pixelDataWithDimensions, String outputImagePath) {

    int width = pixelDataWithDimensions[0];
    int height = pixelDataWithDimensions[1];

    // Create a BufferedImage using the pixel data
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, width, height, pixelDataWithDimensions, 2, width);

    // Save the BufferedImage as a PNG file
    outputImagePath = outputImagePath.substring(0, outputImagePath.length() - 4) + "_fromIntArray"
        + outputImagePath.substring(outputImagePath.length() - 4);
    File outputFile = new File(outputImagePath);
    try {
      ImageIO.write(image, "png", outputFile);
      System.out.println(outputFile.getName() + " Image saved successfully.");
    } catch (IOException e) {
      System.out.println("Error saving intArray image: " + e.getMessage());
    }
  }

  public static int[] rotateImageArray(int[] pixelDataWithDimensions) {
    int width = pixelDataWithDimensions[0];
    int height = pixelDataWithDimensions[1];

    int[] rotatedPixelData = new int[pixelDataWithDimensions.length];
    rotatedPixelData[0] = width; // Setting width as the first element
    rotatedPixelData[1] = height; // Setting height as the second element

    int index = 2; // Starting index after width and height information

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = pixelDataWithDimensions[index];
        int alpha = (pixel >> 24) & 0xFF;
        if (alpha == 0) {
          // Preserve fully transparent pixel
          rotatedPixelData[(width - x - 1) + (height - y - 1) * width + 2] = 0x00000000; // Transparent
        } else {
          // Preserve other pixels
          rotatedPixelData[(width - x - 1) + (height - y - 1) * width + 2] = pixel;
        }
        index++;
      }
    }

    return rotatedPixelData;
  }

  public static int[] invertImageArray(int[] pixelDataWithDimensions) {
    int width = pixelDataWithDimensions[0];
    int height = pixelDataWithDimensions[1];

    int[] invertedPixelData = new int[pixelDataWithDimensions.length];
    invertedPixelData[0] = height; // Setting height as the first element
    invertedPixelData[1] = width; // Setting width as the second element

    int index = 2; // Starting index after width and height information

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = pixelDataWithDimensions[index];
        int alpha = (pixel >> 24) & 0xFF;
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;

        // Invert colors
        int invertedPixel = (alpha << 24) | ((255 - red) << 16) | ((255 - green) << 8) | (255 - blue);
        invertedPixelData[index] = invertedPixel;

        index++;
      }
    }
    return invertedPixelData;
  }

  public static int[] grayscaleImageArray(int[] pixelDataWithDimensions) {
    int width = pixelDataWithDimensions[0];
    int height = pixelDataWithDimensions[1];

    int[] grayscalePixelData = new int[pixelDataWithDimensions.length];
    grayscalePixelData[0] = height; // Setting height as the first element
    grayscalePixelData[1] = width; // Setting width as the second element

    int index = 2; // Starting index after width and height information

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = pixelDataWithDimensions[index];
        int alpha = (pixel >> 24) & 0xFF;
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;
        int gray = (red + green + blue) / 3;
        int grayPixel = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
        grayscalePixelData[index] = grayPixel;
        index++;
      }
    }
    return grayscalePixelData;
  }

  // Save the image to a file
  public static void saveImage(BufferedImage image, String outputFilePath) {
    try {
      ImageIO.write(image, "png", new File(outputFilePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
