package models;

import exceptions.RepeatedNameException;

import java.awt.Point;
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
	private ArrayList<Reports> reports;
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
		this.reports = new ArrayList<>();
	}

	// -----------------------------CRUD-----------------------------------

	public boolean add(MyProcess myProcess) {
		if (searchRepeat(myProcess.getName())) {
			processes.add(myProcess);
			return true;
		} else {
			return false;
		}
	}

	public boolean edit(String name, String newName, long size, long time, boolean isLocked) {
		boolean isEdit = false;
		if (searchProcess(name) != null) {
			MyProcess temp = searchProcess(name);
			if (searchRepeat(newName) || newName.equals(name)) {
				edit(newName, size, time, isLocked, temp);
				isEdit = true;
			}
		}
		return isEdit;
	}

	private void edit(String newName, long size, long time, boolean isLocked, MyProcess temp) {
		temp.setName(newName);
		temp.setSize(size);
		temp.updateTime(time);
		temp.setLocked(isLocked);
	}

	public boolean delete(String nameProcess) {
		if (searchProcess(nameProcess) != null) {
			processes.remove(searchProcess(nameProcess));
			return true;
		} else {
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

	public MyProcess searchProcess(String nameProcess) {
		for (MyProcess myProcess : processes) {
			if (myProcess.getName().equalsIgnoreCase(nameProcess)) {
				return myProcess;
			}
		}
		return null;
	}

	// -----------------------------END CRUD-----------------------------------

	// -----------------------------LOGIC---------------------------------------

	public void initSimulation() {
		fillQueue();
		int index = 0;
		while (!queue.isEmpty() || valideFinish()) {
			if (index >= partitions.size()) {
				index = 0;
				if (!partitionsToCompacte().isEmpty()) {
					compacte(partitionsToCompacte());
				}
				nextProccess();

			}
			Partition actualPartition = partitions.get(index);
			if (actualPartition.getMyProcess() != null) {
				actualPartition.timeDiscount();
			}
			index++;
		}

	}

	private boolean valideFinish() {
		for (Partition partition : partitions) {
			if (partition.getTime() > 0) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<Partition> partitionsToCompacte() {
		ArrayList<Partition> partitions = new ArrayList<>();
		for (int i = 0; i < this.partitions.size() - 1; i++) {
			if (this.partitions.get(i).getTime() == 0) {
				partitions.add(this.partitions.get(i));
			}
		}
		return partitions;
	}

	private boolean nextProccess() {
		Node<MyProcess> node = queue.peek();
		while (node != null) {
			int index = valideJoinProcess(node);
			if (index > -1) {
				joinProcess(node, index);
				node = null;
				return true;
			} else {
				node = node.getNext();
			}
		}
		return false;
	}

	private void joinProcess(Node<MyProcess> node, int i) {
		Partition partitionN = new Partition(node.getData().getSize(), node.getData().getTime(), "PAR" + count,
				node.getData().getName());
		long size = partitions.get(i).getSize() - node.getData().getSize();
		System.out.println(partitions.get(i).getStart()+"-----------");
		partitions.get(i).setName("PAR" + count);
		partitions.get(i).setSize(node.getData().getSize());
		partitions.get(i).setTime(node.getData().getTime());
		partitions.get(i).setStart(partitions.get(i).getStart());
		partitions.get(i).setMyProcess(node.getData());
		partitions.get(i).setFinish(false);
		terminatedPartitions
				.add(new Partition(partitions.get(i).getSize(), node.getData().getTime(), partitions.get(i).getName(),0));
		processesTerminated.add(new MyProcess(node.getData().getName(), node.getData().getTime(),
				node.getData().getSize(), node.getData().isLocked()));
		allPartitions.add(partitionN);
		count++;
		if (size > 0) {
			Partition partitionNL = new Partition(size, 0, "PAR" + count, "Libre");
			allPartitions.add(partitionNL);
			partitions.add(i + 1, partitionNL);
			count++;
		}
		queue.delete(node);
	}

	private int valideJoinProcess(Node<MyProcess> proccess) {
		for (int i = 0; i < partitions.size(); i++) {
			if (partitions.get(i).getSize() >= proccess.getData().getSize() && partitions.get(i).getTime() == 0) {
				return i;
			}
		}
		return -1;
	}

	private void compacte(ArrayList<Partition> partitions2) {
		Partition temp = partitions2.get(0);
		generateReport(temp);
		Partition np = new Partition(size(partitions2) + partitions.get(partitions.size() - 1).getSize(), 0,
				"PAR" + count, "Compactacion");
		allPartitions.add(np);
		for (Partition partition : partitions2) {
			if (partition.getMyProcess() != null) {
				queue.delete(nodeProcess(partition.getMyProcess().getName()));
			}
			this.partitions.remove(partition);
		}
		this.partitions.remove(partitions.size() - 1);
		partitions.add(new Partition(np.getSize(), np.getTime(), np.getName(),(int) (size-np.getSize())));
		count++;
	}

	private void generateReport(Partition temp) {
		int index = indexPartition(temp);
		ArrayList<ReportCompact> list = new ArrayList<>();
		for (int i = index+1; i < partitions.size() - 1; i++) {
			if (!(partitions.get(i).getTime()==0)) {				
				list.add(new ReportCompact(partitions.get(i).getName(),
						new Point(partitions.get(i).getStart(),
								(int) (partitions.get(i).getStart() + partitions.get(i).getSize())),
						new Point((int) (partitions.get(i).getStart() - temp.getSize()),
								(int) ((partitions.get(i).getStart() + partitions.get(i).getSize())
										- temp.getSize()))));
				partitions.get(i).setStart((int) (partitions.get(i).getStart() - temp.getSize()));
			}
		}
		reports.add(new Reports(list));
	}

	private int indexPartition(Partition temp) {
		int index = 0;
		for (int i = 0; i < partitions.size(); i++) {
			if (temp.getName().equals(partitions.get(i).getName())) {
				index = i;
			}
		}
		return index;
	}

	private long size(ArrayList<Partition> partitions) {
		long size = 0;
		for (Partition partition : partitions) {
			size += partition.getSize();
		}
		return size;
	}

	private void fillQueue() {
		int size = 0;

		for (MyProcess myProcess : processes) {
			queue.push(myProcess);
		}

		int initial = 0;

		for (MyProcess myProcess : processes) {
			if (size + myProcess.getSize() <= this.size) {
				size += myProcess.getSize();
				System.out.println("aa" + size);
				newPartition(myProcess, partitions, initial);
				initial += myProcess.getSize();
				newPartition(myProcess, terminatedPartitions, initial);
				queue.delete(nodeProcess(myProcess.getName()));
				allPartitions.add(
						new Partition(myProcess.getSize(), myProcess.getTime(), "PAR" + count, myProcess.getName()));
				newProcess(myProcess, processesTerminated);
				count++;
			}

		}
		if (this.size - size > 0) {
			allPartitions.add(new Partition(this.size - size, 0, "PAR" + count, "Libre"));
			partitions.add(new Partition(this.size - size, 0, "PAR" + count, "Libre"));
			count++;
		}
		
		for (Partition partition : partitions) {
			System.out.println(partition.getName()+" " + partition.getStart());
		}
		Collections.sort(terminatedPartitions);
		Collections.sort(processesTerminated);
	}

	private void newPartition(MyProcess myProcess, ArrayList<Partition> partitions, int initial) {
		partitions.add(new Partition(myProcess.getSize(), myProcess.getTime(), "PAR" + count, myProcess, initial));
	}

	private void newProcess(MyProcess process, ArrayList<MyProcess> processes) {
		processes.add(new MyProcess(process.getName(), process.getTime(), process.getSize(), process.isLocked()));
	}

	private Node<MyProcess> nodeProcess(String name) {
		Node<MyProcess> tempNode = queue.getFirstNode();
		while (tempNode != null) {
			if (tempNode.getData().getName().equalsIgnoreCase(name)) {
				return tempNode;
			} else {
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
	// ------------------------------END LOGIC----------------------------------

	// -----------------------------GETTERS AND SETERS-------------------------

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

	// -------------------------------TEST
	// SHOW------------------------------------------

	public void show() {
		for (Reports r : reports) {
			System.out.println("----------------------------------");
			for (ReportCompact rc : r.getReportCompacts()) {
				System.out.println(rc.getName()+" " + rc.getPrevius()+ " " + rc.getNext());
			} 
			System.out.println("----------------------------------");
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

//	public static Object[][] processCompactsInfo(ArrayList<ReportCompact> reportsCompacts) {
//		Object[][] compactsInfo = new Object[reportsCompacts.size()][3];
//		for (int i = 0; i < reportsCompacts.size(); i++) {
//			compactsInfo[i][0] = reportsCompacts.get(i).getPartition1();
//			compactsInfo[i][1] = reportsCompacts.get(i).getPartition2();
//			compactsInfo[i][2] = reportsCompacts.get(i).getNewPartition();
//		}
//		return compactsInfo;
//	}

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
		manager.add(new MyProcess("P15", 3, 15, false));
		manager.add(new MyProcess("P13", 5, 13, false));
		manager.add(new MyProcess("P4", 2, 4, false));
		manager.add(new MyProcess("P12", 4, 12, false));
		manager.add(new MyProcess("P18", 6, 18, false));
		manager.add(new MyProcess("P5", 7, 5, false));
		manager.add(new MyProcess("P8", 3, 8, false));

		manager.initSimulation();
		manager.show();

	}
}
