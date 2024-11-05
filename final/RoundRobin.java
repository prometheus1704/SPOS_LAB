import java.util.*;

class Process {
    int id, AT, BT, ST, FT, TAT, WT, remainingTime;
    ArrayList<Integer> startTimes, finishTimes;

    public Process(int id, int AT, int BT) {
        this.id = id;
        this.AT = AT;
        this.BT = BT;
        this.remainingTime = BT;
        this.startTimes = new ArrayList<>();
        this.finishTimes = new ArrayList<>();
        this.ST = -1; // Start Time initialized to -1
    }

    public void calculateTimes() {
        this.ST = startTimes.get(0); // First time CPU was allocated
        this.FT = finishTimes.get(finishTimes.size() - 1);
        this.TAT = FT - AT;
        this.WT = TAT - BT;
    }
}

public class RoundRobin {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter total no of processes: ");
        int n = scan.nextInt();

        System.out.println("Enter time quantum: ");
        int quantum = scan.nextInt();

        Process[] processes = new Process[n];
        Queue<Process> readyQueue = new LinkedList<>();
        
        // Input process details
        for (int i = 0; i < n; i++) {
            System.out.print("Enter Arrival Time for process " + (i + 1) + ": ");
            int arrivalTime = scan.nextInt();
            System.out.print("Enter CPU TIME for process " + (i + 1) + ": ");
            int burstTime = scan.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
            System.out.println();
        }

        // Sort processes by arrival time
        Arrays.sort(processes, Comparator.comparingInt(p -> p.AT));

        int currentTime = 0;
        int completed = 0;
        Process[] arrivalOrder = processes.clone();
        int arrivalIndex = 0;

        while (completed < n) {
            // Check for newly arrived processes
            while (arrivalIndex < n && arrivalOrder[arrivalIndex].AT <= currentTime) {
                readyQueue.offer(arrivalOrder[arrivalIndex]);
                arrivalIndex++;
            }

            if (readyQueue.isEmpty()) {
                currentTime = arrivalOrder[arrivalIndex].AT;
                continue;
            }

            Process current = readyQueue.poll();

            // Record start time for this quantum
            current.startTimes.add(currentTime);

            int executeTime = Math.min(quantum, current.remainingTime);
            current.remainingTime -= executeTime;
            currentTime += executeTime;

            // Record finish time for this quantum
            current.finishTimes.add(currentTime);

            // Check for newly arrived processes again
            while (arrivalIndex < n && arrivalOrder[arrivalIndex].AT <= currentTime) {
                readyQueue.offer(arrivalOrder[arrivalIndex]);
                arrivalIndex++;
            }

            if (current.remainingTime > 0) {
                readyQueue.offer(current);
            } else {
                completed++;
            }
        }

        // Calculate final times for each process
        for (Process p : processes) {
            p.calculateTimes();
        }

        // Print results
        System.out.println("\nProcess\tArrival Time\tBurst Time\tStart Time\tFinish Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\n", 
                p.id, p.AT, p.BT, p.ST, p.FT, p.TAT, p.WT);
        }

        // Calculate and print averages
        double avgTurnaroundTime = Arrays.stream(processes).mapToDouble(p -> p.TAT).average().orElse(0.0);
        double avgWaitingTime = Arrays.stream(processes).mapToDouble(p -> p.WT).average().orElse(0.0);

        System.out.printf("\nAverage Turnaround Time: %.2f", avgTurnaroundTime);
        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
    }
}

// 0 5
// 1 4
// 2 2
// 4 1