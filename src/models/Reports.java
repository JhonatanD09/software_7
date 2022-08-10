package models;

import java.util.ArrayList;

public class Reports {

	private ArrayList<ReportCompact> reportCompacts;

	public Reports(ArrayList<ReportCompact> reportCompacts) {
		super();
		this.reportCompacts = reportCompacts;
	}

	public ArrayList<ReportCompact> getReportCompacts() {
		return reportCompacts;
	}

	public void setReportCompacts(ArrayList<ReportCompact> reportCompacts) {
		this.reportCompacts = reportCompacts;
	}
	
	
}
