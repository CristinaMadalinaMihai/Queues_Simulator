package assignment_2;

public class Task implements Comparable<Task>{
	
	private int id;
	private int arrivalTime; /* t arrival */
	private int processingTime; /* t service */
	private boolean exists; /* auxiliary variable to help me not remove the task & still access its processing time to help me compute the avg time */
	private int decrementedProcessingTime;
	
	/* Constructor: Task is characterized by: t arrival & t service */
	public Task(int id, int arrivalTime, int processingTime, boolean exists, int decrementedProcessingTime) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.processingTime = processingTime;
		this.exists = true;
		this.decrementedProcessingTime = decrementedProcessingTime;
	}

	/* Getters & Setters */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
	}

	public boolean getExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public int getDecrementedProcessingTime() {
		return decrementedProcessingTime;
	}

	public void setDecrementedProcessingTime(int decrementedProcessingTime) {
		this.decrementedProcessingTime = decrementedProcessingTime;
	}

	@Override
	public int compareTo(Task o) { // sort list with respect to arrivalTime
		// TODO Auto-generated method stub
		return o.getArrivalTime() - this.getArrivalTime();
	}


}
