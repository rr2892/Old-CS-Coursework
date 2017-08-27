import java.util.ArrayList;

public class Module {
	
	public int length, address;
	public ArrayList<String> definitionList, useList, programText;
	public ArrayList<Boolean> useWasUsed;
	
	public Module(){
		definitionList = new ArrayList<String>();
		useList = new ArrayList<String>();
		useWasUsed = new ArrayList<Boolean>();
		programText = new ArrayList<String>();
	}
}
