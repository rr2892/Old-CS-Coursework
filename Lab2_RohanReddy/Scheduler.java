/**
 *  Rohan Reddy
 *  Lab 2
 *  Scheduler.java
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;


public class Scheduler {

    
    public static String RAND_INT_FILE = "random-numbers.txt";
    
    public static String inputFile = "";

    public static int currentLineInRand = 0;


    //Main method for whole program. Prints out results for all 4 methods for easier testing.
    public static void main(String[] args) {
        boolean verbose = false;
        
        if (args.length == 1) {
            inputFile = args[0];
        }
        
        
        else if (args.length == 2) {
            inputFile = args[1];
            if (args[0].contains("--verbose")) {
                verbose = true;
            } else {
                System.out.println("Invalid command line parameter");
                System.exit(1);
            }
        } else {
            System.out.println("Invalid number of command line parameters");
            System.out.println(args.length);
            System.exit(1);
        }
        

        //Print the results for each method
        
        Process[] procs = fileToProcesses(inputFile);
        Arrays.sort(procs);
        currentLineInRand = 0;
        FCFS fc = new FCFS(procs, "");
        fc.run(verbose);
        fc.viewResults();
        
        
        procs = fileToProcesses(inputFile);
        Arrays.sort(procs);
        currentLineInRand = 0;
        RR rr = new RR(procs);
        rr.run(verbose);
        rr.viewResults();
        
        procs = fileToProcesses(inputFile);
        Arrays.sort(procs);
        currentLineInRand = 0;
        UNI uni = new UNI(procs);
        uni.run(verbose);
        uni.viewResults();
        
        
        procs = fileToProcesses(inputFile);
        Arrays.sort(procs);
        currentLineInRand = 0;
        SJF sjf = new SJF(procs);
        sjf.run(verbose);
        sjf.viewResults();
        
    }

    
    //Get the input in a usable form
    public static Process[] fileToProcesses(String filename) {
        File file = new File(filename);
        Process[] processes = new Process[0];
        try {
            Scanner scan = new Scanner(file);
            int numModules = scan.nextInt();
            processes = new Process[numModules];
            for (int i = 0; i < numModules; i++) {
                
                scan.useDelimiter("\\D+");
                int A = scan.nextInt();
                int B = scan.nextInt();
                int C = scan.nextInt();
                int M = scan.nextInt();
                Process p = new Process(A, B, C, M);
                processes[i] = p;
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File does not exist");
            System.exit(2);
        }
        return processes;
    }

    
    
    
    //Get a random number from the text file
    public static int randomOS(int U) {
        File file = new File(RAND_INT_FILE);
        int n = 0;
        
        int line = currentLineInRand++;
        try {
            Scanner scan = new Scanner(file);
            
            while (line > 0) {
                scan.nextLine();
                line--;
            }
            n = scan.nextInt();
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File does not exist");
            System.exit(2);
        }
        return 1 + (n % U);
    }
}