import java.util.ArrayList;
import java.util.List;

class SPOS3_SJF{
    public static List<process> processes = new ArrayList<>();
    public static List<process> ganttChart = new ArrayList<>();
    public static int totalTurnAroundTime = 0;
    public static int totalWaitTime = 0;

    static void showGantt(){
        System.out.println("Gantt chart");
        System.out.print(ganttChart.get(0).arrivalTime + " " + ganttChart.get(0).processName + " " + ganttChart.get(0).CPUTime);
        for(int i=1;i<ganttChart.size();i++){
            System.out.print(" " + ganttChart.get(i).processName + " " + ganttChart.get(i).CPUTime);
        }
        System.out.println();
    }

    public static void showCompleteChart() {
        System.out.printf("%-10s%-10s%-10s%-10s%-10s%-12s%-10s%n", 
                          "Process", "Arrival", "CPU", "Start", "Finish", 
                          "Turnaround", "Waiting");
    
        int totalTurnAroundTime = 0;
        int totalWaitTime = 0;
    
        // Iterating through processes
        // check here if the same formula works or not (for turnaround time)
        for (int i = 0; i < processes.size(); i++) {
            int turnaroundTime = ganttChart.get(i).CPUTime - processes.get(i).arrivalTime;
            int waitTime = turnaroundTime - processes.get(i).CPUTime;
    
            totalTurnAroundTime += turnaroundTime;
            totalWaitTime += waitTime;
    
            // Print the process details
            System.out.printf("%-10s%-10d%-10d%-10d%-10d%-12d%-10d%n",
                              processes.get(i).processName,  // Process name
                              processes.get(i).arrivalTime,  // Arrival time
                              processes.get(i).CPUTime,      // CPU time
                              ganttChart.get(i).arrivalTime, // Start time
                              ganttChart.get(i).CPUTime,     // Finish time
                              turnaroundTime,                // Turnaround time
                              waitTime);                     // Wait time
        }
    
        // Calculating averages
        double averageTurnAroundTime = (double) totalTurnAroundTime / processes.size();
        double averageWaitTime = (double) totalWaitTime / processes.size();
    
        // Print totals and averages
        System.out.println();
        System.out.printf("Total    Turn-Around   Time : %d%n", totalTurnAroundTime);
        System.out.printf("Average  Turn-Around   Time : %.2f%n", averageTurnAroundTime);
        System.out.printf("Total    Wait          Time : %d%n", totalWaitTime);
        System.out.printf("Average  Wait          Time : %.2f%n", averageWaitTime);
    }
    

    static public void SJF(){
        processes.sort((p1, p2)->p1.CPUTime-p2.CPUTime);
        int start = processes.get(0).arrivalTime;
        int end = start + processes.get(0).CPUTime;

        ganttChart.add(new process(processes.get(0).processName, start, end));
        for(int i=1;i<processes.size();i++){
            process p = processes.get(i);
            int processArrival = p.arrivalTime;
            int processCPU = p.CPUTime;
            if(processArrival > end){
                start = processArrival;
                end += start + processCPU;
                ganttChart.add(new process(p.processName, start, end));
            }else{
                start = end;
                end += processCPU;
                ganttChart.add(new process(p.processName, start, end));
            }
        }
        showGantt();
        showCompleteChart();

    }

    public static void main(String[] args) {
        processes.add(new process("P1", 0, 3));
        processes.add(new process("P2", 4, 1));
        processes.add(new process("P3", 3, 2));
        processes.add(new process("P4", 1, 5));
        processes.add(new process("P5", 12, 5));

        SJF();

    }
}
class process{
    String processName;
    int arrivalTime;
    int CPUTime;
    process(String processName, int arrivalTime, int CPUTime){
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.CPUTime = CPUTime;
    }
};