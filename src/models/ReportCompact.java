package models;

public class ReportCompact {

	private String partition1, partition2, newPartition;
	private long size;
	public ReportCompact(String partition1, String partition2, String newPartition, long size) {
		super();
		this.partition1 = partition1;
		this.partition2 = partition2;
		this.newPartition = newPartition;
		this.size = size;
	}
	public String getPartition1() {
		return partition1;
	}
	public void setPartition1(String partition1) {
		this.partition1 = partition1;
	}
	public String getPartition2() {
		return partition2;
	}
	public void setPartition2(String partition2) {
		this.partition2 = partition2;
	}
	public String getNewPartition() {
		return newPartition;
	}
	public void setNewPartition(String newPartition) {
		this.newPartition = newPartition;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	
	
}
