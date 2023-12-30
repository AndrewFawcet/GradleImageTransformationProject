package image_transformation;

import java.util.Scanner;

public class ImageTransformation {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Welcome to Image Transformation Selector");
    System.out.println("Choose an image transformation method:");
    System.out.println("1. Pipeline Image Processing");

    int choice = scanner.nextInt();

    switch (choice) {
        case 1:
            PipelineImageProcessing.runPipelineProcessing();
            break;
        default:
            System.out.println("Invalid choice.");
            break;
    }

    scanner.close();
  }
}



