import java.util.Scanner;

public class LRUPageReplacementArray {

    public static void lru(int[] pages, int capacity) {
        int[] frames = new int[capacity];      // Holds the frames
        int[] lastUsed = new int[capacity];    // Tracks the last usage of pages in frames
        int pageFaults = 0, time = 0;
        boolean isHit;
        String comment;  // Comment variable initialized here

        // Initialize frames to -1 to indicate they are empty
        for (int i = 0; i < capacity; i++) {
            frames[i] = -1;
        }

        // Print the table headers
        System.out.println("\nStep\tPage\tFrames\t\tPage Fault\tComment");

        // Process each page in the reference string
        for (int step = 0; step < pages.length; step++) {
            int currentPage = pages[step];
            isHit = false;
            comment = "";  // Initialize comment at the start of each step

            // Check if the page is already in the frames (Page Hit)
            for (int i = 0; i < capacity; i++) {
                if (frames[i] == currentPage) {
                    isHit = true;
                    lastUsed[i] = time;  // Update the usage time
                    break;
                }
            }

            if (!isHit) {
                // If there's an empty frame, use it without replacing
                boolean replaced = false;
                for (int i = 0; i < capacity; i++) {
                    if (frames[i] == -1) {  // Empty frame found
                        frames[i] = currentPage;
                        lastUsed[i] = time;
                        replaced = true;
                        pageFaults++;
                        comment = "Loaded into frame";
                        break;
                    }
                }

                // If no empty frame, replace the least recently used page
                if (!replaced) {
                    int lruIndex = 0;
                    for (int i = 1; i < capacity; i++) {
                        if (lastUsed[i] < lastUsed[lruIndex]) {
                            lruIndex = i;
                        }
                    }
                    comment = "Replaced " + frames[lruIndex] + " (LRU)";
                    frames[lruIndex] = currentPage;
                    lastUsed[lruIndex] = time;
                    pageFaults++;
                }
            } else {
                // Page hit - no replacement needed
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

            time++;
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

        lru(pages, capacity);

        scanner.close();
    }
}
