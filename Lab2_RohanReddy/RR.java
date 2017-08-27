/**
 *  Rohan Reddy
 *  Lab 2
 *  RR.java
 *
 */
public class RR extends FCFS {

    public RR(Process[] p) {
        super(p, "\n\nThe scheduling algorithm used was RR(q=2)\n\n");
    }

    
    
    @Override
    public void processNotFinished(Process current) {
        if (current.burstRemaining == 0) {
            current.block();
            inProgress = null;
        } else {
            quantum--;
            
            if (quantum == 0) {
                
                current.burstRemainder = current.burstRemaining;
                current.state = State.READY;
                queue.add(current);
                inProgress = null;
            }
        }
    }
}