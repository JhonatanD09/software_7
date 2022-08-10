package presenters;

import exceptions.*;
import models.Manager;
import models.MyProcess;
import views.AddProcessDialog;
import views.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Presenter implements ActionListener {

    private Manager manager;
    private MainFrame mainFrame;
    private AddProcessDialog addProcessDialog;
    private AddProcessDialog editProcessDialog;

    public Presenter() {
        manager = new Manager();
        mainFrame = new MainFrame(this);
        manager.add(new MyProcess("P15", 3, 15, false));
        manager.add(new MyProcess("P13", 5, 13, false));
        manager.add(new MyProcess("P4", 2, 4, false));
        manager.add(new MyProcess("P12", 4, 12, false));
        manager.add(new MyProcess("P18", 6, 18, false));
        manager.add(new MyProcess("P5", 7, 5, false));
        manager.add(new MyProcess("P8", 3, 8, false));
        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (Events.valueOf(e.getActionCommand())) {
            case ADD_PROCESS:
                manageAddProcessAction();
                break;
            case EDIT_PROCESS:
                manageEditProcessAction(e);
                break;
            case DELETE_PROCESS:
                manageDeleteProcessAction(e);
                break;
            case ACCEPT_ADD_PROCESS:
                manageAcceptAddProcess();
                break;
            case ACCEPT_EDIT_PROCESS:
                manageAcceptEditProcessAction(e);
                break;
            case CANCEL_ADD_PROCESS:
                manageCancelAddProcessAction();
                break;
            case CANCEL_EDIT_PROCESS:
                manageCancelEditProcessAction();
                break;
            case INIT_SIMULATION:
                manageInitSimulationAction();
                break;
            case NEW_SIMULATION:
                manageNewSimulationAction();
                break;
            case EXIT:
                System.exit(0);
                break;
        }
    }

    private void manageNewSimulationAction() {
        manager = new Manager();
        mainFrame.newSimulation();
    }

    private void manageInitSimulationAction() {
        if (!manager.getProcesses().isEmpty()) {
            manager.initSimulation();
            mainFrame.initReportsPanel(manager.getProcesses(), manager.getTerminatedPartitions(),
                    manager.getAllPartitions(), manager.getProcessesTerminated(), manager.getReportCompacts(),
                    manager.getAllPartitions().get(manager.getAllPartitions().size()-1));
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Debe haber almenos un proceso para poder iniciar la simulacion",
                    "ALERTA", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void manageEditProcessAction(ActionEvent e) {
        String processName = ((JButton) e.getSource()).getName();
        MyProcess process = manager.searchProcess(processName);
        editProcessDialog = new AddProcessDialog(this, true);
        editProcessDialog.setInitialInfo(process.getName(), String.valueOf(process.getTime()),
                String.valueOf(process.getSize()), process.isLocked());
        editProcessDialog.setVisible(true);
    }

    private void manageCancelEditProcessAction() {
        editProcessDialog.dispose();
    }

    private void manageAcceptEditProcessAction(ActionEvent e) {
        try {
            String actualName = ((JButton) e.getSource()).getName();
            if (!actualName.equals(editProcessDialog.getProcessName())) {
                manager.verifyProcessName(editProcessDialog.getProcessName());
            }
            manager.edit(actualName, editProcessDialog.getProcessName(), editProcessDialog.getProcessSize(),
                    editProcessDialog.getProcessTime(), editProcessDialog.getIsBlocked());
            mainFrame.updateProcesses(manager.getProcesses());
            editProcessDialog.dispose();
        } catch (EmptyProcessNameException | RepeatedNameException | EmptyProcessSizeException
                 | EmptyProcessTimeException | InvalidSizeException | InvalidTimeException ex) {
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage());
        }
    }

    private void manageDeleteProcessAction(ActionEvent e) {
        String processName = ((JButton) e.getSource()).getName();
        if (manager.delete(processName)) {
            mainFrame.updateProcesses(manager.getProcesses());
            JOptionPane.showMessageDialog(mainFrame, "Proceso eliminado con exito", "Eliminar",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "No se ha podido eliminar el proceso", "ERROR!!!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageAcceptAddProcess() {
        try {
            String processName = addProcessDialog.getProcessName();
            manager.verifyProcessName(processName);
            long time = addProcessDialog.getProcessTime();
            long size = addProcessDialog.getProcessSize();
            boolean isBlocked = addProcessDialog.getIsBlocked();
            manager.add(new MyProcess(processName, time, size, isBlocked));
            mainFrame.updateProcesses(manager.getProcesses());
            addProcessDialog.dispose();
        } catch (EmptyProcessNameException | EmptyProcessTimeException | EmptyProcessSizeException
                 | RepeatedNameException | InvalidTimeException | InvalidSizeException ex) {
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "ERROR!!!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageAddProcessAction() {
        addProcessDialog = new AddProcessDialog(this, false);
        addProcessDialog.setVisible(true);
    }

    private void manageCancelAddProcessAction() {
        addProcessDialog.dispose();
    }
}