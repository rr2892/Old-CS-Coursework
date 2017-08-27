/**
 *  Rohan Reddy
 *  Lab 2
 *  SJF.java
 *
 */
import java.util.*;

public class SJF extends FCFS {
    public SJF(Process[] p) {
        super(p, "The scheduling algorithm used was SJF\n");
    }

    public void run(boolean verbose){
        
        
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

            
            //Takes care of the sorting by shortest job first whenever a new process will start running. Also includes code for tiebreakers

            if (this.inProgress == null) {
                
                for (int i = 0; i < queue.size(); i++) {
                    if (queue.get(i).state == State.READY) {
                        Collections.sort(queue, new Comparator<Process>() {
                            @Override
                            public int compare(Process process1, Process process2) {
                                int test = FCFS.compare(process1.timeNeeded-process1.workDone,
                                                       process2.timeNeeded-process2.workDone);
                                if(test == 0){
                                    test = FCFS.compare(process1.arrivalTime, process2.arrivalTime);
                                    if(test == 0){
                                        test = FCFS.compare(inputList.indexOf(process1), inputList.indexOf(process2));
                                    }
                                }
                                return test;
                            }
                        });
                        processDequeue(0);
                        break;
                    }
                }
            }
            ioCounted = false;
            clock++;
        }
        
        
        clock -= 1;
    }


}