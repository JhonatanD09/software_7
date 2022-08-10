package views;

import models.ReportCompact;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PartitionReportPanel extends JPanel {

    public PartitionReportPanel(ReportCompact report){
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        initComponents(report);
    }

    private void initComponents(ReportCompact report){
        JLabel nameLb = new JLabel(report.getName());
        nameLb.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel infoPanel = new JPanel(new BorderLayout());
        JPanel positionsPreviusPanel = positionsPanel(report.getPrevius().x, report.getPrevius().y, "Posicion de memoria previa: ");
        JPanel positionsNextPanel = positionsPanel(report.getNext().x, report.getNext().y, "Posicion de memoria nueva: ");
        infoPanel.add(nameLb, BorderLayout.NORTH);
        infoPanel.add(positionsPreviusPanel, BorderLayout.CENTER);
        infoPanel.add(positionsNextPanel, BorderLayout.SOUTH);
        infoPanel.setBackground(Color.WHITE);
        add(infoPanel);
    }

    private JPanel positionsPanel(int initial, int end, String title){
        JPanel positionsPanel = new JPanel(new GridLayout(1,2));
        positionsPanel.setBackground(Color.WHITE);
        positionsPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK, 1, true), title));
        JLabel lb1 = new JLabel("Desde: " + initial);
        lb1.setFont(new Font("Arial",Font.PLAIN,14));
        positionsPanel.add(lb1);
        JLabel lb2 = new JLabel("Hasta: " + end);
        lb2.setFont(new Font("Arial",Font.PLAIN,14));
        positionsPanel.add(lb2);
        return positionsPanel;
    }
}
