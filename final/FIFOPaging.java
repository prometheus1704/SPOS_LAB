import java.util.*;

public class FIFOPaging {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Input for pages
        System.out.print("Enter number of pages: ");
        int numPages = scanner.nextInt();
        
        System.out.println("Enter page reference string: ");
        int[] pages = new int[numPages];
        for (int i = 0; i < numPages; i++) {
            pages[i] = scanner.nextInt();
        }
        
        System.out.print("Enter the number of frames: ");
        int numFrames = scanner.nextInt();
        
        // FIFO algorithm implementation
        int pageFaults = simulateFIFO(pages, numFrames);
        System.out.println("\nTotal Page Faults: " + pageFaults);
        System.out.println("Total Page Hits: " + (numPages - pageFaults));
        System.out.printf("Hit Ratio: %.2f%%\n", ((double)(numPages - pageFaults)/numPages) * 100);
        
        scanner.close();
    }
    
    private static int simulateFIFO(int[] pages, int numFrames) {
        Queue<Integer> frame = new LinkedList<>();
        int pageFaults = 0;
        
        System.out.println("\nPage Replacement Process:");
        System.out.println("-------------------------");
        
        for (int page : pages) {
            boolean isHit = frame.contains(page);
            
            // Display current page being processed
            System.out.print("Reference " + page + " -> Frames: ");
            
            if (!isHit) {
                pageFaults++;
                
                if (frame.size() == numFrames) {
                    frame.poll(); // Remove the oldest page if frames are full
                }
                frame.add(page); // Add new page
            }
            
            // Display current state of frames
            for (Integer frameContent : frame) {
                System.out.print(frameContent + " ");
            }
            
            // Fill empty frames with dashes
            for (int j = frame.size(); j < numFrames; j++) {
                System.out.print("- ");
            }
            
            // Show if it was a hit or fault
            if (isHit) {
                System.out.println(" (Hit)");
            } else {
                System.out.println(" (Fault)");
            }
        }
        
        return pageFaults;
    }
}