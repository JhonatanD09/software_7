package models;

import java.awt.Point;

public class ReportCompact {
	
	private String name;
	private Point previus;
	private Point next;
	public ReportCompact(String name, Point previus, Point next) {
		super();
		this.name = name;
		this.previus = previus;
		this.next = next;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Point getPrevius() {
		return previus;
	}
	public void setPrevius(Point previus) {
		this.previus = previus;
	}
	public Point getNext() {
		return next;
	}
	public void setNext(Point next) {
		this.next = next;
	}
	
	
	
}
