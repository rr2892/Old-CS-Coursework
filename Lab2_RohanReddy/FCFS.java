/**
 *  Rohan Reddy
 *  Lab 2
 *  FCFS.java
 *
 */

import java.util.*;

public class FCFS {
    
    Process inProgress;
    

    ArrayList<Process> inputList, queue;
    
    
    int initialProcesses, finalProcesses;
    
    int cpuUsage, ioUsage;
 
    int clock, quantum;


    String methodText = "The scheduling algorithm used was First Come First Serve\n";

    public FCFS(Process[] inputList, String methodText) {
        this.inputList = new ArrayList<Process>(Arrays.asList(inputList));
        this.queue = new ArrayList<Process>(this.inputList.size());
        this.initialProcesses = this.inputList.size();
        finalProcesses = 0;
        clock = 0;
        this.inProgress = null;
        this.ioUsage = 0;
        this.cpuUsage = 0;
        this.quantum = 0;
        if (!methodText.isEmpty()) {
            this.methodText = methodText;
        }
    }


    //Prints results    
    public void viewResults() {
        System.out.printf(this.methodText);
        int totalTimeSpentWaiting = 0;
        int totalTurnaroundTime = 0;
        
        for (int i = 0; i < inputList.size(); i++) {
            Process p = inputList.get(i);
            System.out.printf("Process: %d\n", i);
            System.out.printf("    (A, B, C, M) = (%d, %d, %d, %d)\n", p.arrivalTime, p.maximumInterval, p.timeNeeded, p.multiplier);
            System.out.printf("    Finishing time: %d\n", p.finishTime);
            System.out.printf("    Turnaround time: %d\n", p.finishTime - p.arrivalTime);
            System.out.printf("    I/O time: %d\n", p.ioTime);
            System.out.printf("    Waiting time: %d\n", p.waitTime);
            totalTurnaroundTime += p.finishTime - p.arrivalTime;
            totalTimeSpentWaiting += p.waitTime;

        }
        System.out.printf("\n");
        
        System.out.printf("Summary Data:\n");
        System.out.printf("    Finishing time: %d\n", clock);
        System.out.printf("    CPU Utilization: %f\n", cpuUsage / (clock + 0.0));
        System.out.printf("    I/O Utilization: %f\n", ioUsage / (clock + 0.0));
        System.out.printf("    Throughput: %f\n", (inputList.size() / (clock + 0.0)) * 100.0);
        System.out.printf("    Average turnaround time: %f\n", totalTurnaroundTime / (inputList.size() + 0.0));
        System.out.printf("    Average waiting time: %f\n", totalTimeSpentWaiting / (inputList.size() + 0.0));
        System.out.printf("\n");
    }

    
    //Handles the verbose input tag
    public void verboseMode(boolean verbose) {
        if (verbose) {
            System.out.printf("Before cycle %5d:", clock);
            for (int i = 0; i < inputList.size(); i++) {
                System.out.printf("%11s  %2d", inputList.get(i).state, inputList.get(i).burstRemaining);
            }
            System.out.println();
        }
    }

    //Process handlers


    public void processNotFinished(Process current) {
        if (current.burstRemaining == 0) {
            current.block();
            inProgress = null;
        }
    }

    
    public void processRunning(Process current) {
        
        cpuUsage++;
        
        boolean isDone = current.work();
        if (isDone) {
            current.terminate(clock);
            finalProcesses++;
            inProgress = null;
        } else {
            processNotFinished(current);
        }
    }
    

    
    public void processReady(Process current) {
        current.cpuWait();
    }

    
    public void addToQueue(Process current) {
        queue.add(current);
    }

    
    public void processUnblock(Process current) {
        current.state = State.READY;
        addToQueue(current);
    }

    
    public boolean processBlocked(Process current, boolean ioCounted) {
        if (!ioCounted) {
            ioUsage++;
            ioCounted = true;
        }
        current.work();
        if (current.burstRemaining == 0) {
            processUnblock(current);
        }
        return ioCounted;
    }

    
    public void processUnstarted(Process current) {
        if (current.arrivalTime == clock) {
            current.state = State.READY;
            addToQueue(current);
        }
    }

    
    public void processDequeue(int i) {
        this.inProgress = queue.get(i);
        queue.get(i).run();
        
        
        quantum = 2;
        queue.remove(i);
    }


    //Core method
    public void run(boolean verbose) {
        
        
        boolean ioCounted = false;
        while (initialProcesses != finalProcesses) {
            
            verboseMode(verbose);
            
            for (int i = 0; i < inputList.size(); i++) {
                
                Process current = inputList.get(i);
                if (current.state == State.RUNNING) {
                    processRunning(current);
                } else if (current.state == State.READY) {
                    processReady(current);
                } else if (current.state == State.BLOCKED) {
                    ioCounted = processBlocked(current, ioCounted);
                } else if (current.state == State.UNSTARTED) {
                    processUnstarted(current);
                }
            }

            
            
            if (this.inProgress == null) {
                for (int i = 0; i < queue.size(); i++) {
                    if (queue.get(i).state == State.READY) {
                        processDequeue(i);
                        break;
                    }
                }
            }
            ioCounted = false;
            
            clock++;
        }
        
        
        clock -= 1;
    }

    public static int compare(int x, int y) {
      return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

}