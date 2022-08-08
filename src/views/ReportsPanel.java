package views;

import models.Manager;
import models.MyProcess;
import models.Partition;
import models.ReportCompact;
import presenters.Events;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ReportsPanel extends JPanel {

    private static final String[] TERMINED_COLUMNS = {"Nombre", "Tiempo"};
    private static final String[] INITIAL_PARTITIONS_COLUMNS = {"Nombre", "Tamaño"};
    private static final String[] INITIAL_PARTITIONS_COLUMNS2 = {"Nombre","Proceso", "Tamaño"};
    private static final String[] COMPACTS_COLUMNS = {"Particion 1","Particion 2", "Nueva particion"};
    private static final String NEW_SIMULATION_BTN_TXT = "Nueva simulacion";
    private static final Color BLUE_COLOR = Color.decode("#2980B9");

    public ReportsPanel(ActionListener listener, ArrayList<MyProcess> processes,
                        ArrayList<Partition> initialPartitions, ArrayList<Partition> terminatedPartitions, ArrayList<MyProcess> processesTermined,
                        ArrayList<ReportCompact> compactsInfo, Partition finalPartition ){
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FDFEFE"));
        initTitle();
        addReports (processes, initialPartitions,terminatedPartitions, processesTermined,compactsInfo,finalPartition);
        initNewSimulationBtn(listener);
    }

    public void addReports( ArrayList<MyProcess> processes,
                           ArrayList<Partition> initialPartitions,ArrayList<Partition> terminatedPartitions, ArrayList<MyProcess> processesTermined,
                            ArrayList<ReportCompact> compactsInfo, Partition finalPartition){
        JTabbedPane reports = new JTabbedPane();
        reports.setFont(new Font("Arial", Font.BOLD, 18));
//        for(Partition partition : partitions){
//            PartitionReportsPanel partitionReportsPanel = new PartitionReportsPanel(partition.getReadyProccess(),
//                    partition.getProcessDespachados(), partition.getExecuting(), partition.getProcessExpired(),
//                    partition.getProcessLocked(), partition.getProcessTerminated());
//            reports.add(partitionReportsPanel, partition.getName());
//        }
//        TablePanel reportProcessesPanel = new TablePanel(Partition.processInfo(processes), COLUMNS);
//        reports.add("Listos", reportProcessesPanel);

        TablePanel reportInitialPartitions = new TablePanel(Manager.processInfo(processes),
                INITIAL_PARTITIONS_COLUMNS);
        reports.add("Procesos iniciales", reportInitialPartitions);
        
        TablePanel reportTerminatedPartitions = new TablePanel(Manager.processInitialPartitionsInfo(initialPartitions),
                INITIAL_PARTITIONS_COLUMNS);
        reports.add("Terminacion de particiones", reportTerminatedPartitions);
        
        TablePanel reportAllPartitions = new TablePanel(Manager.allPartitions(terminatedPartitions),
                INITIAL_PARTITIONS_COLUMNS2);
        reports.add("Particiones creadas", reportAllPartitions);
        
        TablePanel terminedProcessesTable = new TablePanel(Manager.processProcessTermiedInfo(processesTermined), TERMINED_COLUMNS);
        reports.add("Orden terminacion procesos", terminedProcessesTable);
        JLabel dataJLabel = new JLabel("La particion final es: "+ finalPartition.getName() + " con un tamaño de : "+ finalPartition.getSize(), SwingConstants.CENTER);
        setLabel(dataJLabel);
        reports.add("Particion final",dataJLabel);

        TablePanel compactsTable = new TablePanel(Manager.processCompactsInfo(compactsInfo), COMPACTS_COLUMNS);
        reports.add("Compactaciones", compactsTable);

        add(reports, BorderLayout.CENTER);
    }

    private void setLabel(JLabel label) {
    	label.setFont(new Font("Arial", Font.BOLD, 25));
    	label.setOpaque(true);
    	label.setBackground(Color.WHITE);
    }
    
    private void initNewSimulationBtn(ActionListener listener){
        JButton newSimulationBtn = new JButton(NEW_SIMULATION_BTN_TXT);
        newSimulationBtn.setFont(new Font("Arial", Font.BOLD, 20));
        newSimulationBtn.setForeground(Color.WHITE);
        newSimulationBtn.setBackground(BLUE_COLOR);
        newSimulationBtn.addActionListener(listener);
        newSimulationBtn.setActionCommand(Events.NEW_SIMULATION.toString());
        add(newSimulationBtn, BorderLayout.SOUTH);
    }

    private void initTitle(){
        JLabel titleLb = new JLabel("REPORTES");
        titleLb.setFont(new Font("Arial", Font.BOLD, 16));
        titleLb.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLb, BorderLayout.NORTH);
    }
}