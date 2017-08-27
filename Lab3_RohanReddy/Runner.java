import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Runner {

	public static void main(String[] args){

		File file = new File(args[0]);

		// I/O code. Fill the ArrayLists in each Task.
        try {
            Scanner scan = new Scanner(file);

            int T = scan.nextInt();
            int R = scan.nextInt();

            Task[] tasks = new Task[T];
            Resource[] resources = new Resource[R];

            for(int i = 0; i < R; i++){
            	resources[i] = new Resource(scan.nextInt());
            }


            while(scan.hasNext()){
            	String command = scan.next();
            	int index = scan.nextInt() - 1;
            	
            	if(tasks[index]==null)
            		tasks[index] = new Task();

            	tasks[index].commands.add(command);
            	tasks[index].resourceType.add(scan.nextInt());
            	tasks[index].numberReq.add(scan.nextInt());
            }

            //line(1);

            //System.out.println("Tasks: " + T);

            // for(int i = 0; i < R; i++){
            // 	System.out.println("Resource #: " + (i+1) + "\tCapacity: " + resources[i].capacity);
            // }

            // line(1);

            // for(int i = 0; i < T; i++){
            // 	for(int j = 0; j < tasks[i].commands.size(); j++){
	           //  	System.out.println("Command: " + tasks[i].commands.get(j) + "\tTask Number: " + (i+1) + 
	           //  		"\tRsrc Type: " + tasks[i].resourceType.get(j) + "\tNumber Req: " + tasks[i].numberReq.get(j));
	           //  }
            // }
	          


            scan.close();



            line(2);


            //Copy the Task and Resource list, since they will be changed from the original after the FIFO cycle.
            Task[] tasks2 = new Task[T];
            Resource[] resources2 = new Resource[R];

            for(int i = 0; i < T; i++)
            	tasks2[i] = new Task(tasks[i]);

            for(int i = 0; i < R; i++)
            	resources2[i] = new Resource(resources[i]);


            //Call the Cycle: banker = false for FIFO, banker = true for Banker.
            System.out.print("FIFO");
        	Cycle(T, R, tasks, resources, false);

        	System.out.print("Banker");
        	Cycle(T, R, tasks2, resources2, true);


        } catch (FileNotFoundException e) {
            System.out.println("Error: File does not exist");
            System.exit(0);
        }



	}

	//Cleanup for skipping lines. Takes less space. Most of these have been commented out. Comment in the println and line statements for 
	//more detailed output. 
	private static void line(int n){
		for(int i = 0; i < n; i++) System.out.println();
	}

	//Banker's algorithm.
	private static boolean isSafe(boolean banker, Task[] originalTasks, int taskIndex, Resource[] originalResources, int resIndex, int req){
		//Value of false means we're doing FIFO.
		if(!banker)
			return true;


		//Create a copy of tasks and resources to run through our "pretend" scenarios.
		Task[] tasks = new Task[originalTasks.length];
		Resource[] resources = new Resource[originalResources.length];

		for(int i = 0; i < tasks.length; i++)
			tasks[i] = new Task(originalTasks[i]);

		for(int i = 0; i < resources.length; i++)
			resources[i] = new Resource(originalResources[i]);


		//When this whole array is true, we know it is safe to grant the request.
		ArrayList<Boolean> willFinish = new ArrayList<Boolean>();


		//Tasks that have terminated shouldn't be tested here.
		for(int i = 0; i < tasks.length; i++)
			if(tasks[i].state == State.TERMINATED)
				willFinish.add(true);
			else
				willFinish.add(false);


		//Pretend to grant the request. Same code as in the request check of Cycle().
		int alreadyExists = tasks[taskIndex].inUse.indexOf(resIndex);

		if(alreadyExists == -1){
			tasks[taskIndex].inUse.add(resIndex);
			tasks[taskIndex].quantities.add(req);
			alreadyExists = tasks[taskIndex].inUse.indexOf(resIndex);
		}else{
			tasks[taskIndex].quantities.set(alreadyExists, tasks[taskIndex].quantities.get(alreadyExists) + req);
		}

		//Here's what's going on: we pretended to grant the request.
		//Looking at the quantity of the resource that the task now holds, does it exceed its initial claim? If so, abort.
		int amountHeld = tasks[taskIndex].quantities.get(alreadyExists); 
		int amountClaimed = tasks[taskIndex].initialClaims.get(tasks[taskIndex].initialResources.indexOf(resIndex));
		
		if(amountHeld > amountClaimed){
			originalTasks[taskIndex].state = State.ABORTED;
			System.out.println("\nThe banker aborted Task " + (taskIndex+1) + " for lying about its initial claim.");
			System.out.println("Claimed: " + amountClaimed + " of Resource " + (resIndex+1) + "\tHeld: " + amountHeld);
			return false;
		}


		//We passed the first check; let's update resources.
		resources[resIndex].available -= req;


		//So we are pretending that we granted the request. Now, we want to know if every task will be able to finish.
		while(willFinish.contains(false)){

			//This is an index for the next task that will be able to terminate. If it's still null after the for loop, nothing else 
			//will be able to terminate, and we're in an unsafe state.
			Integer t = null;

			//The loop ends early if we found a task that can terminate. 
			for(int i = 0; i < tasks.length && t == null; i++){

				//If the task has already finished in our scenario, skip it.
				if(willFinish.get(i) == true)
					continue;

				boolean stillSafe = true;

				//We're going through every claim this task has and seeing the worst case scenario: it asks to complete all of its claims at once.
				for(int j = 0; j < tasks[i].initialResources.size() && stillSafe; j++){

					int currentResource = tasks[i].initialResources.get(j);

					int amountInUse = tasks[i].inUse.indexOf(currentResource);


					if(amountInUse == -1)
						amountInUse = 0;
					else
						amountInUse = tasks[i].quantities.get(amountInUse);

					int maxPossibleRequest = tasks[i].initialClaims.get(currentResource) - amountInUse;

					//If the worst case scenario request can actually be made, then we're fine for this resource.
					//Otherwise, the task can't terminate, at least for now. So we're ending the loop early.
					if(maxPossibleRequest > resources[currentResource].available)
						stillSafe = false;

				}

				//If the loop didn't end early, it means we found a task that can terminate. 
				if(stillSafe)
					t = i;

			}

			//If we went through every task and none can terminate, the state is unsafe. The banker will decline the request.
			if (t == null){
				return false;
			}
			else{
				//If we're at this point, it means one of the tasks successfully terminated. Mark it accordingly and release its resources.
				willFinish.set(t, true);

				for(int i = 0; i < tasks[t].inUse.size(); i++){
					resources[tasks[t].inUse.get(i)].available += tasks[t].quantities.get(i);
				}
			}
			
		}

		//Finishing the while loop without returning false means that every task successfully terminated in the given scenario.
		//The banker will now grant the request.
		return true;
	}



	//The full algorithm that takes banker as a parameter, so it does both FIFO and Banker algorithms.
	private static void Cycle(int T, int R, Task[] tasks, Resource[] resources, boolean banker){

		//Variables to keep track of completion, time, and deadlock.
		int numberOfTasksTerminated = 0;
		int cycle = 0;
		int countdownToDeadlock = 0;

		//sorted: The list of tasks with the ones currently waiting/blocked in front. nextRound is filled during the cycle and will 
		//become sorted at the start of the following cycle.
		ArrayList<Integer> sorted = new ArrayList<Integer>();
		ArrayList<Integer> nextRound = new ArrayList<Integer>();

		//This is the first cycle so the order is kept as default.
		for(int i = 0; i < T; i++)
			sorted.add(i);

		//The loop ends when we finished all tasks.
		while(numberOfTasksTerminated < T){

			//Indices and quantities of resources to release at cycle end. 
			ArrayList<Integer> releaseQueue = new ArrayList<Integer>();
			ArrayList<Integer> releaseNums = new ArrayList<Integer>();

			//The number of tasks to terminate at cycle end.
			int terminateAtCycleEnd = 0;

			//The number of tasks currently unable to progress (only used during FIFO).
			countdownToDeadlock = 0;



			//System.out.println("\n\nCycle " + cycle + "-" + (cycle+1));


			//Add the unblocked processes at the end of the list.
			for(int i = 0; i < T; i++){ 
				if(!sorted.contains(i)){
					sorted.add(i);
				}
			}


			for(Integer i : sorted){

				if(tasks[i].state == State.TERMINATED || tasks[i].state == State.ABORTED){
					continue;
				} 

				//Store the critical data for this task in a more palatable form.
				int cmd = tasks[i].cmd;
				String command = tasks[i].commands.get(cmd).trim();
				int resIndex = tasks[i].resourceType.get(cmd) - 1;
				int req = tasks[i].numberReq.get(cmd);


				// System.out.println("\nTask " + (i+1) + " on cmd=" + cmd + ": " + command + " " + req + " of Resource #" + resIndex 
				// 	+ ", which currently has " + resources[resIndex].available + "/" + resources[resIndex].capacity + " available");


			
				if(command.equals("compute")){
					if(tasks[i].state != State.COMPUTING){
						tasks[i].cyclesLeft = resIndex+1;
						tasks[i].state = State.COMPUTING;
						//System.out.println("\nTask " + (i+1) + " will now compute for " + tasks[i].cyclesLeft + " cycles.");
					}else{
						//System.out.println("\nTask " + (i+1) + " is computing this cycle. It has " + tasks[i].cyclesLeft+ " cycles left to compute.");
					}

					tasks[i].cyclesLeft--;

					//Clean up if we're done computing.
					if(tasks[i].cyclesLeft == 0){
						tasks[i].state = State.RUNNING;
					}
							
				}
				else if(command.equals("initiate")){

					//If we're in banker mode and the task is claiming more than the resource actually has, abort it.
					if(banker && req > resources[resIndex].capacity){
						tasks[i].state = State.ABORTED;
						System.out.println("\nThe banker aborted Task " + (i+1) + " because it claimed more than the resource can hold.");
						System.out.println("Claimed: " + req + " of Resource " + (resIndex+1) +  "\tCapacity: " + resources[resIndex].capacity);
						numberOfTasksTerminated++;

						// for(int j = 0; j < tasks[toBeAborted].inUse.size(); j++){
						// 	resIndex = tasks[toBeAborted].inUse.get(j);
						// 	resources[resIndex].available += tasks[toBeAborted].quantities.get(j);
						// 	System.out.println("Freed " + tasks[toBeAborted].quantities.get(j) + " up for Resource #" + resIndex);
						// }

						continue;
					}

					tasks[i].state = State.RUNNING; 
					tasks[i].initialResources.add(resIndex);
					tasks[i].initialClaims.add(req);
				
				}
				else if(command.equals("request")){

					//Conduct the banker check. It will return true regardless if we're not in banker mode.
					boolean safe = isSafe(banker, tasks, i, resources, resIndex, req);

					//Only structured this way so that you can print the optional line below. Otherwise, I'd have combined the conditions.
					if(banker){
						//System.out.println("Banker says: Task " + (i+1) + " request is " + (safe? "safe" : "unsafe"));

						//If the task's state is now ABORTED, that means the task lied about its claim. 
						//The banker took care of aborting it, now we just have to clean up after it and free up its resources.
						if(tasks[i].state == State.ABORTED){
							terminateAtCycleEnd++;

							for(int j = 0; j < tasks[i].inUse.size(); j++){
								releaseQueue.add(tasks[i].inUse.get(j));
								releaseNums.add(tasks[i].quantities.get(j));

								tasks[i].quantities.remove(j);
								tasks[i].inUse.remove(j);
							}

							continue;
						}
					}


					//Safe: The check for the banker. The second part is the check for FIFO. 
					if(safe && req <= resources[resIndex].available){

						//Here, we are granting the request.
						//Make sure not to duplicate the resource index in the list inUse. 
						int alreadyExists = tasks[i].inUse.indexOf(resIndex);
						if(alreadyExists == -1){
							tasks[i].inUse.add(resIndex);
							tasks[i].quantities.add(req);
						}else{
							tasks[i].quantities.set(alreadyExists, tasks[i].quantities.get(alreadyExists) + req);
						}
						

						resources[resIndex].available -= req;

						tasks[i].state = State.RUNNING;

						//System.out.println("Task " + (i+1) + " request granted");
					}
					else{

						//If we got to this point, one of two things happened.
						// 1) We're in banker mode and we failed the safety check. In which case, we can print the optional message below.
						// 2) We're in FIFO mode and there aren't enough units to complete the request. In which case, add to the deadlock counter.

						tasks[i].state = State.WAITING;
						tasks[i].timeWaiting++;
						nextRound.add(i);

						if(banker){
							//System.out.println("The banker has blocked this request because it is not safe. Moved to WAITING.");
						}else{
							countdownToDeadlock++;
							// System.out.println("Task " + (i+1) + " request cannot be completed, moved to WAITING.\nThe deadlock counter is "
							// 	+ countdownToDeadlock + "/" + (T - numberOfTasksTerminated));
						}
					}
				}
				else if(command.equals("release")){

					//Queue up the resources that need to be released at the end of the cycle.
					int releaseIndex = tasks[i].inUse.indexOf(resIndex);

					releaseQueue.add(resIndex);
					releaseNums.add(tasks[i].quantities.get(releaseIndex));

					tasks[i].quantities.remove(releaseIndex);
					tasks[i].inUse.remove(releaseIndex);

				}

				tasks[i].timeTaken++;

				//If we're still running, move to the next command.
				if(tasks[i].state == State.RUNNING)
					tasks[i].cmd++;				

				//If the next command is terminate, we can actually go ahead and take care of that right now.
				if(tasks[i].commands.get(tasks[i].cmd).trim().equals("terminate")){
					tasks[i].state = State.TERMINATED;
					terminateAtCycleEnd++;

					//System.out.println("Task " + (i+1) + " has terminated");
					continue;
				}


				//System.out.println("Task " + (i+1) + " is " + tasks[i].state + " at end of Cycle " + cycle + "-" + (cycle+1));


				//If we're in FIFO, this will take care of a deadlock.
				if(countdownToDeadlock == T - numberOfTasksTerminated){

					//This stays true as long as we didn't free up enough resources to complete the task.
					boolean stillDeadlocked = true;

					//Keep aborting tasks and cleaning up until we can run the next step of the current task.
					while(stillDeadlocked){
						int toBeAborted;

						for(toBeAborted = 0; tasks[toBeAborted].state != State.WAITING; toBeAborted++);

						//System.out.println("\nDEADLOCKED! Task " + (toBeAborted+1) + " aborted");
						tasks[toBeAborted].state = State.ABORTED;

						for(int j = 0; j < tasks[toBeAborted].inUse.size(); j++){
							resIndex = tasks[toBeAborted].inUse.get(j);
							resources[resIndex].available += tasks[toBeAborted].quantities.get(j);
			
							//System.out.println("Freed " + tasks[toBeAborted].quantities.get(j) + " up for Resource #" + resIndex);
						}

						numberOfTasksTerminated++;

						if(req <= resources[resIndex].available){
							stillDeadlocked = false;
						}

					}

					//Since we deadlocked, freed resources are only available next cycle. So we break out of the loop.
					break;

				}

			} 


			//Release the queued quantities at cycle end
			for(int j = 0; j < releaseQueue.size(); j++){
				resources[releaseQueue.get(j)].available += releaseNums.get(j);
			}

			//Count the tasks that we terminated this cycle.
			numberOfTasksTerminated += terminateAtCycleEnd;

			//Below, we have an optional End of Cycle Recap.

			// System.out.println("\nEnd of Cycle RECAP.");
			// for(int j = 0; j < resources.length; j++){
			// 	System.out.println("Resource #" + j + ": " + resources[j].available + "/" + resources[j].capacity);
			// }

			// line(1);
			// for(int j = 0; j < tasks.length; j++){
			// 	System.out.print("Task #" + (j+1) + ": " + tasks[j].state);

			// 	for(int k = 0; k < tasks[j].inUse.size(); k++){
			// 		System.out.print("\tHas " + tasks[j].quantities.get(k) + " of Resource #" + tasks[j].inUse.get(k));
			// 	}
			// 	line(1);
			// }

			//Queue up the tasks that were blocked this cycle into the new sorted list. Clear the list nextRound.
			sorted = nextRound;
			nextRound = new ArrayList<Integer>();


			cycle++;

		}

		double totalTime = 0, totalWaiting = 0;

		line(2);

		//Print the end of program metrics.
		for(int i = 0; i < T; i++){

			if(tasks[i].state == State.ABORTED){
				System.out.println("Task " + (i+1) + "\taborted");
			}
			else{
				System.out.println("Task " + (i+1) + "\t" + tasks[i].timeTaken + "\t" + tasks[i].timeWaiting
					+ "\t" + Math.round(((double)tasks[i].timeWaiting / (double)tasks[i].timeTaken) * 100) + "%");
				totalTime += tasks[i].timeTaken;
				totalWaiting += tasks[i].timeWaiting;
			}
		}

		System.out.println("Total\t" + (int)totalTime + "\t" + (int)totalWaiting + "\t"
			+ Math.round((totalWaiting/totalTime) * 100) + "%\n\n");

	}






}