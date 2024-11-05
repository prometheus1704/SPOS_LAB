import java.util.*;

public class OptimalPaging {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Enter the no. of pages: ");
        int pagecnt = scan.nextInt();
        
        System.out.println("Enter the frame no.: ");
        int framecnt = scan.nextInt();
        
        int[] pages = new int[pagecnt];
        System.out.println("Enter the reference string:");
        for(int i = 0; i < pagecnt; i++) {
            pages[i] = scan.nextInt();
        }
        
        int pageFaults = simulateOptimal(pages, framecnt);
        System.out.println("\nTotal Page Faults: " + pageFaults);
        System.out.println("Total Page Hits: " + (pagecnt - pageFaults));
        System.out.printf("Hit Ratio: %.2f%%\n", ((double)(pagecnt - pageFaults)/pagecnt) * 100);
    }
    
    public static int simulateOptimal(int[] pages, int framecnt) {
        ArrayList<Integer> frame = new ArrayList<>(framecnt);
        int pageFaults = 0;
        
        System.out.println("\nPage Replacement Process:");
        System.out.println("-------------------------");
        
        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];
            boolean isHit = frame.contains(page);
            
            System.out.print("Reference " + page + " -> Frames: ");
            
            // Handle page fault
            if (!isHit) {
                pageFaults++;
                
                if (frame.size() == framecnt) {
                    int index = findOptimalPage(frame, pages, i);
                    frame.remove(index);
                }
                frame.add(page);
            }
            
            // Display current state of frames
            for (Integer frameContent : frame) {
                System.out.print(frameContent + " ");
            }
            
            // Fill empty frames with dashes
            for (int j = frame.size(); j < framecnt; j++) {
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
    
    private static int findOptimalPage(ArrayList<Integer> frame, int[] pages, int currentIndex) {
        int farthest = -1;
        int index = 0;
        
        // Check each frame
        for (int i = 0; i < frame.size(); i++) {
            int currentPage = frame.get(i);
            int j;
            
            // Find the next occurrence of current page
            for (j = currentIndex + 1; j < pages.length; j++) {
                if (currentPage == pages[j]) {
                    if (j > farthest) {
                        farthest = j;
                        index = i;
                    }
                    break;
                }
            }
            
            // If page is not found in future, return its index
            if (j == pages.length) {
                return i;
            }
        }
        
        return index;
    }
}