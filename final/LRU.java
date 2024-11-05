import java.util.*;

public class LRU {
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
        
        int pageFaults = simulateLRU(pages, framecnt);
        System.out.println("\nTotal Page Faults: " + pageFaults);
        System.out.println("Total Page Hits: " + (pagecnt - pageFaults));
        System.out.printf("Hit Ratio: %.2f%%\n", ((double)(pagecnt - pageFaults)/pagecnt) * 100);
    }
    
    public static int simulateLRU(int[] pages, int framecnt) {
        LinkedHashMap<Integer, Integer> frame = new LinkedHashMap<>(framecnt, 0.75f, true);
        int pageFaults = 0;
        
        System.out.println("\nPage Replacement Process:");
        System.out.println("-------------------------");
        
        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];
            boolean isHit = frame.containsKey(page);
            
            System.out.print("Reference " + page + " -> Frames: ");
            
            // Handle page fault
            if (!isHit) {
                pageFaults++;
                
                if (frame.size() == framecnt) {
                    Integer lru = frame.keySet().iterator().next();
                    frame.remove(lru);
                }
                frame.put(page, 1);
            } 
            // Handle page hit
            else {
                // Move the page to the most recently used position
                frame.remove(page);
                frame.put(page, 1);
            }
            
            // Display current state of frames
            for (Integer frameContent : frame.keySet()) {
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
}