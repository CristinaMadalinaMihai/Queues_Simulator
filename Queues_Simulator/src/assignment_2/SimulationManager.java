package assignment_2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class SimulationManager implements Runnable {
	
	// data read from In-Test.txt
	public int numberOfTasks;
	public int numberOfServers; // a.k.a. number of threads
	public int timeLimit; 
	public int minArrivalTime;
	public int maxArrivalTime;
	public int minProcessingTime;
	public int maxProcessingTime;
	
	private Scheduler scheduler; // entity responsible for queue management and client distribution
	private volatile List<Task> generatedTasks; // pool of tasks (client shopping in the store)
	
	public int minWaitingTimeForAllTasks = 0;
	
	private String txtIn;
	private String txtOut;
	
	public void readDataFromInputFile(String txtFileName) { 
		try {
			File inputFile = new File(txtFileName);
			Scanner scan = new Scanner(inputFile);
			
			this.numberOfTasks = scan.nextInt();
			this.numberOfServers = scan.nextInt();
			this.timeLimit = scan.nextInt();
			
			String arrivalTime = scan.next();
			String[] arrival = arrivalTime.split(",", 2);
			this.minArrivalTime = Integer.parseInt(arrival[0]); 
			this.maxArrivalTime = Integer.parseInt(arrival[1]);
			
			String processingTime = scan.next();
			String[] processing = processingTime.split(",", 2);
			this.minProcessingTime = Integer.parseInt(processing[0]);
			this.maxProcessingTime = Integer.parseInt(processing[1]);

		} catch (FileNotFoundException e) {
			System.out.println("\nopen file failed");
			e.printStackTrace();
		}
	}
	
	public SimulationManager(String txtIn, String txtOut) {
		this.txtIn = txtIn;
		this.txtOut = txtOut;
		readDataFromInputFile(txtIn); // read data and store it into the declared public variables
		
		this.scheduler = new Scheduler(numberOfServers, 1000); // initialize the Scheduler
		
		for (Server i : this.scheduler.getServers()) { // --> create and start numberOfServers threads
			i.startThread(); // --> start threads
			i.setServerStatus(false); // --> "closed"
		}
		
		generateNRandomTasks();	// generate numberOfTasks clients and store them to generatedTasks
	}
	
	private void generateNRandomTasks() {
		this.generatedTasks = new ArrayList<Task>();
		// minProcessingTime < processingTime < maxProcessingTime
		// minArrivalTime < arrivalTime < maxArrivalTime
		Random r = new Random();
		// r.nextInt((max - min) + 1) + min;
		
		for (int i = 0; i < numberOfTasks; i++) { // create new Task
			int arrivalTime = r.nextInt((maxArrivalTime - minArrivalTime) + 1) + minArrivalTime;
			int processingTime = r.nextInt( (maxProcessingTime - minProcessingTime) + 1 ) + minProcessingTime;
			generatedTasks.add(new Task(i + 1, arrivalTime, processingTime, true, processingTime));
		}
	}
	
	public int computeGeneratedTasksSize(List<Task> tasks) {
		int size = 0;
		for (Task i : tasks) {
			if (i.getExists() == true)
				++size;
		}
		return size;
	}

	@Override
	public void run() {
		int currentTime = 0;
		try {
			FileWriter outputF = new FileWriter("out-test-1.txt");
			PrintWriter outputFile = new PrintWriter(outputF);
		while (currentTime < timeLimit) {
			if (computeGeneratedTasksSize(generatedTasks) > 0) { // while there are still tasks to dispatch
				for (Task i : generatedTasks) { // iterate through generatedTasks left
					if (i.getArrivalTime() == currentTime && i.getExists() == true) { // pick tasks that have arrivalTime = currentTime
						i.setExists(false);// sent task to the queue with minWaitingPeriod
						minWaitingTimeForAllTasks += scheduler.dispatchTask(i); }}}
			outputFile.print("\n\nTime: " + String.valueOf(currentTime)); outputFile.print("\nWaiting clients: ");
			if (computeGeneratedTasksSize(generatedTasks) > 0) { // if tasks still waiting
				for (Task i : generatedTasks) {
					if (i.getExists() == true) {
						String s = "(" + String.valueOf(i.getId()) + "," + String.valueOf(i.getArrivalTime()) + "," + String.valueOf(i.getProcessingTime() + "); ");
					outputFile.print(s);}}
			} else {outputFile.print("-");}
			for (int i = 0; i < numberOfServers; i++) {
				int serverNumber = i + 1;
				String s = "\nQueue " + String.valueOf(serverNumber) + ": ";
				outputFile.print(s);
				if ( (scheduler.getServers().get(i).getServerStatus() == false) || (scheduler.getServers().size() == 0) ) { // "closed" queue
					outputFile.print("closed");
				} else { // if there are clients in queue
					BlockingQueue<Task> currentQueueTasks = scheduler.getServers().get(i).getTasks();
					for (Task j : currentQueueTasks) {
						String ss = "(" + String.valueOf(j.getId()) + "," + String.valueOf(j.getArrivalTime()) + "," + String.valueOf(j.getDecrementedProcessingTime()) + "); ";
						outputFile.print(ss);}}}
			currentTime++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("\nsleep simulator\n");
				e.printStackTrace();
			}				
		} // end while
		float avgTime = computeAverageTime(scheduler, currentTime); 
		System.out.println("\nAverage waiting time: " + String.valueOf(avgTime));
		outputFile.print("\nAverage waiting time: " + String.valueOf(avgTime));
		outputFile.close(); // CLOSE "out-test-1.txt"
		} catch (IOException e2) {
			System.out.println("\nsuccessfully wrote to the file");
			e2.printStackTrace();
		}
	}
	

	public float computeAverageTime(Scheduler scheduler, int currentTime) {
		float nominator = minWaitingTimeForAllTasks;
		float demonimator = 0;
		for (Server i : scheduler.getServers()) {
			nominator += i.getTotalProcessingTime();
			demonimator += i.getTotalTasks();
		}
		return nominator / demonimator;
	}
	
	public static void main(String[] args) {
		SimulationManager gen = new SimulationManager(args[0], args[1]);
		Thread simulationThread = new Thread(gen);
		simulationThread.start();
	}
	

	
}
