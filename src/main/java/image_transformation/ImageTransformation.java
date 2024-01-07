package image_transformation;

import java.util.Scanner;

public class ImageTransformation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.println("Welcome to Image Transformation Selector");

        do {
            System.out.println("Choose an image transformation method:");
            System.out.println("1. Pipeline Image Processing Using Image Buffer");
            System.out.println("2. Pipeline Image Processing Using int Arrays");
            System.out.println("3. Pipeline Image Processing Using int Arrays and Sharding (no synchronisation)");
            System.out.println("4. Pipeline Image Processing Using int Arrays, Sharding and concurrent hashmap (synchronisation)");                        
            System.out.println("0. Exit");

            choice = scanner.nextInt();

            switch (choice) {
                case 4:
                    PipelineImageProcessingUsingArraysShardingAndSynchronisation.runPipelineImageProcessingUsingArraysShardingAndSynchronisation();
                    break;               
                case 3:
                    PipelineImageProcessingUsingArraysAndSharding.runPipelineImageProcessingUsingArraysAndSharding();
                    break;               
                case 2:
                    PipelineImageProcessingUsingArrays.runPipelineImageProcessingUsingArrays();
                    break;
                case 1:
                    PipelineImageProcessing.runPipelineImageProcessing();
                    break;
                case 0:
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("I apologize, that was an inappropriate choice. Please try again.\n ('0' to Exit)");
                    break;
            }
        } while (choice != 0);

        scanner.close();
    }
}