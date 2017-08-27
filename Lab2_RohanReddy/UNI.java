/**
 *  Rohan Reddy
 *  Lab 2
 *  UNI.java
 *
 */
public class UNI extends FCFS {

    public UNI(Process[] p) {
        super(p, "\n\nThe scheduling algorithm used was UNI\n\n");
    }

    @Override
    public void run(boolean verbose) {
        
        
        boolean ioCounted = false;
        int i = 0;
        Process current = inputList.get(i);
        boolean done = false;


        //Custom run implementation for Uniprocessing
        while(!done) {
            
            
            verboseMode(verbose);


            if((current.state == State.UNSTARTED || current.state == State.READY)){
                current.run();
            }



            for(int j = 0; j < inputList.size(); j++)
            {
                if(inputList.get(j).state == State.UNSTARTED && clock >= inputList.get(j).arrivalTime){
                    inputList.get(j).state = State.READY;
                }

                if(inputList.get(j).state == State.READY){
                    inputList.get(j).cpuWait();
                }
            }
            


            if (current.state == State.RUNNING) {
                processRunning(current);
            } else if (current.state == State.BLOCKED) {
                ioCounted = processBlocked(current, ioCounted);
            }
            

            if(current.state == State.TERMINATED){
                current.finishTime++;

                if(i == inputList.size()-1){
                    done = true;
                }
                else{
                    current = inputList.get(++i);
                }
            }
        

 
            ioCounted = false;
            
            clock++;
        }
        
        
        
    }



   
    @Override
    public void processUnblock(Process current) {
        current.run();
    }

}