package image_transformation;

public class CheckStackSize {
    public static void runCheckStackSize() {
        // Get the default stack size in bytes
        long stackSize = Thread.currentThread().getThreadGroup().getParent().getMaxPriority();

        // Convert stack size to megabytes (MB)
        long stackSizeMB = stackSize / (1024 * 1024);

        // Print the default stack size
        System.out.println("Default Stack Size: " + stackSizeMB + " MB");
    }
}
