import java.util.*;

class Process {
    int id, AT, BT, ST, FT, TAT, WT;
    int remainingTime;    // Added for preemptive
    
    public Process(int id, int AT, int BT) {
        this.id = id;
        this.AT = AT;
        this.BT = BT;
        this.remainingTime = BT;  // Initially, remaining time = burst time
    }
}

public class SRTF {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Enter total no of processes: ");
        int n = scan.nextInt();
        
        Process processes[] = new Process[n];
        int totalBurstTime = 0;
        
        // Input processes
        for (int i = 0; i < n; i++) {
            System.out.print("Enter Arrival Time for process " + (i + 1) + ": ");
            int arrivalTime = scan.nextInt();
            System.out.print("Enter CPU TIME for process " + (i + 1) + ": ");
            int burstTime = scan.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
            totalBurstTime += burstTime;
            System.out.println();
        }
        
        // Variables for tracking execution
        int currentTime = 0;
        int completed = 0;
        int[] startTime = new int[n];
        int[] finishTime = new int[n];
        boolean[] started = new boolean[n];
        
        System.out.println("\nGantt Chart:");
        int prevProcess = -1;  
        
        
        while (completed < n) {
            int shortest = -1;
            int minRemaining = Integer.MAX_VALUE;
            
        
            for (int i = 0; i < n; i++) {
                if (processes[i].AT <= currentTime && processes[i].remainingTime > 0) {
                    if (processes[i].remainingTime < minRemaining) {
                        minRemaining = processes[i].remainingTime;
                        shortest = i;
                    }
                }
            }
            
            
            if (shortest == -1) {
                currentTime++;
                continue;
            }
            
           
            if (shortest != prevProcess) {
                System.out.print(currentTime + " P" + processes[shortest].id + " ");
                prevProcess = shortest;
            }
            
          
            if (!started[shortest]) {
                started[shortest] = true;
                startTime[shortest] = currentTime;
            }
            
            // Execute process for 1 time unit
            processes[shortest].remainingTime--;
            currentTime++;
            
            // If process completes
            if (processes[shortest].remainingTime == 0) {
                completed++;
                finishTime[shortest] = currentTime;
                
                // Calculate times
                processes[shortest].FT = finishTime[shortest];
                processes[shortest].ST = startTime[shortest];
                processes[shortest].TAT = processes[shortest].FT - processes[shortest].AT;
                processes[shortest].WT = processes[shortest].TAT - processes[shortest].BT;
            }
        }
        
        System.out.println(currentTime); // Final time for Gantt chart
        
        // Print process details
        System.out.println("\nProcess\tArrival Time\tBurst Time\tStart Time\tFinish Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\n", 
                p.id, p.AT, p.BT, p.ST, p.FT, p.TAT, p.WT);
        }
        
        
        double avgTAT = Arrays.stream(processes).mapToDouble(p -> p.TAT).average().orElse(0.0);
        double avgWT = Arrays.stream(processes).mapToDouble(p -> p.WT).average().orElse(0.0);
        
        System.out.printf("\nAverage Turnaround Time: %.2f", avgTAT);
        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWT);
    }
}

// 0 5
// 1 3
// 2 4
// 4 1