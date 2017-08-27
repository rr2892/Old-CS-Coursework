import java.io.*;
import java.util.ArrayList;

public class Linker {
	public static void main(String[] args){

		String inp = "";
		String next = null;
		String file = args[0];
		
		System.out.println(file);

		//Getting input
		
		try{
			
			BufferedReader reader = new BufferedReader(new FileReader(file));

	        while((next = reader.readLine()) != null) {
	            if(!next.equals("")){
	            	inp += next + "\n";
	            }
	        }
	        reader.close();
		}
	    catch(FileNotFoundException ex) {
	            System.out.println("Couldn't open file '" + file + "'");
        }
        catch(IOException ex) {
            System.out.println( "Error reading file '" + next + "'");
        }
	
		inp = inp.trim();
		
	    String[] input = inp.split("\\s+");
	    
	    System.out.println();
	    
	    
	    //First Pass Setup
	    System.out.println(input[0]);
	    int moduleCount = Integer.parseInt(input[0]);
	    
	    Module[] moduleList = new Module[moduleCount];
	    
	    int j;
	    
	    int instructionCount = 0, baseAddress = 0;
	    ArrayList<String> symbolTable = new ArrayList<String>();
	    ArrayList<Integer> symbolValues = new ArrayList<Integer>();
	    ArrayList<Boolean> wasUsed = new ArrayList<Boolean>();
	    ArrayList<Integer> whereDefined = new ArrayList<Integer>();
	    
	    
	    //First Pass Loop
	    for(int i = 0; i < moduleList.length; i++)
	    {
	    	moduleList[i] = new Module();
	    	
	    	j = 1;
    		for(int m = 0; m < i; m++){
    			j+=moduleList[m].length;
    		}
    			

	    	int k = j, l = 0;
	    	
	    	int defLen= 2 * Integer.parseInt(input[k]) + 1;
	    	
	    	for( ; k < defLen + j; k++){
	    		moduleList[i].definitionList.add(input[k]);
	    		System.out.print(moduleList[i].definitionList.get(l++) + " ");
	    	}
	    	
	    	System.out.println();
	    	int useLen = Integer.parseInt(input[j + defLen]) + 1;
	    	l = 0;
	    	
	    	for(k = j + defLen; k < useLen + defLen + j; k++)
	    	{
	    		moduleList[i].useList.add(input[k]);
	    		System.out.print(moduleList[i].useList.get(l++) + " ");
	    		
	    		moduleList[i].useWasUsed.add(false);
	    	}
	    	
	    	moduleList[i].useWasUsed.set(0, true);
	    	
	    	System.out.println();
	    	int pLen = Integer.parseInt(input[j + defLen + useLen]) + 1;
	    	l = 0;
	    	
	    	for(k = j + defLen + useLen; k < pLen + useLen + defLen + j; k++)
	    	{
	    		moduleList[i].programText.add(input[k]);
	    		System.out.print(moduleList[i].programText.get(l++) + " ");
	    	}
	    	
	    	System.out.println();
	    	
	    	
	    	moduleList[i].length = moduleList[i].definitionList.size() + moduleList[i].useList.size() + moduleList[i].programText.size();
	    	
	    	moduleList[i].address = baseAddress;
	    	baseAddress += moduleList[i].programText.size()-1;
	    	
	    	int numDefs = Integer.parseInt(moduleList[i].definitionList.get(0));
	    	
	    	if(numDefs != 0){
	    		for(int a = 0; a < numDefs; a++)
	    		{
	    			if(!symbolTable.contains(moduleList[i].definitionList.get(2*a+1)))
	    			{
	    				symbolTable.add(moduleList[i].definitionList.get(2*a + 1));
		    			symbolValues.add(Integer.parseInt(moduleList[i].definitionList.get(2*a + 2)) + instructionCount);
		    			wasUsed.add(false);
		    			whereDefined.add(i);
	    			}
	    			else{
	    				System.out.println("Error: Symbol " + moduleList[i].definitionList.get(2*a+1) + " has already been defined. Prior definition will be used.");
	    			}
	    			
	    			
	    		}
	    	}
	    	
	    	instructionCount += Integer.parseInt(moduleList[i].programText.get(0));
	    }
	    
	    

	    

	    //Second Pass Setup
	    System.out.println("\n\nSymbol Table");
	    
	    
	    for(int i = 0; i < symbolTable.size(); i++){
	    	System.out.println(symbolTable.get(i) + " = " + symbolValues.get(i));
	    }
	    
	    System.out.println("\nMemory Map");
	    
	    int relative;
	    
	    
	    
	    //Second Pass Loop
	    for(int i = 0; i < moduleList.length; i++){
	    	
	    	Module m = moduleList[i];
	    	relative = 0;
	    	
	    	System.out.println("+" + m.address);
	    	
	    	
	    	for(j = 1; j < m.programText.size(); j++)
	    	{
	    		int instruct = Integer.parseInt(m.programText.get(j));
	    		
	    		String line = relative + ":\t\t" + instruct + "\t -->\t";
	    		String error = "";
	    		
	    		int resolution = 0;
	    		
	    		if(instruct%10 == 1){
	    			resolution = instruct/10;
	    		}else if(instruct%10 == 2){
	    			if((instruct/10)%1000 > 600){
	    				error = "Error: Absolute address exceeds machine size.";
	    				resolution = (instruct/10000) * 1000;
	    			}else{
	    				resolution = instruct/10;
	    			}
	    		}else if(instruct%10 == 3){
	    			resolution = instruct/10 + m.address;
	    			
	    			if(resolution%1000 > m.address + m.length){
	    				error = "Error: Relative address exceeds module size.";
	    				resolution = (instruct/10000) * 1000;
	    			}
	    		}else if(instruct%10 == 4){
	    			
	    			int symbolIndex = (instruct/10)%10;
	    			
	    			if(symbolIndex >= m.useList.size()){
	    				error = "External address too large to reference symbol. Treated as immediate.";
	    				resolution = instruct/10;
	    			}
	    			else{
		    			String symbol = m.useList.get(symbolIndex+1);
		    			resolution = instruct/10;
		    			resolution -= resolution%10;
		    			
		    			if(symbolTable.contains(symbol))
		    			{	
			    			int k = 0;
			    			
			    			while(!symbolTable.get(k).equals(symbol)){
			    				k++;
			    			}
			    			
			    			resolution += symbolValues.get(k);
			    			
			    			wasUsed.set(k, true);
		    			}
		    			else
		    			{
		    				error = "Error: Symbol " + symbol + " is not defined. Using value 0.";
		    			}
		    			
		    			m.useWasUsed.set(symbolIndex+1, true);
	    			}
	    		}
	    		
	    		line += resolution + "\t" + error;
	    		
	    		System.out.println(line);
	    		
	    		relative++;
	    	}	
	    }
	    
	    
	    
	    for(int i = 0; i < wasUsed.size(); i++){
	    	if(wasUsed.get(i) == false)
	    		System.out.println("Warning: Symbol " + symbolTable.get(i) + " was defined in module " + whereDefined.get(i) + " but never used.");
	    }
	    
	    for(int i = 0; i < moduleList.length; i++){
	    	Module m = moduleList[i];
	    	for(j = 0; j < m.useWasUsed.size(); j++){
	    		if(m.useWasUsed.get(j) == false){
	    			System.out.println("Warning: Symbol " + m.useList.get(j) + " was on the use list in module " + i + " but wasn't used.");
	    		}
	    	}
	    }
	    
	      
	}
}
