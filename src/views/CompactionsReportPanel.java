package views;

import models.ReportCompact;
import models.Reports;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class CompactionsReportPanel extends JPanel {

    public CompactionsReportPanel(ArrayList<Reports> reports){
        setBackground(Color.WHITE);
        setLayout(new GridLayout(reports.size(), 1, 10 , 10));
        initComponents(reports);
    }

    private void initComponents(ArrayList<Reports> reports){
        for (int i = 0; i < reports.size(); i++) {
            add(compactionPanel(reports.get(i), i + 1));
        }
    }

    private JPanel compactionPanel(Reports report, int compactionNumber){
        JPanel compactionPanel = new JPanel(new BorderLayout());
        compactionPanel.setBorder(new LineBorder(Color.BLACK));
        compactionPanel.setBackground(Color.decode("#16A085"));
        JLabel nameLb = new JLabel("Compactacion #" + compactionNumber);
        nameLb.setFont(new Font("Arial", Font.BOLD, 20));
        nameLb.setForeground(Color.WHITE);
        compactionPanel.add(nameLb, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new GridLayout(1, report.getReportCompacts().size(),5 ,5));
        for(ReportCompact reportCompact : report.getReportCompacts()){
            centerPanel.add(new PartitionReportPanel(reportCompact));
        }
        compactionPanel.add(centerPanel, BorderLayout.CENTER);
        return compactionPanel;
    }
}
