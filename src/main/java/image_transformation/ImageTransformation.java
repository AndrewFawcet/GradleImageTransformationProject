package image_transformation;

import java.util.Scanner;

public class ImageTransformation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean saveImages = false;

        System.out.println("Welcome to Image Transformation Selector");

        do {
            System.out.println("Choose an image transformation method:");
            System.out.println("1. Pipeline Image Processing Using Image Buffer");
            System.out.println("2. Pipeline Image Processing Using int Arrays");
            System.out.println("3. Pipeline Image Processing Using int Arrays and Sharding (no synchronisation)");
            System.out.println("4. Pipeline Image Processing Using int Arrays, Sharding and concurrent hashmap (synchronisation)");
            System.out.println("5. Pipeline Image Processing Using int Arrays testing with IO operation timing removed");
            System.out.println("6. Pipeline Image Processing Using int Arrays and Sharding (no synchronisation) testing removing IO timing");
//            System.out.println("7. Pipeline Image Processing Using int Arrays and Sharding (with synchronisation) testing removing IO timing");
                                    
            System.out.println("0. Exit");

            choice = scanner.nextInt();

            if (choice >= 5 && choice <= 6) {
                System.out.println("Do you want to save the images to the hard drive? (y/n)  \n -testing without IO operations should be 'n', only use 'y' for checking code ");
                saveImages = scanner.next().equalsIgnoreCase("y");
            }

            switch (choice) {
                case 6:
                    PipelineImageProcessingUsingArraysAndSharding_IORemoved.runPipelineImageProcessingUsingArraysAndSharding_IORemoved(saveImages);
                    break;
                case 5:
                    PipelineImageProcessingUsingArrays_IORemoved.runPipelineImageProcessingUsingArrays_IORemoved(saveImages);
                    break;
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