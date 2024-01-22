package image_transformation;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ArrayImageTransformations {

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

}
