/**
 *  Rohan Reddy
 *  Lab 2
 *  Process.java
 *
 */

public class Process implements Comparable<Process> {
    
    public State state;

    public int waitTime;
    public int ioTime;
    public int finishTime;

    //A B C and M 
    public int arrivalTime;  
    public int maximumInterval; 
    public int timeNeeded; 
    public int multiplier; 

    
    public int burstRemaining;
    
    public int finalCPUBurst;
    
    
    public int workDone;
    
    public int burstRemainder;

    
    public Process(int A, int B, int C, int M) {
        this.arrivalTime = A;
        this.maximumInterval = B;
        this.timeNeeded = C;
        this.multiplier = M;
        this.burstRemaining = 0;
        this.ioTime = 0;
        this.finishTime = 0;
        this.waitTime = 0;
        this.state = State.UNSTARTED;
        this.workDone = 0;
    }

    
    public void run() {
        
        if (this.burstRemainder != 0) {
            this.burstRemaining = this.burstRemainder;
        }
        
        else {
            this.burstRemaining = this.setupCPUBurst();
        }
        this.burstRemainder = 0;
        this.state = State.RUNNING;
    }


    public void block() {
        this.burstRemaining = this.setupIOBurst();
        this.state = State.BLOCKED;
    }



    public void cpuWait() {
        this.waitTime += 1;
        this.state = State.READY;
    }

    
    //Do all processing for the bursts
    public boolean work() {
        if (this.state == State.RUNNING) {
            this.workDone++;
            this.burstRemaining--;
            if (this.workDone == this.timeNeeded) {
                return true;
            }
        } else if (this.state == State.BLOCKED) {
            this.ioTime++;
            this.burstRemaining--;
        }
        return false;
    }

    

    public void terminate(int time) {
        this.finishTime = time;
        this.state = State.TERMINATED;
    }



    public int setupCPUBurst() {
        
        int t = Scheduler.randomOS(this.maximumInterval);
        
        if (t > this.timeRemaining()) {
            this.finalCPUBurst = this.timeRemaining();
        }
        
        else {
            this.finalCPUBurst = t;
        }

        return this.finalCPUBurst;
    }

    public int setupIOBurst() {
        return this.finalCPUBurst * this.multiplier;
    }

    
    

    public int timeRemaining() {
        return this.timeNeeded - this.workDone;
    }



    
    public int compareTo(Process p) {
        if (this.arrivalTime < p.arrivalTime) {
            return -1;
        } else if (this.arrivalTime > p.arrivalTime) {
            return 1;
        } else {
            return 0;
        }

    }
}