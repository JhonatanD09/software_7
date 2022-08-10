package models;

public class Partition implements Comparable<Partition>{
	
	private long size, time;
	private String name, item;
	private MyProcess myProcess;
	private boolean isFinish , isFree;
	private int start;
	
	
	public Partition(long size, long time, String name, MyProcess myProcess, int start) {
		super();
		this.size = size;
		this.time = time;
		this.name = name;
		this.myProcess = myProcess;
		this.isFinish = false;
		this.isFree = false;
		this.start = start;
	}
	

	public Partition(long size, long time, String name, int start) {
		super();
		this.size = size;
		this.time = time;
		this.name = name;
		this.myProcess = null;
		this.isFinish = true;
		this.start = start;
	}
	
	public Partition(long size, long time, String name, String item) {
		super();
		this.size = size;
		this.time = time;
		this.name = name;
		this.item = item;
		this.myProcess = null;
		this.isFinish = true;
	}
	
	public void timeDiscount() {
		if(myProcess.getTime()>0) {
			time--;
		}else {
			this.isFinish = true;
			this.isFree = true;
		}
	}

	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MyProcess getMyProcess() {
		return myProcess;
	}
	public void setMyProcess(MyProcess myProcess) {
		this.myProcess = myProcess;
	}
	
	public boolean isFinish() {
		return isFinish;
	}
	
	public boolean isFree() {
		return isFree;
	}
	
	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	
	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public String getItem() {
		return item;
	}

	@Override
	public int compareTo(Partition o) {
		return (int) (getTime()-o.getTime());
	}

	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}

	
}
