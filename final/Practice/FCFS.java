
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

class Process {

    int id, AT, BT, ST, FT, TAT, WT;

    public Process(int id, int AT, int BT) {
        this.id = id;
        this.AT = AT;
        this.BT = BT;
    }

    public void calculateTimes(int currTime) {
        this.ST = Math.max(currTime, AT);
        this.FT = ST + BT;
        this.TAT = FT - AT;
        this.WT = TAT - BT;
    }
}

public class FCFS {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter total no of processes: ");
        int n = scan.nextInt();

        Process processes[] = new Process[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Enter Arrival Time for process " + (i + 1) + ": ");
            int arrivalTime = scan.nextInt();
            System.out.print("Enter CPU TIME for process " + (i + 1) + ": ");
            int burstTime = scan.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
            System.out.println();
        }

        Arrays.sort(processes, Comparator.comparingInt(p -> p.AT));

        int currentTime = 0;
        for (Process p : processes) {
            p.calculateTimes(currentTime);
            currentTime = p.FT;
        }

        System.out.println("Process\tArrival Time\tBurst Time\tStart Time\tFinish Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d%n", p.id, p.AT, p.BT, p.ST, p.FT, p.TAT, p.WT);
        }

        double avgTurnaroundTime = Arrays.stream(processes).mapToDouble(p -> p.TAT).average().orElse(0.0);
        double avgWaitingTime = Arrays.stream(processes).mapToDouble(p -> p.WT).average().orElse(0.0);

        System.out.printf("\nAverage Turnaround Time: %.2f", avgTurnaroundTime);
        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);

    }
}
