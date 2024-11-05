import java.util.*;
import java.util.Scanner;

public class MemoryManagement {
    // First Fit Allocation
    static void firstFit(int blockSize[], int blocks, int processSize[], int processes) {
        int allocation[] = new int[processes];

        // Initially no process is allocated to any block
        for (int i = 0; i < processes; i++) {
            allocation[i] = -1;
        }

        // Allocate memory to processes
        for (int i = 0; i < processes; i++) {
            for (int j = 0; j < blocks; j++) {
                if (blockSize[j] >= processSize[i]) {
                    allocation[i] = j;
                    blockSize[j] -= processSize[i]; // Reduce block size
                    break;
                }
            }
        }
        printAllocation(allocation, processSize);
    }

    // Next Fit Allocation
    static void nextFit(int blockSize[], int blocks, int processSize[], int processes) {
        int allocation[] = new int[processes];
        for (int i = 0; i < processes; i++) {
            allocation[i] = -1;
        }

        int j = 0; // Start from the first block
        for (int i = 0; i < processes; i++) {
            while (j < blocks) {
                if (blockSize[j] >= processSize[i]) {
                    allocation[i] = j;
                    blockSize[j] -= processSize[i];
                    break;
                }
                j = (j + 1) % blocks; // Move to next block in a circular way
            }
        }

        printAllocation(allocation, processSize);
    }

    // Best Fit Allocation
    static void bestFit(int blockSize[], int blocks, int processSize[], int processes) {
        int allocation[] = new int[processes];
        for (int i = 0; i < processes; i++) {
            allocation[i] = -1;
        }

        for (int i = 0; i < processes; i++) {
            int bestIndex = -1;

            for (int j = 0; j < blocks; j++) {
                if (blockSize[j] >= processSize[i] && (bestIndex == -1 || blockSize[j] < blockSize[bestIndex])) {
                    bestIndex = j; // Update bestIndex to the smaller suitable block
                }
            }

            if (bestIndex != -1) {
                allocation[i] = bestIndex;
                blockSize[bestIndex] -= processSize[i];
            }
           
        }
        printAllocation(allocation, processSize);
    }

    // Worst Fit Allocation
    static void worstFit(int blockSize[], int blocks, int processSize[], int processes) {
        int allocation[] = new int[processes];
        for (int i = 0; i < processes; i++) {
            allocation[i] = -1;
        }

        for (int i = 0; i < processes; i++) {
            int worstIndex = -1;
            for (int j = 0; j < blocks; j++) {
                if (blockSize[j] >= processSize[i]) {
                    if (worstIndex == -1 || blockSize[j] > blockSize[worstIndex]) {
                        worstIndex = j;
                    }
                }
            }

            if (worstIndex != -1) {
                allocation[i] = worstIndex;
                blockSize[worstIndex] -= processSize[i];
            }
        }
        printAllocation(allocation, processSize);
    }


    

    // Function to print allocation result
    static void printAllocation(int allocation[], int processSize[]) {
        System.out.println("Process No.\tProcess Size\tBlock No.");
        for (int i = 0; i < processSize.length; i++) {
            System.out.print((i + 1) + "\t\t" + processSize[i] + "\t\t");
            if (allocation[i] != -1) {
                System.out.println(allocation[i] + 1);
            } else {
                System.out.println("Not Allocated");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input process information
        System.out.println("Enter the number of processes: ");
        int processes = sc.nextInt();
        int processSize[] = new int[processes];
        System.out.println("Enter the process sizes: ");
        for (int i = 0; i < processes; i++) {
            processSize[i] = sc.nextInt();
        }

        // Input block information
        System.out.println("Enter the number of blocks: ");
        int blocks = sc.nextInt();
        int blockSize[] = new int[blocks];
        System.out.println("Enter the block sizes: ");
        for (int i = 0; i < blocks; i++) {
            blockSize[i] = sc.nextInt();
        }

        // Display menu and choose strategy
        int choice = 0;

        do {
            System.out.println("Choose a memory allocation strategy: ");
            System.out.println("1. First Fit\n2. Next Fit\n3. Best Fit\n4. Worst Fit");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    firstFit(blockSize.clone(), blocks, processSize, processes);
                    break;
                case 2:
                    nextFit(blockSize.clone(), blocks, processSize, processes);
                    break;
                case 3:
                    bestFit(blockSize.clone(), blocks, processSize, processes);
                    break;
                case 4:
                    worstFit(blockSize.clone(), blocks, processSize, processes);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 5);
        sc.close();
    }
}
