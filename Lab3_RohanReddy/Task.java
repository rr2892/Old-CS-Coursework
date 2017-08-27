import java.util.ArrayList;

public class Task{


	//Keeps track of the current command for the task.
	public int cmd;

	//The current state.
	public State state;


	//Metrics for display at the end of the program.
	public int timeTaken, timeWaiting;
	
	//For computing. Keeps track of remaining cycles.
	public int cyclesLeft;

	//initialResources: Indices of the resources initially claimed. initialClaims: Quantities claimed.
	public ArrayList<Integer> initialResources;
	public ArrayList<Integer> initialClaims;

	//commands: List of commands, ordered. resourceType: Indices of resources requested. numberReq: Quantities requested.
	public ArrayList<String> commands;
    public ArrayList<Integer> resourceType;
    public ArrayList<Integer> numberReq;

    //inUse: Resources currently held by the task. quantities: Quantities held.
    public ArrayList<Integer> inUse;
    public ArrayList<Integer> quantities;

    //Default constructor.
	public Task(){
		timeTaken = 0;
		timeWaiting = 0;
		state = State.UNSTARTED;
		cmd = 0;
		cyclesLeft = 0;

		initialResources = new ArrayList<Integer>();
		initialClaims = new ArrayList<Integer>();

		commands = new ArrayList<String>();
		resourceType = new ArrayList<Integer>();
		numberReq = new ArrayList<Integer>();

		inUse = new ArrayList<Integer>();
		quantities = new ArrayList<Integer>();
	}

	//Copy constructor.
	public Task(Task t){
		timeTaken = t.timeTaken;
		timeWaiting = t.timeWaiting;
		state = t.state;
		cmd = t.cmd;
		cyclesLeft = t.cyclesLeft;

		initialResources = new ArrayList<Integer>();
		initialClaims = new ArrayList<Integer>();

		commands = new ArrayList<String>();
		resourceType = new ArrayList<Integer>();
		numberReq = new ArrayList<Integer>();

		inUse = new ArrayList<Integer>();
		quantities = new ArrayList<Integer>();

		for(int i = 0; i < t.initialResources.size(); i++){
			initialResources.add(t.initialResources.get(i));
			initialClaims.add(t.initialClaims.get(i));
		}
		

		for(int i = 0; i < t.commands.size(); i++){
			commands.add(t.commands.get(i));
			resourceType.add(t.resourceType.get(i));
			numberReq.add(t.numberReq.get(i));
		}


		for(int i = 0; i < t.inUse.size(); i++){
			inUse.add(t.inUse.get(i));
			quantities.add(t.quantities.get(i));
		}

	}
}