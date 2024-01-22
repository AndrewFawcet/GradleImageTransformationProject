package image_transformation;

public class ArrayShardImageTransformations {
  public static int[] rotateImageArray(int[] pixelDataWithDimensions, int width, int startY, int endY) {
    int height = endY - startY;

    int[] rotatedPixelData = new int[pixelDataWithDimensions.length];
    rotatedPixelData[0] = width; // Setting width as the first element
    rotatedPixelData[1] = height; // Setting height as the second element

    int index = 2; // Starting index after width and height information

    for (int y = startY; y < endY; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = pixelDataWithDimensions[(y - startY) * width + x + index];
        int alpha = (pixel >> 24) & 0xFF;

        // Calculate rotated coordinates for the shard
        int newX = width - x - 1; // Rotate horizontally
        int newY = (endY - y - 1); // Reverse vertically

        // Update the pixel data in the rotated shard
        rotatedPixelData[newY * width + newX + index] = (alpha << 24) | (pixel & 0x00FFFFFF);
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
}
