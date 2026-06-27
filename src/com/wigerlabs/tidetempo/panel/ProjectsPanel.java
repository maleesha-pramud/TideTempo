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
import com.wigerlabs.tidetempo.dialog.ProjectAddEditDialog;
import com.wigerlabs.tidetempo.dialog.ProjectViewDialog;
import com.wigerlabs.tidetempo.dialog.SuccessDialog;
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
public class ProjectsPanel extends javax.swing.JPanel {

    private HomeScreen homeScreen;

    /**
     * Creates new form ProjectsPanel
     *
     * @param parent
     */
    public ProjectsPanel(HomeScreen parent) {
        initComponents();
        this.homeScreen = parent;
        init();
        loadProjects();
        setupActionColumn();
    }
    
    private void init() {
        JTableHeader header = projectsTable.getTableHeader();
        header.setBackground(Colors.LIGHT_BLUE);
        header.setPreferredSize(new Dimension(0, 50));
        Border border = BorderFactory.createLineBorder(Colors.LIGHT_BLUE);
        tableScrollPane.setBorder(border);
    }

    public synchronized void loadProjects() {

        if (projectsTable.isEditing()) {
            projectsTable.getCellEditor().stopCellEditing();
        }

        DefaultTableModel dtm = (DefaultTableModel) projectsTable.getModel();
        dtm.setRowCount(0);
        try {
            ResultSet rs = MySQL.execute("SELECT p.id, p.title, pr.name AS priority, c.name AS client, p.created_at, p.updated_at, s.name AS status "
                    + "FROM project p "
                    + "INNER JOIN priority pr ON p.priority_id = pr.id "
                    + "INNER JOIN client c ON p.client_id = c.id "
                    + "INNER JOIN status s ON p.status_id = s.id "
                    + "ORDER BY p.id ASC");

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("priority"));
                row.add(rs.getString("client"));
                row.add(rs.getString("created_at"));
                row.add(rs.getString("updated_at"));
                row.add(rs.getString("status"));
                dtm.addRow(row);
            }
            SwingUtilities.updateComponentTreeUI(projectsTable);
            projectsTable.revalidate();
            projectsTable.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupActionColumn() {
        ActionTableEvent event = new ActionTableEvent() {
            @Override
            public void onEdit(int projectId) {
                ProjectAddEditDialog projectEditDialog = new ProjectAddEditDialog(homeScreen, true, true, projectId, ProjectsPanel.this);
                projectEditDialog.setVisible(true);
            }

            @Override
            public void onView(int projectId) {
                try {
                    java.sql.ResultSet rs = com.wigerlabs.tidetempo.connection.MySQL.execute(
                            "SELECT p.title AS projectName, p.description AS projectDescription, " +
                            "c.name AS clientName, p.estimated_hours AS estimatedHours, s.name AS status " +
                            "FROM project p " +
                            "INNER JOIN client c ON p.client_id = c.id " +
                            "INNER JOIN status s ON p.status_id = s.id " +
                            "WHERE p.id = '" + projectId + "'");

                    if (rs.next()) {
                        java.io.InputStream filePath = getClass().getClassLoader().getResourceAsStream("com/wigerlabs/tidetempo/report/ProjectReport.jrxml");
                        java.util.HashMap<String, Object> parameters = new java.util.HashMap<>();

                        parameters.put("projectName", rs.getString("projectName"));
                        parameters.put("projectDescription", rs.getString("projectDescription"));
                        parameters.put("clientName", rs.getString("clientName"));
                        parameters.put("estimatedHours", rs.getDouble("estimatedHours"));
                        parameters.put("status", rs.getString("status"));
                        
                        // Calculate total logged hours
                        java.sql.ResultSet rsTime = com.wigerlabs.tidetempo.connection.MySQL.execute("SELECT IFNULL(SUM(tl.minutes), 0) AS total_minutes FROM time_log tl INNER JOIN task t ON tl.task_id = t.id WHERE t.project_id='" + projectId + "'");
                        double totalLoggedHours = 0;
                        if (rsTime.next()) {
                            totalLoggedHours = rsTime.getInt("total_minutes") / 60.0;
                        }
                        parameters.put("totalLoggedHours", totalLoggedHours);

                        // Fetch time log details for JRResultSetDataSource
                        java.sql.ResultSet rsLogs = com.wigerlabs.tidetempo.connection.MySQL.execute("SELECT t.title AS task_title, tl.created_at AS log_date, tl.minutes AS logged_minutes FROM time_log tl INNER JOIN task t ON tl.task_id = t.id WHERE t.project_id = '" + projectId + "' ORDER BY tl.created_at DESC");
                        
                        net.sf.jasperreports.engine.JasperReport compileReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(filePath);
                        net.sf.jasperreports.engine.JRResultSetDataSource dataSource = new net.sf.jasperreports.engine.JRResultSetDataSource(rsLogs);
                        net.sf.jasperreports.engine.JasperPrint fillReport = net.sf.jasperreports.engine.JasperFillManager.fillReport(compileReport, parameters, dataSource);
                        net.sf.jasperreports.view.JasperViewer.viewReport(fillReport, false);
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(homeScreen, "Project data not found.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(homeScreen, "Failed to generate report.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void onDelete(int projectId) {
                ConfirmationDialog.show("Are you sure you want to delete this project?");
                int confirmationAnswer = ConfirmationDialog.getResult();
                if (confirmationAnswer == 0) {
                    try {
                        MySQL.execute("DELETE FROM project WHERE id='" + projectId + "'");
                        SwingUtilities.invokeLater(() -> {
                            loadProjects();
                            SuccessDialog.show("Project deleted successfully!");
                        });
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                homeScreen,
                                "Project Deleting Failed! Please try again later.",
                                "Project Deleteing Failed!",
                                JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                }
            }

        };
        projectsTable.getColumnModel().getColumn(7).setCellRenderer(new ActionTableCellRender(6));
        projectsTable.getColumnModel().getColumn(7).setCellEditor(new ActionTableCellEditor(event, 6));

        javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(new String[]{"Pending", "In Progress", "Completed", "Cancelled"});
        statusCombo.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                int row = projectsTable.getSelectedRow();
                if (row != -1) {
                    String projectId = projectsTable.getValueAt(row, 0).toString();
                    String newStatus = (String) e.getItem();
                    int statusId = newStatus.equals("Pending") ? 1 : newStatus.equals("In Progress") ? 2 : newStatus.equals("Completed") ? 3 : 4;
                    try {
                        MySQL.execute("UPDATE project SET status_id='" + statusId + "' WHERE id='" + projectId + "'");
                    } catch(Exception ex) { ex.printStackTrace(); }
                }
            }
        });
        projectsTable.getColumnModel().getColumn(6).setCellEditor(new javax.swing.DefaultCellEditor(statusCombo));
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
        projectsTable = new javax.swing.JTable();
        addProjectBtn = new javax.swing.JButton();

        setBackground(new java.awt.Color(2, 8, 23));
        setMinimumSize(new java.awt.Dimension(708, 588));

        projectsTable.setAutoCreateRowSorter(true);
        projectsTable.setBackground(new java.awt.Color(2, 8, 23));
        projectsTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        projectsTable.setForeground(new java.awt.Color(255, 255, 255));
        projectsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Title", "Priority Status", "Client", "Created at", "Updated at", "Status", "Actions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        projectsTable.setGridColor(new java.awt.Color(28, 39, 56));
        projectsTable.setRowHeight(40);
        projectsTable.setShowGrid(false);
        projectsTable.setShowHorizontalLines(true);
        tableScrollPane.setViewportView(projectsTable);
        if (projectsTable.getColumnModel().getColumnCount() > 0) {
            projectsTable.getColumnModel().getColumn(0).setMaxWidth(50);
            projectsTable.getColumnModel().getColumn(7).setMinWidth(180);
            projectsTable.getColumnModel().getColumn(7).setMaxWidth(180);
        }

        addProjectBtn.setBackground(new java.awt.Color(59, 130, 246));
        addProjectBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addProjectBtn.setText("New Project");
        addProjectBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addProjectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProjectBtnActionPerformed(evt);
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
                        .addComponent(addProjectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(addProjectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addProjectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProjectBtnActionPerformed
        ProjectAddEditDialog projectAddDialog = new ProjectAddEditDialog(homeScreen, true, this);
        projectAddDialog.setVisible(true);
    }//GEN-LAST:event_addProjectBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProjectBtn;
    private javax.swing.JTable projectsTable;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables
}
