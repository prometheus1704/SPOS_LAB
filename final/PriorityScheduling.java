import java.util.*;

class Process {
    int id, AT, BT, priority, ST, FT, TAT, WT;
    boolean isCompleted;

    public Process(int id, int AT, int BT, int priority) {
        this.id = id;
        this.AT = AT;
        this.BT = BT;
        this.priority = priority;
        this.isCompleted = false;
    }

    public void calculateTimes() {
        this.TAT = FT - AT;
        this.WT = TAT - BT;
        
    }
}

public class PriorityScheduling {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter total no of processes: ");
        int n = scan.nextInt();

        Process[] processes = new Process[n];
        
        // Input process details
        for (int i = 0; i < n; i++) {
            System.out.print("Enter Arrival Time for process " + (i + 1) + ": ");
            int arrivalTime = scan.nextInt();
            System.out.print("Enter CPU TIME for process " + (i + 1) + ": ");
            int burstTime = scan.nextInt();
            System.out.print("Enter Priority for process " + (i + 1) + " (0 is highest priority): ");
            int priority = scan.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime, priority);
            System.out.println();
        }

        int currentTime = 0;
        int completed = 0;

        while (completed < n) {
            // Find process with highest priority among arrived processes
            int highestPriorityIndex = -1;
            int highestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!processes[i].isCompleted && processes[i].AT <= currentTime) {
                    if (processes[i].priority < highestPriority) {
                        highestPriority = processes[i].priority;
                        highestPriorityIndex = i;
                    }
                    // If priorities are equal, choose the one that arrived first
                    else if (processes[i].priority == highestPriority && 
                             processes[i].AT < processes[highestPriorityIndex].AT) {
                        highestPriorityIndex = i;
                    }
                }
            }

            if (highestPriorityIndex == -1) {
                // No process available, move to next arrival time
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : processes) {
                    if (!p.isCompleted && p.AT < nextArrival) {
                        nextArrival = p.AT;
                    }
                }
                currentTime = nextArrival;
                continue;
            }

            Process currentProcess = processes[highestPriorityIndex];
            
            // Set start time if process is starting for the first time
            if (!currentProcess.isCompleted && currentProcess.ST == 0) {
                currentProcess.ST = currentTime;
            }

            // Execute process
            currentTime += currentProcess.BT;
            currentProcess.FT = currentTime;
            currentProcess.isCompleted = true;
            completed++;

            // Calculate times for completed process
            currentProcess.calculateTimes();
        }

        // Print results
        System.out.println("\nProcess\tPriority\tArrival Time\tBurst Time\tStart Time\tFinish Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\n", 
                p.id, p.priority, p.AT, p.BT, p.ST, p.FT, p.TAT, p.WT);
        }

        // Calculate and print averages
        double avgTurnaroundTime = Arrays.stream(processes).mapToDouble(p -> p.TAT).average().orElse(0.0);
        double avgWaitingTime = Arrays.stream(processes).mapToDouble(p -> p.WT).average().orElse(0.0);
       
        System.out.printf("\nAverage Turnaround Time: %.2f", avgTurnaroundTime);
        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
        
    }
}

// Process Priority        Arrival Time   burst time 
// 1       2               0              3
// 2       6               2              5
// 3       3               1              4
// 4       5               4              2
// 5       7               6              9 
// 6       4               5              4
// 7       10              7              10