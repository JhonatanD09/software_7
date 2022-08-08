package models;

import exceptions.RepeatedNameException;

import java.util.ArrayList;
import java.util.Collections;


public class Manager {

	private Queue<MyProcess> queue;
	private ArrayList<MyProcess> processes;
	private ArrayList<Partition> partitions;
	private ArrayList<Partition> terminatedPartitions;
	private ArrayList<MyProcess> processesTerminated;
	private ArrayList<Partition> allPartitions;
	private ArrayList<ReportCompact> reportCompacts;
	private int size;
	private int count;

	public Manager() {
		this.queue = new Queue<>();
		this.processes = new ArrayList<>();
		this.partitions = new ArrayList<>();
		this.terminatedPartitions = new ArrayList<>();
		this.processesTerminated = new ArrayList<>();
		this.allPartitions = new ArrayList<>();
		this.reportCompacts = new ArrayList<>();
		this.size = 50;
		this.count = 1;
	}
	
	//-----------------------------CRUD-----------------------------------
	
	public boolean add(MyProcess myProcess) {
		if (searchRepeat(myProcess.getName())) {
			processes.add(myProcess);
			return true;
		}else {
			return false;
		}
	}
	
	public boolean edit(String name,String newName, long size, long time, boolean isLocked ) {
		boolean isEdit = false;
		if (searchProcess(name)!= null) {
			MyProcess temp = searchProcess(name);
			if(searchRepeat(name)) {
				edit(newName, size, time, isLocked, temp);
				isEdit = true;
			}
		}
		return isEdit;
	}

	private void edit(String newName, long size, long time, boolean isLocked, MyProcess temp) {
		temp.setName(newName);
		temp.setSize(size);
		temp.setTime(time);
		temp.setLocked(isLocked);
	}
	
	public boolean delete(String nameProcess) {
		if (searchProcess(nameProcess)!= null) {
			processes.remove(searchProcess(nameProcess));
			return true;
		}else {
			return false;
		}
	}
	
	private boolean searchRepeat(String name) {
		for (MyProcess myProcess : processes) {
			if (myProcess.getName().equalsIgnoreCase(name)) {
				return false;
			}
		}
		return true;
	}
	
	public MyProcess searchProcess(String  nameProcess) {
		for (MyProcess myProcess : processes) {
			if (myProcess.getName().equalsIgnoreCase(nameProcess)) {
				return myProcess;
			}
		}
		return null;
	}
	
	//-----------------------------END CRUD-----------------------------------
	
	
	//-----------------------------LOGIC---------------------------------------
	
	public void initSimulation() {
		fillQueue();
		int index = 0;
		while (!queue.isEmpty()|| isNotTerminated()) {
			if (index >= partitions.size()) {
				index = 0;
			}
			Partition actualPartition = partitions.get(index);
			if(!actualPartition.isFinish()) {
				actualPartition.timeDiscount();
			}else {
				if(actualPartition.getName()!=null) {		
					if(nodeProcess(actualPartition.getName())!=null) {						
						queue.delete(nodeProcess(actualPartition.getName()));
					}
				}
				nextProccess();	
				if(validateCompactation()) {
					compactation(actualPartition);
				}
			}
			index++;
		}
		long size =  partitions.get(0).getSize()+ partitions.get(1).getSize();
		allPartitions.add(new Partition(size, 0,"PAR"+count,"Final"));
		reportCompacts.add(new ReportCompact(partitions.get(0).getName(), partitions.get(1).getName(), "PAR"+count, size));
	}
	
	private boolean isNotTerminated() {
		for (Partition partition : partitions) {
			if (partition.getTime()>0) {
				return true;
			}
		}
		return false;
	}
	
	private void nextProccess() {
		Node<MyProcess> node = queue.peek();
		while (node != null) {
			int index = valideJoinProcess(node);
			if (index > -1) {
				joinProcess(node, index);
				node = null;
			} else {
				node = node.getNext();
			}
		}
	}

	private void joinProcess(Node<MyProcess> node, int i) {
		Partition partitionN = new Partition( node.getData().getSize(),node.getData().getTime(),"PAR" + count,node.getData().getName() );
		long size = partitions.get(i).getSize() - node.getData().getSize();
		partitions.get(i).setName("PAR" + count);
		partitions.get(i).setSize(node.getData().getSize());
		partitions.get(i).setTime(node.getData().getTime());
		partitions.get(i).setMyProcess(node.getData());
		partitions.get(i).setFinish(false);
		terminatedPartitions
				.add(new Partition(partitions.get(i).getSize(), node.getData().getTime(),partitions.get(i).getName()));
		processesTerminated.add(new MyProcess(node.getData().getName(), node.getData().getTime(),
				node.getData().getSize(), node.getData().isLocked()));
		allPartitions.add(partitionN);
		count++;
		if (size > 0) {
			Partition partitionNL = new Partition(size, 0,"PAR" + count,"Libre");
			allPartitions.add(partitionNL);
			partitions.add(i + 1, partitionNL);
			count++;
		}
		queue.delete(node);
	}

	private int valideJoinProcess(Node<MyProcess> proccess) {
		for (int i = 0; i < partitions.size(); i++) {
			if (partitions.get(i).getSize() >= proccess.getData().getSize() && partitions.get(i).isFinish()) {
				return i;
			}
		}
		return -1;
	}
	
	private void compactation(Partition actualPartition) {
		compactationProcess(actualPartition);
		while (validateCompactation()) {
			ArrayList<Partition> partitions = toCompactation();
			compactationProcess(partitions.get(0));
		}
	}

	private void compactationProcess(Partition actualPartition) {
		ArrayList<Partition> partitions = toCompactation();
		if (!partitions.get(0).getName().equals(actualPartition.getName())) {
			compacte(partitions.get(0), actualPartition);
		}else {
			compacte(partitions.get(1), actualPartition);
		}
	}

	
	private void compacte(Partition partition, Partition actualPartition) {
		long size = partition.getSize()+actualPartition.getSize();
		long time = partition.getTime()+actualPartition.getTime();
		Partition newPartition = new Partition(size, time, "PAR"+count);
		reportCompacts.add(new ReportCompact(actualPartition.getName(), partition.getName(), newPartition.getName(), size));
		deleteMyProcessToQueue(partition);
		deleteMyProcessToQueue(actualPartition);
		partitions.remove(partition);
		partitions.remove(actualPartition);
		partitions.add(newPartition);
		allPartitions.add(new Partition(newPartition.getSize(),newPartition.getTime(),newPartition.getName(),"Compactacion"));
		count++;
	}

	private void deleteMyProcessToQueue(Partition partition) {
		if (partition.getMyProcess()!=null) {
			if(nodeProcess(partition.getMyProcess().getName())!=null) {				
				queue.delete(nodeProcess(partition.getMyProcess().getName()));
			}
		}
	}

	private ArrayList<Partition> toCompactation(){
		ArrayList<Partition> partitions = new ArrayList<>();
		for (Partition partition : this.partitions) {
			if(partition.isFinish()) {
				partitions.add(partition);
			}
		}
		return partitions;
	}
	
	private boolean validateCompactation() {
		int count = 0;
		for (Partition partition : partitions) {
			if (partition.isFinish()) {
				count++;
			}
		}
		return count > 1;
	}
	
	
	private void fillQueue() {
		int size = 0;
		for (MyProcess myProcess : processes) {
			size +=  myProcess.getSize();
			if (size <= this.size) {	
				newPartition(myProcess, partitions);
				newPartition(myProcess,terminatedPartitions);
				allPartitions.add(new Partition(myProcess.getSize(),myProcess.getTime(), "PAR"+count,myProcess.getName()));
				newProcess(myProcess,processesTerminated);
				count++;
			}else if(myProcess.getSize()<this.size){				
				queue.push(myProcess);
			}else {
				System.out.println("Proceso muy grande");
			}
		}
		Collections.sort(terminatedPartitions);
		Collections.sort(processesTerminated);
	}
	
	private void newPartition(MyProcess myProcess, ArrayList<Partition> partitions) {
		partitions.add(new Partition(myProcess.getSize(), myProcess.getTime(), "PAR"+count, myProcess));
	}
	
	private void newProcess(MyProcess process, ArrayList<MyProcess> processes) {
		processes.add(new MyProcess(process.getName(), process.getTime(), process.getSize(), process.isLocked()));
	}
	
	private Node<MyProcess> nodeProcess(String name){
		Node<MyProcess> tempNode = queue.getFirstNode();
		while(tempNode != null) {
			if(tempNode.getData().getName().equalsIgnoreCase(name)) {
				return tempNode;
			}else {
				tempNode = tempNode.getNext();
			}
		}
		return null;
	}

	public void verifyProcessName(String processName) throws RepeatedNameException {
		for (MyProcess process : processes) {
			if (processName.equals(process.getName())) {
				throw new RepeatedNameException(processName);
			}
		}
	}
	//------------------------------END LOGIC----------------------------------
	
	//-----------------------------GETTERS AND SETERS-------------------------

	/**
	 * 
	 * @return Lista de procesos
	 */
	public ArrayList<MyProcess> getProcesses() {
		return processes;
	}
	
	/**
	 * 
	 * @return terminacion de los procesos
	 */
	public ArrayList<MyProcess> getProcessesTerminated() {
		return processesTerminated;
	}
	
/**
 * 
 * @return Terminacion de las particiones
 */
	public ArrayList<Partition> getTerminatedPartitions() {
		return terminatedPartitions;
	}
	
	/**
	 * 
	 * @return Quien paso por
	 */
	public ArrayList<Partition> getAllPartitions() {
		return allPartitions;
	}
	
	/**
	 * 
	 * @return Ahora es una clase reporte xd
	 * 
	 */
	public ArrayList<ReportCompact> getReportCompacts() {
		return reportCompacts;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	//-------------------------------TEST SHOW------------------------------------------
	
	public void show() {
		for (ReportCompact rc : reportCompacts) {
			System.out.println(rc.getPartition1()+" se une con "+rc.getPartition2()+" y forman "+rc.getNewPartition()+ " con un tama de " + rc.getSize());
		}
	}

	public static Object[][] processProcessTermiedInfo(ArrayList<MyProcess> termined) {
		Object[][] processInfo = new Object[termined.size()][2];
		for (int i = 0; i < termined.size(); i++) {
			processInfo[i][0] = termined.get(i).getName();
			processInfo[i][1] = termined.get(i).getTime();
		}
		return processInfo;
	}

	public static Object[][] processInitialPartitionsInfo(ArrayList<Partition> initialPartitions) {
		Object[][] partitionsInfo = new Object[initialPartitions.size()][2];
		for (int i = 0; i < initialPartitions.size(); i++) {
			partitionsInfo[i][0] = initialPartitions.get(i).getName();
			partitionsInfo[i][1] = initialPartitions.get(i).getSize();
		}
		return partitionsInfo;
	}

	public static Object[][] allPartitions(ArrayList<Partition> initialPartitions) {
		Object[][] partitionsInfo = new Object[initialPartitions.size()][3];
		for (int i = 0; i < initialPartitions.size(); i++) {
			partitionsInfo[i][0] = initialPartitions.get(i).getName();
			partitionsInfo[i][1] = initialPartitions.get(i).getItem();
			partitionsInfo[i][2] = initialPartitions.get(i).getSize();
		}
		return partitionsInfo;
	}

	public static Object[][] processCompactsInfo(ArrayList<ReportCompact> reportsCompacts){
		Object[][] compactsInfo = new Object[reportsCompacts.size()][3];
		for (int i = 0; i < reportsCompacts.size(); i++) {
			compactsInfo[i][0] = reportsCompacts.get(i).getPartition1();
			compactsInfo[i][1] = reportsCompacts.get(i).getPartition2();
			compactsInfo[i][2] = reportsCompacts.get(i).getNewPartition();
		}
		return compactsInfo;
	}

	public static Object[][] processInfo(ArrayList<MyProcess> processes) {
		Object[][] processInfo = new Object[processes.size()][4];
		for (int i = 0; i < processes.size(); i++) {
			processInfo[i][0] = processes.get(i).getName();
			processInfo[i][1] = processes.get(i).getTime();
			processInfo[i][2] = processes.get(i).getSize();
			processInfo[i][3] = processes.get(i).isLocked();
		}
		return processInfo;
	}

	public static void main(String[] args) {
		Manager manager = new Manager();
		System.out.println(manager.add(new MyProcess("P11", 5, 11, false)));
		System.out.println(manager.add(new MyProcess("P15", 7, 15, false)));
		System.out.println(manager.add(new MyProcess("P18", 8, 18, false)));
		System.out.println(manager.add(new MyProcess("P6", 3, 6, false)));
		System.out.println(manager.add(new MyProcess("P9", 4, 9, false)));
		System.out.println(manager.add(new MyProcess("P13", 6, 13, false)));
		System.out.println(manager.add(new MyProcess("P20", 2, 20, false)));
		
		
		manager.initSimulation();
		manager.show();
		
	}
}
