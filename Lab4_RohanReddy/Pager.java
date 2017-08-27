import java.util.Scanner;
import java.io.*;

public class Pager {

	Process[] processes;
	int quantum;
	int M, P, S, J, N;
	String R;
	FrameTable table;
	Scanner scan;
	
	public Pager(int M, int P, int S, int J, int N, String R, FrameTable table, Scanner scan){
		quantum = 3;
		this.M = M;
		this.P = P;
		this.S = S;
		this.J = J;
		this.N = N;
		this.R = R;
		this.table = table;
		this.scan = scan;
	}


	public static void main(String[] args){

		if(args.length != 6){
			System.out.println("Entered incorrect # of arguments. Please enter 6.");
			System.exit(0);
		}
		
		FrameTable table = null;
		Scanner scan = null;

		try{
			scan = new Scanner(new File("random-numbers.txt"));
		}catch(FileNotFoundException e){
			System.out.println("Random number file not found. Exiting.");
			System.exit(0);
		}

		int M = Integer.parseInt(args[0]);		//Machine size in words
		int P = Integer.parseInt(args[1]);		//Page size in words
		int S = Integer.parseInt(args[2]);		//Size of a process (references are to virtual addresses 0..S-1)
		int J = Integer.parseInt(args[3]);		//Job mix, describes A, B, and C
		int N = Integer.parseInt(args[4]);		//Number of references for each process
		String R = args[5].trim();				//Replacement algorithm: LIFO, RANDOM, or LRU


		if(R.equals("lru")){
			table = new LRUTable(M, P);
		}else if(R.equals("lifo")){
			table = new LIFOTable(M, P);
		}else if(R.equals("random")){
			table = new RandomTable(M, P, scan);
		}else{
			System.out.println("Please enter lru, lifo, or random for the replacement algorithm.");
			System.exit(0);
		}

		Pager pager = new Pager(M, P, S, J, N, R, table, scan);
		pager.page();
		pager.output();


	}
	
	
	
	void page() {

		if (J == 1) {
			processes = new Process[1];
			processes[0] = new Process(S, 1);
			
		} 
		

		else if(J==2 || J==3 || J==4 ){
			processes = new Process[4];
			for(int i = 0; i < 4; i++){
				processes[i] = new Process(S, i+1);
			}
		}
		
		else {
			System.out.println("Error: Job mix number should be 1-4.");
			System.exit(0);
		}


		if(J==1){

			for(int i = 1; i <= N; i++){
				int page = processes[0].next / P;

				if (table.faulted(page, 1, i)) {

					table.replaceFrame(processes, page, 1, i);
					processes[0].faultTimes++;
				}

				processes[0].reference(1, 0, 0, scan);

			}
		}else{

			int maxCycle = N / quantum;
			double A[] = new double[4];
			double B[] = new double[4];
			double C[] = new double[4];

		
			if(J == 2){
				for(int i=0;i<4;i++){
					A[i] = 1;
					B[i] = 0;
					C[i] = 0;
				}
			}

			else if(J == 3){		
				for(int i=0;i<4;i++){
					A[i] = 0;
					B[i] = 0;
					C[i] = 0;
				}	
			}

		
			else{
				A[0] = 0.75;
				B[0] = 0.25;  
				C[0] = 0;
				
				A[1] = 0.75;
				B[1] = 0;       
				C[1] = 0.25;

				A[2] = 0.75;
				B[2] = 0.125;
				C[2] = 0.125;

				A[3] = 0.5;  
				B[3] = 0.125;
				C[3] = 0.125;
			}
			

			for (int cycle = 0; cycle <= maxCycle; cycle++) {
				for(int j = 0; j < 4; j++){
					work(j+1, cycle, maxCycle, A[j], B[j], C[j]);
				}
			}

		}
	}


	

	void work(int process, int cycle, int maxCycle, double A, double B, double C){
		int refTimes;

		if(cycle != maxCycle){
			refTimes = quantum;
		}

		else{
			refTimes = N % quantum;
		}

		for (int ref = 0; ref < refTimes; ref++) {

			int time = quantum * cycle * 4 + ref + 1 + (process - 1) * refTimes;
			int page = processes[process - 1].next / P;
		
			if (table.faulted(page, process, time)) {
				table.replaceFrame(processes, page, process, time);
				processes[process - 1].faultTimes++;
			}
			processes[process - 1].reference(A, B, C, scan);
		}
	}


	
	void output() {

		int totalFaultTimes = 0, totalResTimes = 0, totalEvictTimes = 0;
		

		System.out.println("\nThe machine size is " + M);
		System.out.println("The page size is " + P);
		System.out.println("The process size is " + S);
		System.out.println("The job mix number is " + J);
		System.out.println("The number of references per process is " + N);
		System.out.println("The replacement algorithm is " + R + "\n");
		

		int faultTime, resTime, evictTime;

		for (int i = 0; i < processes.length; i++) {

			faultTime = processes[i].faultTimes;
			resTime = processes[i].resTimes;
			evictTime = processes[i].evictTimes;

			if (evictTime == 0) 
				System.out.println("Process #" + (i+1) + " had " + faultTime + " faults; no evictions. Average Residence: undefined.");
			else 
				System.out.println("Process #" + (i+1) + " had " + faultTime + " faults. Average Residence: " + (double)resTime/evictTime);
			

			totalFaultTimes += faultTime;
			totalResTimes += resTime;
			totalEvictTimes += evictTime;
		}
		
		
		if (totalEvictTimes == 0) 
			System.out.println("\nTotal # of Faults: " + totalFaultTimes + "; no evictions. Overall Average Residence: undefined.");
		else 
			System.out.println("\nTotal # of Faults: " + totalFaultTimes + ". Overall Average Residence: " 
				+ (double)totalResTimes/totalEvictTimes + "\n");	
	}
}
