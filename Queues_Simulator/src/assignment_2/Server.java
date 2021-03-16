package assignment_2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
	
	private volatile BlockingQueue<Task> tasks;
	private AtomicInteger waitingPeriod; // time to finish serving current queue (SCHEDULER)
	private boolean flag; 
	private volatile Task takenTask; // variable to help dealing with current processing task
	
	// --> variable for computing avg time (SIMULATION)
	private float totalProcessingTime; 
	private int totalTasks;
	private boolean serverStatus; // --> open/close server (SIMULATION)
	
	/* Constructor */
	public Server() {
		// initialize Queue and waitingPeriod
		this.tasks = new LinkedBlockingDeque<Task>();
		this.waitingPeriod = new AtomicInteger(); 
		this.flag = false;
		this.totalProcessingTime = 0;
		this.totalTasks = 0;
		this.takenTask = new Task(0, 0, 0, false, 0);
	}

	/* Getters & Setters*/
	public boolean getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(boolean serverStatus) {
		this.serverStatus = serverStatus;
	}

	public float getTotalProcessingTime() { // --> for avg time (SIMULATION)
		return totalProcessingTime;
	}
	
	public float getTotalTasks() { // --> for avg time (SIMULATION)
		return totalTasks;
	}

	public AtomicInteger getWaitingPeriod() { // --> for adding next task (SCHEDULER)
		return waitingPeriod;
	}
	
	public BlockingQueue<Task> getTasks() { // --> for avg time (SIMULATION)
		return tasks;
	}

	public Task getTakenTask() {
		return takenTask;
	}

	public void setTakenTask(Task takenTask) {
		this.takenTask = takenTask;
	}
	
	public void addTask(Task newTask) { 
		// 1. add task to queue
		// 2. increment the total number of tasks
		// 3. increment the waitingPeriod
		this.tasks.add(newTask); 
		this.waitingPeriod.addAndGet(newTask.getProcessingTime());
		totalTasks++;
	}
	
	public void removeTask(int initialProcessingTime) {
		// 1. remove task from queue
		// 2. decrement the waitingPeriod
		// 3. increment the totalProcessingTime
		this.tasks.remove();
		this.waitingPeriod.addAndGet((-1) * initialProcessingTime);
		totalProcessingTime += initialProcessingTime; 
	}
	
	public void setFlag(boolean toRunOrNotToRun) {
		this.flag = toRunOrNotToRun;
	}
	
	public void startThread() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		while (flag == true ) {
			
			if (tasks.size() > 0) { // --> server has tasks
				setServerStatus(true); // --> open server
					
				this.takenTask = tasks.peek(); // 1. peek next task from queue
				int currentTaskProcessingTime = takenTask.getProcessingTime();
				while (currentTaskProcessingTime > 0) { // 2. stop the thread for one second and decrement processingTime of that Task
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						System.out.println("\nserver can't sleep 1 sec");
						e.printStackTrace();
					}
					--currentTaskProcessingTime;
					takenTask.setDecrementedProcessingTime(currentTaskProcessingTime);
				}

				removeTask(takenTask.getProcessingTime()); // 3. decrement the waitingPeriod + remove task from queue

			} else { // --> server has NO tasks
				setServerStatus(false); // --> closed server
			}
		}
	}

}
