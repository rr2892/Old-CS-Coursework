
public class LRUTable implements FrameTable{
	int frameCount;
	int table[][];
	
	public LRUTable(int M, int P){
		frameCount = M / P;
		table = new int[frameCount][4];
	}


	@Override public void replaceFrame(Process[] processes, int page, int process, 
			int time) {
		int oldestTime = time;
		int newFrame = 0;
		
		for (int i = frameCount-1; i >= 0; i--) {
		
			if ((table[i][0] == 0) && (table[i][1] == 0)) {
				table[i][0] = page;
				table[i][1] = process;
				table[i][2] = time;
				table[i][3] = time;
				return;
			} 
			
			else if (oldestTime > table[i][2]) {
				newFrame = i;
				oldestTime = table[i][2];
			}
		}
		
		int evictedIndex = table[newFrame][1];
		Process toBoot = processes[evictedIndex - 1];
		toBoot.evictTimes++;
		toBoot.resTimes += time - table[newFrame][3];	
		
		table[newFrame][0] = page;
		table[newFrame][1] = process;
		table[newFrame][3] = table[newFrame][2] = time;
	}

	@Override public boolean faulted(int page, int process, int time) {
		for (int i = 0; i < frameCount; i++) {
			
			if ((table[i][0] == page) && (table[i][1] == process)) {
				table[i][2] = time; 
				return false;
			}
		}

		return true;
	}

	
	
}
