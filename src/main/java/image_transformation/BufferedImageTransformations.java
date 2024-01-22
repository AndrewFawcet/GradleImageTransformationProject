package image_transformation;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BufferedImageTransformations {

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
