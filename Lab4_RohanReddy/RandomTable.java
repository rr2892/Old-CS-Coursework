import java.util.Scanner;


public class RandomTable implements FrameTable {

	int frameCount;
	Scanner scan;
	int[][] table;
	
	public RandomTable(int M, int P, Scanner scan){
		this.frameCount = M / P;
		this.scan = scan;
		this.table = new int[frameCount][3];
		
	}

	@Override public void replaceFrame(Process[] processes, int page, int process, 
			int time) {

		for (int i = (frameCount - 1); i >= 0; i--) {
			if ((table[i][0] == 0) && (table[i][1] == 0)) {
				table[i][0] = page;
				table[i][1] = process;
				table[i][2] = time;
				return;
			}
		}	

		int rand = scan.nextInt();
		int evictedFrame = rand % frameCount;


		int evictedIndex = table[evictedFrame][1];
		Process toBoot = processes[evictedIndex - 1];
		toBoot.evictTimes++;
		toBoot.resTimes += time - table[evictedFrame][2];
		
		table[evictedFrame][0] = page;
		table[evictedFrame][1] = process;
		table[evictedFrame][2] = time;
		
	}	

	@Override public boolean faulted(int page, int process, int time) {
		for (int i = 0; i < frameCount; i++) {

			if ((table[i][0] == page) && (table[i][1] == process)) {
				return false;
			}
		}
		return true;
	}


}