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
    BufferedImage rotatedImage = rotateImage(originalImage);
    BufferedImage invertedImage = invertImage(rotatedImage);
    BufferedImage grayscaleImage = grayscaleImage(invertedImage);
    saveImage(grayscaleImage, outputImagePath);
  }

  // Rotate the image
  public static BufferedImage rotateImage(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = originalImage.getRGB(x, y);
        int alpha = (pixel >> 24) & 0xFF;

        if (alpha == 0) {
          // Preserve fully transparent pixel
          rotatedImage.setRGB(width - x - 1, height - y - 1, 0x00000000); // Transparent
        } else {
          // Preserve other pixels
          rotatedImage.setRGB(width - x - 1, height - y - 1, pixel);
        }
      }
    }

    return rotatedImage;
  }

  // Invert the image colors
  public static BufferedImage invertImage(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    BufferedImage invertedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = originalImage.getRGB(x, y);
        int alpha = (pixel >> 24) & 0xFF;
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;

        // Invert colors
        int invertedPixel = (alpha << 24) | ((255 - red) << 16) | ((255 - green) << 8) | (255 - blue);

        invertedImage.setRGB(x, y, invertedPixel);
      }
    }

    return invertedImage;
  }

  // Convert the image to grayscale
  public static BufferedImage grayscaleImage(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = originalImage.getRGB(x, y);
        // System.out.println("pixel " + pixel);
        int alpha = (pixel >> 24) & 0xFF;
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;

        int gray = (red + green + blue) / 3;
        int grayPixel = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

        grayscaleImage.setRGB(x, y, grayPixel);
      }
    }

    return grayscaleImage;
  }

  // Save the image to a file
  public static void saveImage(BufferedImage image, String outputFilePath) {
    try {
      File outputFile = new File(outputFilePath);
      ImageIO.write(image, "png", outputFile);
      System.out.println(outputFile.getName() + " Image saved successfully.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}