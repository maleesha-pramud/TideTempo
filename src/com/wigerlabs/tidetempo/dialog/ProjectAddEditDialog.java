/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.wigerlabs.tidetempo.dialog;

import com.wigerlabs.tidetempo.connection.MySQL;
import com.wigerlabs.tidetempo.panel.ProjectsPanel;
import com.wigerlabs.tidetempo.util.CurrentTimeGenerator;
import com.wigerlabs.tidetempo.util.ComboItem;
import com.wigerlabs.tidetempo.util.SessionManager;
import com.wigerlabs.tidetempo.util.TopBarStyle;
import com.wigerlabs.tidetempo.util.User;
import com.wigerlabs.tidetempo.validation.Validator;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author malee
 */
public class ProjectAddEditDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProjectAddEditDialog.class.getName());
    
    private List<ComboItem> clientList;
    private List<ComboItem> priorityList;
    private User userData;
    private boolean isEditing = false;
    private int projectId = 1;
    private ProjectsPanel projectsPanel;
    private double currentEstimatedHours = 0;

    /**
     * Creates new form ProjectAddDialog
     *
     * @param parent
     * @param modal
     */
    public ProjectAddEditDialog(java.awt.Frame parent, boolean modal, boolean isEditing, int projectId, ProjectsPanel projectsPanel) {
        super(parent, modal);
        initComponents();
        this.isEditing = isEditing;
        this.projectId = projectId;
        this.projectsPanel = projectsPanel;
        loadUserSessionData();
        loadPriorities();
        loadClients();
        init();
        TopBarStyle.getInstance().applyStyle(this);
    }
    
    public ProjectAddEditDialog(java.awt.Frame parent, boolean modal, ProjectsPanel projectsPanel) {
        super(parent, modal);
        initComponents();
        this.projectsPanel = projectsPanel;
        loadUserSessionData();
        loadPriorities();
        loadClients();
        init();
        TopBarStyle.getInstance().applyStyle(this);
    }
    
    private void init() {
        if (isEditing) {
            headerLabel.setText("Edit Project Details");
            projectButton.setText("Update Project");
            loadProjectData();
        } else {
            headerLabel.setText("Add Project Details");
            projectButton.setText("Create Project");
        }
    }
    
    private void loadProjectData() {
        try {
            ResultSet rs = MySQL.execute("SELECT p.title, p.description, p.estimated_hours, pr.id AS priority_id, pr.name AS priority, c.id AS client_id, c.name AS client "
                    + "FROM project p "
                    + "INNER JOIN priority pr ON p.priority_id = pr.id "
                    + "INNER JOIN client c ON p.client_id = c.id "
                    + "WHERE p.user_id='" + userData.id + "' AND p.id='" + projectId + "'");
            if (rs.next()) {
                titleField.setText(rs.getString("title"));
                descriptionArea.setText(rs.getString("description"));
                currentEstimatedHours = rs.getDouble("estimated_hours");
                estimatedHoursField.setText(String.valueOf(currentEstimatedHours));
                
                for (ComboItem p : priorityList) {
                    if (p.getName().equals(rs.getString("priority"))) {
                        priorityCombo.setSelectedItem(p);
                        break;
                    }
                }
                
                for (ComboItem c : clientList) {
                    if (c.getName().equals(rs.getString("client"))) {
                        clientCombo.setSelectedItem(c);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadUserSessionData() {
        userData = SessionManager.getUserSession();
    }
    
    private void loadClients() {
        clientList = new ArrayList<>();
        clientCombo.addItem(new ComboItem(0, "Select Client"));
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM `client`");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                ComboItem client = new ComboItem(id, name);
                clientList.add(client);
                clientCombo.addItem(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadPriorities() {
        priorityList = new ArrayList<>();
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM `priority`");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                ComboItem priority = new ComboItem(id, name);
                priorityList.add(priority);
                priorityCombo.addItem(priority);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        priorityLabel = new javax.swing.JLabel();
        priorityCombo = new javax.swing.JComboBox<>();
        projectButton = new javax.swing.JButton();
        clientLabel = new javax.swing.JLabel();
        clientCombo = new javax.swing.JComboBox<>();
        descriptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        estimatedHoursField = new javax.swing.JTextField();
        estimatedHoursLabel = new javax.swing.JLabel();

        estimatedHoursLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        estimatedHoursLabel.setText("Estimated Duration (Hours)");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add New Project");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(2, 8, 23));

        headerLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        headerLabel.setText("Add Project Details");

        titleLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        titleLabel.setText("Project Title");

        priorityLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        priorityLabel.setText("Priority");

        projectButton.setBackground(new java.awt.Color(59, 130, 246));
        projectButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        projectButton.setText("Create Project");
        projectButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        projectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectButtonActionPerformed(evt);
            }
        });

        clientLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clientLabel.setText("Client");

        descriptionLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        descriptionLabel.setText("Project Description");

        descriptionArea.setColumns(20);
        descriptionArea.setRows(5);
        jScrollPane1.setViewportView(descriptionArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(projectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(priorityCombo, 0, 293, Short.MAX_VALUE)
                            .addComponent(estimatedHoursField, 0, 293, Short.MAX_VALUE)
                            .addComponent(titleField)
                            .addComponent(clientCombo, 0, 293, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(priorityLabel)
                                    .addComponent(estimatedHoursLabel)
                                    .addComponent(titleLabel)
                                    .addComponent(headerLabel)
                                    .addComponent(clientLabel)
                                    .addComponent(descriptionLabel))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(17, 17, 17))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(headerLabel)
                .addGap(28, 28, 28)
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(descriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(clientLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(priorityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(priorityCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(estimatedHoursLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(estimatedHoursField, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(projectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void projectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectButtonActionPerformed
        String project_title = titleField.getText();
        String project_description = descriptionArea.getText();
        ComboItem priority = (ComboItem) priorityCombo.getSelectedItem();
        ComboItem client = (ComboItem) clientCombo.getSelectedItem();
        
        Timestamp nowTime = CurrentTimeGenerator.getInstance().getCurrentTime();
        
        if (!Validator.isInputFieldValid("Project Title", project_title)) {
            return;
        } else if (!Validator.isInputFieldValid("Project Description", project_description)) {
            return;
        } else if (!Validator.isComboBoxValid("Client", client.getId())) {
            return;
        } else if (!Validator.isComboBoxValid("Priority", priority.getId())) {
            return;
        }
        
        double estimatedHours = 0;
        try {
            estimatedHours = Double.parseDouble(estimatedHoursField.getText());
            if (estimatedHours < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid estimated hours.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (!isEditing) {
                MySQL.execute("INSERT INTO `project` (title, description, priority_id, user_id, client_id, status_id, created_at, estimated_hours) VALUES "
                        + "('" + project_title + "','" + project_description + "','" + priority.getId() + "','" + userData.id + "','" + client.getId() + "','" + 1 + "','" + nowTime + "'," + estimatedHours + ")");
                SuccessDialog.show("Project added successfully!");
            } else {
                MySQL.execute("UPDATE `project` "
                        + " SET title='" + project_title + "', description='" + project_description + "', priority_id='" + priority.getId() + "', client_id='" + client.getId() + "', estimated_hours=" + estimatedHours + ", updated_at='" + nowTime + "' WHERE id='" + projectId + "'");
                SuccessDialog.show("Project updated successfully!");
            }
            if (projectsPanel != null) {
                SwingUtilities.invokeLater(() -> {
                    projectsPanel.loadProjects();
                });
            }
            this.dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Please try again to add the same project.", "Project adding failed!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_projectButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<com.wigerlabs.tidetempo.util.ComboItem> clientCombo;
    private javax.swing.JLabel clientLabel;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextField estimatedHoursField;
    private javax.swing.JLabel estimatedHoursLabel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<com.wigerlabs.tidetempo.util.ComboItem> priorityCombo;
    private javax.swing.JLabel priorityLabel;
    private javax.swing.JButton projectButton;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
