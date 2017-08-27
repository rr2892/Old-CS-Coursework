import java.util.ArrayList;


public class LIFOTable implements FrameTable{
	
	int frameCount;
	ArrayList<int[]> table;
	
	public LIFOTable(int M, int P) {
		this.frameCount = M / P;
		this.table = new ArrayList<int[]>();
	}

	@Override public void replaceFrame(Process[] processes, int page, int process, int time) {
		if (frameCount == table.size()) {
			int[] evicted = table.get(0);
			int evictedIndex = evicted[1];
			Process toBoot = processes[evictedIndex - 1];
			toBoot.evictTimes++;
			toBoot.resTimes += time - evicted[2];
	
			table.remove(0);
		} 

		int[] newFrame = {page, process, time}; 
		table.add(0, newFrame);
	}
	
	@Override public boolean faulted(int page, int process, int time) {
		for (int i = 0; i < table.size(); i++) {
			int[] pageInFrame = table.get(i);
			
			if ((pageInFrame[0] == page) && (pageInFrame[1] == process)) {
				return false;
			}
		}

		return true;
	}

	
	
}
