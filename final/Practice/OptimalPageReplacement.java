import java.util.Scanner;

public class OptimalPageReplacement {

    // Method to check if a page is already in a frame
    public static boolean isPageInFrames(int[] frames, int page) {
        for (int frame : frames) {
            if (frame == page) {
                return true;
            }
        }
        return false;
    }

    // Method to find the optimal page to replace
    public static int findOptimalReplacementIndex(int[] frames, int[] pages, int currentIndex) {
        int[] futureIndex = new int[frames.length];
        for (int i = 0; i < frames.length; i++) {
            futureIndex[i] = Integer.MAX_VALUE; // Initialize with a large value
            for (int j = currentIndex + 1; j < pages.length; j++) {
                if (frames[i] == pages[j]) {
                    futureIndex[i] = j;
                    break;
                }
            }
        }

        // Find the frame with the farthest usage in the future
        int optimalIndex = 0;
        for (int i = 1; i < futureIndex.length; i++) {
            if (futureIndex[i] > futureIndex[optimalIndex]) {
                optimalIndex = i;
            }
        }
        return optimalIndex;
    }

    public static void optimalPageReplacement(int[] pages, int capacity) {
        int[] frames = new int[capacity];
        int pageFaults = 0;
        boolean isHit;
        String comment;

        // Initialize frames to -1
        for (int i = 0; i < capacity; i++) {
            frames[i] = -1;
        }

        // Print the table headers
        System.out.println("\nStep\tPage\tFrames\t\tPage Fault\tComment");

        // Process each page in the reference string
        for (int step = 0; step < pages.length; step++) {
            int currentPage = pages[step];
            isHit = isPageInFrames(frames, currentPage);
            comment = "";  // Initialize comment

            if (!isHit) {
                boolean replaced = false;
                for (int i = 0; i < capacity; i++) {
                    if (frames[i] == -1) {  // If an empty frame is available
                        frames[i] = currentPage;
                        replaced = true;
                        pageFaults++;
                        comment = "Loaded into frame";
                        break;
                    }
                }

                if (!replaced) {  // If no empty frame is available, replace the optimal page
                    int replaceIndex = findOptimalReplacementIndex(frames, pages, step);
                    comment = "Replaced " + frames[replaceIndex] + " (Optimal)";
                    frames[replaceIndex] = currentPage;
                    pageFaults++;
                }
            } else {
                comment = "Page hit";
            }

            // Print the current step, page, frames, and the comment
            System.out.print((step + 1) + "\t" + currentPage + "\t");
            for (int i = 0; i < capacity; i++) {
                if (frames[i] != -1) {
                    System.out.print(frames[i] + " ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println("\t\t" + (isHit ? "No" : "Yes") + "\t\t" + comment);
        }

        // Print total page faults at the end
        System.out.println("Total Page Faults = " + pageFaults);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of pages: ");
        int n = scanner.nextInt();

        System.out.print("Enter reference string: ");
        int[] pages = new int[n];
        for (int i = 0; i < n; i++) {
            pages[i] = scanner.nextInt();
        }

        System.out.print("Enter number of frames: ");
        int capacity = scanner.nextInt();

        optimalPageReplacement(pages, capacity);

        scanner.close();
    }
}
