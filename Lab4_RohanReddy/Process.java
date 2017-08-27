import java.util.Scanner;


public class Process {
	public int size;
	public int next;
	public int faultTimes;
	public int evictTimes;
	public int resTimes;
	
	public Process(int size, int process) {
		this.size = size;
		this.next = (111 * process) % size;
		this.faultTimes = 0;
		this.resTimes = 0;
		this.evictTimes = 0;
	}

	public void reference(double A, double B, double C, Scanner scan) {
		int rand = scan.nextInt();
		double quotient = rand / (Integer.MAX_VALUE + 1d);

		if (quotient < A) {
			next = (next + 1) % size;
		} else if (quotient < A + B) {
			next = (next - 5 + size) % size;
		} else if (quotient < A + B + C) {
			next = (next + 4) % size;
		} else {
			next = scan.nextInt() % size;
		}
	}

}
