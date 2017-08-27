
public interface FrameTable {

	void replaceFrame(Process[] processes, int page, int process, int time);
	boolean faulted(int page, int process, int time);
}