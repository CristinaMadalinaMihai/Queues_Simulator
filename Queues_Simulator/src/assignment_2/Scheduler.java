package assignment_2;

import java.util.ArrayList;
import java.util.List;
 
public class Scheduler {
	
	private List<Server> servers;
	private int maxNoServers; // --> from file input (SIMULATION)
	private int maxTasksPerServer; // --> as large as possible... (SILUMATION)
	
	/* Constructor */
	public Scheduler (int maxNoServers, int maxTasksPerServer) {
		this.maxNoServers = maxNoServers;
		this.maxTasksPerServer = maxTasksPerServer;
		this.servers = new ArrayList<Server>();
		
		for (int i = 0; i < maxNoServers; i++) { // for maxNoServers
			this.servers.add(i, new Server()); // --> create server object
			this.servers.get(i).setFlag(true); // --> prepare threads			
		}
	}
	 
	public int dispatchTask(Task task) {
		// time-based strategy
		return addTask(this.servers, task);
	}
	
	public int addTask(List<Server> servers, Task task) {
		
		int minWaitingTime = servers.get(0).getWaitingPeriod().get(); // waitingPeriod of the first Server
		for (Server i : servers) { // iterate through the list of Servers
			if (i.getWaitingPeriod().get() <= minWaitingTime) { // find minimum waitingPeriod
				minWaitingTime = i.getWaitingPeriod().get();
			} 
		}
		for (Server i : servers) { // iterate again
			if (i.getWaitingPeriod().get() == minWaitingTime) { // when Server with minWaitingPeriod found
				i.addTask(task); // add task to that particular Server
				i.setServerStatus(false);
				break;
			}
		}
		return minWaitingTime;
	}
 
	public List<Server> getServers(){
		return servers;
	}
	
}
