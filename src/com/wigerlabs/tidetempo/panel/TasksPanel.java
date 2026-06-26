/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.wigerlabs.tidetempo.panel;

import com.wigerlabs.tidetempo.components.action_table.ActionTableCellEditor;
import com.wigerlabs.tidetempo.components.action_table.ActionTableCellRender;
import com.wigerlabs.tidetempo.components.action_table.ActionTableEvent;
import com.wigerlabs.tidetempo.connection.MySQL;
import com.wigerlabs.tidetempo.dialog.ConfirmationDialog;
import com.wigerlabs.tidetempo.dialog.SuccessDialog;
import com.wigerlabs.tidetempo.dialog.TaskAddEditDialog;
import com.wigerlabs.tidetempo.dialog.TaskViewDialog;
import com.wigerlabs.tidetempo.gui.HomeScreen;
import com.wigerlabs.tidetempo.util.Colors;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.Border;

/**
 *
 * @author malee
 */
public class TasksPanel extends javax.swing.JPanel {

    private HomeScreen homeScreen;

    /**
     * Creates new form TasksPanel
     *
     * @param parent
     */
    public TasksPanel(HomeScreen parent) {
        initComponents();
        this.homeScreen = parent;
        init();
        loadTasks();
        setupActionColumn();
    }

    private void init() {
        JTableHeader header = tasksTable.getTableHeader();
        header.setBackground(Colors.LIGHT_BLUE);
        header.setPreferredSize(new Dimension(0, 50));
        Border border = BorderFactory.createLineBorder(Colors.LIGHT_BLUE);
        tableScrollPane.setBorder(border);
    }

    public synchronized void loadTasks() {

        if (tasksTable.isEditing()) {
            tasksTable.getCellEditor().stopCellEditing();
        }

        DefaultTableModel dtm = (DefaultTableModel) tasksTable.getModel();
        dtm.setRowCount(0);
        try {
            ResultSet rs = MySQL.execute("SELECT t.id, t.title, t.start_time, t.end_time, IFNULL((SELECT SUM(minutes) FROM time_log WHERE task_id = t.id), 0) AS total_time_minutes, p.title AS project, s.name AS status, t.created_at, t.updated_at "
                    + "FROM task t "
                    + "INNER JOIN project p ON t.project_id = p.id "
                    + "INNER JOIN status s ON t.status_id = s.id "
                    + "ORDER BY p.id ASC");

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("project"));
                row.add(rs.getString("start_time"));
                row.add(rs.getString("end_time"));
                
                int totalMins = rs.getInt("total_time_minutes");
                int hrs = totalMins / 60;
                int mins = totalMins % 60;
                row.add(hrs > 0 ? hrs + "h " + mins + "m" : mins + "m");
                
                row.add(rs.getString("created_at"));
                row.add(rs.getString("updated_at"));
                row.add(rs.getString("status"));
                dtm.addRow(row);
            }
            SwingUtilities.updateComponentTreeUI(tasksTable);
            tasksTable.revalidate();
            tasksTable.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupActionColumn() {
        ActionTableEvent event = new ActionTableEvent() {
            @Override
            public void onEdit(int taskId) {
                TaskAddEditDialog taskAddEditDialog = new TaskAddEditDialog(homeScreen, true, true, taskId, TasksPanel.this);
                taskAddEditDialog.setLocationRelativeTo(null);
                taskAddEditDialog.setVisible(true);
            }

            @Override
            public void onView(int taskId) {
                TaskViewDialog taskViewDialog = new TaskViewDialog(homeScreen, true, taskId);
                taskViewDialog.setVisible(true);
            }

            @Override
            public void onDelete(int taskId) {
                ConfirmationDialog.show("Are you sure you want to delete this task?");
                int confirmationAnswer = ConfirmationDialog.getResult();
                if (confirmationAnswer == 0) {
                    try {
                        MySQL.execute("DELETE FROM task WHERE id='" + taskId + "'");
                        SwingUtilities.invokeLater(() -> {
                            loadTasks();
                            SuccessDialog.show("Task deleted successfully!");
                        });
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                homeScreen,
                                "Task Deleting Failed! Please try again later.",
                                "Task Deleteing Failed!",
                                JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStatusChange(int taskId) {
                ConfirmationDialog.show("Are you sure you want to change the status?");
                int confirmationAnswer = ConfirmationDialog.getResult();
                if (confirmationAnswer == 0) {
                    try {
                        ResultSet rs = MySQL.execute("SELECT status_id FROM task WHERE id='" + taskId + "'");
                        if (rs.next()) {
                            int currentStatus = rs.getInt("status_id");
                            int statusId = (currentStatus == 1) ? 2 : (currentStatus == 2) ? 3 : 1;
                            MySQL.execute("UPDATE task SET status_id='" + statusId + "' WHERE id='" + taskId + "'");
                            if (statusId == 3) {
                                MySQL.execute("UPDATE task SET end_time=NOW() WHERE id='" + taskId + "'");
                            } else {
                                MySQL.execute("UPDATE task SET end_time=NULL WHERE id='" + taskId + "'");
                            }
                            SwingUtilities.invokeLater(() -> {
                                loadTasks();
                                SuccessDialog.show("Task status have changed successfully!");
                            });
                        } else {
                            JOptionPane.showMessageDialog(
                                    homeScreen,
                                    "Status change failed. Please try again later.",
                                    "Failed to load task status!",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                homeScreen,
                                "Task Status Updating Failed! Please try again later.",
                                "Task Status Updating Failed!",
                                JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                }
            }
        };
        tasksTable.getColumnModel().getColumn(9).setCellRenderer(new ActionTableCellRender(8));
        tasksTable.getColumnModel().getColumn(9).setCellEditor(new ActionTableCellEditor(event, 8));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableScrollPane = new javax.swing.JScrollPane();
        tasksTable = new javax.swing.JTable();
        addTaskBtn = new javax.swing.JButton();

        setBackground(new java.awt.Color(2, 8, 23));
        setMinimumSize(new java.awt.Dimension(708, 588));

        tasksTable.setAutoCreateRowSorter(true);
        tasksTable.setBackground(new java.awt.Color(2, 8, 23));
        tasksTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tasksTable.setForeground(new java.awt.Color(255, 255, 255));
        tasksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Title", "Project", "Start at", "End at", "Total Time", "Created at", "Updated at", "Status", "Actions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tasksTable.setGridColor(new java.awt.Color(28, 39, 56));
        tasksTable.setRowHeight(40);
        tasksTable.setShowGrid(false);
        tasksTable.setShowHorizontalLines(true);
        tableScrollPane.setViewportView(tasksTable);
        if (tasksTable.getColumnModel().getColumnCount() > 0) {
            tasksTable.getColumnModel().getColumn(0).setMaxWidth(50);
            tasksTable.getColumnModel().getColumn(9).setMinWidth(180);
            tasksTable.getColumnModel().getColumn(9).setMaxWidth(180);
        }

        addTaskBtn.setBackground(new java.awt.Color(59, 130, 246));
        addTaskBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addTaskBtn.setText("New Task");
        addTaskBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addTaskBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTaskBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addTaskBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(addTaskBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addTaskBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTaskBtnActionPerformed
        TaskAddEditDialog taskAddEditDialog = new TaskAddEditDialog(homeScreen, true, TasksPanel.this);
        taskAddEditDialog.setLocationRelativeTo(null);
        taskAddEditDialog.setVisible(true);
    }//GEN-LAST:event_addTaskBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTaskBtn;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTable tasksTable;
    // End of variables declaration//GEN-END:variables
}
