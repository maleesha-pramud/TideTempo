/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.wigerlabs.tidetempo.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.wigerlabs.tidetempo.connection.MySQL;
import com.wigerlabs.tidetempo.dialog.SuccessDialog;
import com.wigerlabs.tidetempo.util.Colors;
import com.wigerlabs.tidetempo.util.ComboItem;
import com.wigerlabs.tidetempo.util.SessionManager;
import com.wigerlabs.tidetempo.util.User;
import com.wigerlabs.tidetempo.validation.Validator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author malee
 */
public class StartWorkingPanel extends javax.swing.JPanel {

    private Timer timer;
    private int seconds = 0;
    private boolean isPlay = false;
    private List<ComboItem> projectList;
    private List<ComboItem> taskList;
    private final User userData;

    /**
     * Creates new form ProjectsPanel
     */
    public StartWorkingPanel() {
        initComponents();
        init();
        loadProjects();
        userData = SessionManager.getUserSession();
    }

    private void init() {
        stopTimer();
        startStopButton.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
        pauseButton.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
        resetButton.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
        resetButton.setIcon(new FlatSVGIcon("com/wigerlabs/tidetempo/img/reset_icon.svg", 30, 30));
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                updateTimerLabel();
            }
        });
        taskCombo.setEnabled(false);
        finalTimePanel.setVisible(false);
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                if (!isPlay) {
                    reloadSelections();
                }
            }
        });
    }

    private void reloadSelections() {
        com.wigerlabs.tidetempo.util.ComboItem selectedProject = (com.wigerlabs.tidetempo.util.ComboItem) projectCombo.getSelectedItem();
        com.wigerlabs.tidetempo.util.ComboItem selectedTask = (com.wigerlabs.tidetempo.util.ComboItem) taskCombo.getSelectedItem();

        loadProjects();

        if (selectedProject != null && selectedProject.getId() != 0) {
            for (int i = 0; i < projectCombo.getItemCount(); i++) {
                if (projectCombo.getItemAt(i).getId() == selectedProject.getId()) {
                    projectCombo.setSelectedIndex(i);
                    break;
                }
            }
            if (selectedTask != null && selectedTask.getId() != 0) {
                for (int i = 0; i < taskCombo.getItemCount(); i++) {
                    if (taskCombo.getItemAt(i).getId() == selectedTask.getId()) {
                        taskCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void loadProjects() {
        projectList = new ArrayList<>();
        projectCombo.removeAllItems();
        projectCombo.addItem(new ComboItem(0, "Select Project"));
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM `project`");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                ComboItem client = new ComboItem(id, title);
                projectList.add(client);
                projectCombo.addItem(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks(int projectId) {
        taskList = new ArrayList<>();
        resetTaskCombo();
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM `task` WHERE project_id = '" + projectId + "'");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                ComboItem client = new ComboItem(id, title);
                taskList.add(client);
                taskCombo.addItem(client);
            }

            taskCombo.setEnabled(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetTaskCombo() {
        taskCombo.removeAllItems();
        taskCombo.addItem(new ComboItem(0, "Select Task"));
    }

    private void updateTimerLabel() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, secs));
    }

    private void updateFinalTimeLabel() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        finalTimeLabel.setText(String.format("%02d Hours %02d Mins %02d Secs", hours, minutes, secs));
    }

    private void stopTimer() {
        isPlay = false;
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        pauseButton.setIcon(new FlatSVGIcon("com/wigerlabs/tidetempo/img/play_icon.svg", 30, 30));
        startStopButton.setText("START");
        startStopButton.setBackground(Colors.GREEN);
    }

    private void startTimer() {
        isPlay = true;
        timer.start();
        pauseButton.setIcon(new FlatSVGIcon("com/wigerlabs/tidetempo/img/pause_icon.svg", 25, 25));
        startStopButton.setText("STOP");
        startStopButton.setBackground(Colors.RED);
        finalTimePanel.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        startStopButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        timerLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        projectLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        projectCombo = new javax.swing.JComboBox<>();
        taskCombo = new javax.swing.JComboBox<>();
        finalTimePanel = new javax.swing.JPanel();
        finalTimeLabel = new javax.swing.JLabel();
        addToTaskBtn = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(708, 588));
        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        mainPanel.setBackground(new java.awt.Color(2, 8, 23));

        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new java.awt.Dimension(250, 250));

        buttonsPanel.setOpaque(false);

        startStopButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        startStopButton.setText("START");
        startStopButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        startStopButton.setMaximumSize(new java.awt.Dimension(400, 400));
        startStopButton.setPreferredSize(new java.awt.Dimension(250, 250));
        startStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopButtonActionPerformed(evt);
            }
        });

        pauseButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        resetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
            .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(buttonsPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(startStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(85, Short.MAX_VALUE))
            .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(buttonsPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(startStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        timerLabel.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        timerLabel.setText("00:00:00");

        javax.swing.GroupLayout centerPanelLayout = new javax.swing.GroupLayout(centerPanel);
        centerPanel.setLayout(centerPanelLayout);
        centerPanelLayout.setHorizontalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, centerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(timerLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        centerPanelLayout.setVerticalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerPanelLayout.createSequentialGroup()
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(timerLabel))
        );

        jPanel1.setOpaque(false);

        projectLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        projectLabel.setText("Project");

        statusLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        statusLabel.setText("Task");

        projectCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectComboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectLabel)
                    .addComponent(projectCombo, 0, 320, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusLabel)
                    .addComponent(taskCombo, 0, 320, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(projectLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(projectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(taskCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        finalTimePanel.setOpaque(false);

        finalTimeLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        finalTimeLabel.setText("00 Hours 00 Mins 00 Secs");

        addToTaskBtn.setBackground(new java.awt.Color(59, 130, 246));
        addToTaskBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addToTaskBtn.setForeground(new java.awt.Color(255, 255, 255));
        addToTaskBtn.setText("Add To Task");
        addToTaskBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addToTaskBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToTaskBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout finalTimePanelLayout = new javax.swing.GroupLayout(finalTimePanel);
        finalTimePanel.setLayout(finalTimePanelLayout);
        finalTimePanelLayout.setHorizontalGroup(
            finalTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(finalTimePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(finalTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addToTaskBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        finalTimePanelLayout.setVerticalGroup(
            finalTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(finalTimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addToTaskBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, finalTimePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(finalTimeLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(centerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(finalTimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(centerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(finalTimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void startStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStopButtonActionPerformed
        if (isPlay) {
            stopTimer();
            updateFinalTimeLabel();
            finalTimePanel.setVisible(true);
        } else {
            startTimer();
        }
    }//GEN-LAST:event_startStopButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        stopTimer();
        seconds = 0;
        timerLabel.setText("00:00:00");
    }//GEN-LAST:event_resetButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        if (isPlay) {
            stopTimer();
        } else {
            startTimer();
        }
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void projectComboActionPerformed(java.awt.event.ActionEvent evt) {
        ComboItem project = (ComboItem) projectCombo.getSelectedItem();
        if (project == null) return;
        int projectId = project.getId();
        if (projectId != 0) {
            loadTasks(projectId);
        } else {
            resetTaskCombo();
            taskCombo.setEnabled(false);
        }
    }

    private void addToTaskBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToTaskBtnActionPerformed
        int loggedMinutes = seconds / 60;
        ComboItem task = (ComboItem) taskCombo.getSelectedItem();
        ComboItem project = (ComboItem) projectCombo.getSelectedItem();

        if (project == null || !Validator.isComboBoxValid("Project Combo", project.getId())) {
            return;
        } else if (task == null || !Validator.isComboBoxValid("Task Combo", task.getId())) {
            return;
        }

        User currentUser = SessionManager.getUserSession();
        if (currentUser == null || currentUser.id == 0) {
            JOptionPane.showMessageDialog(this, "Invalid user session. Please login again.", "Session Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            MySQL.execute("INSERT INTO `time_log` (user_id, task_id, minutes) VALUES ('" + currentUser.id + "', '" + task.getId() + "', '" + loggedMinutes + "')");
            MySQL.execute("UPDATE `task` SET `start_time` = NOW() WHERE `id` = '" + task.getId() + "' AND `start_time` IS NULL");
            SuccessDialog.show("Time added to the task successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Please recheck the task and try again.", "Time adding failed!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_addToTaskBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToTaskBtn;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel finalTimeLabel;
    private javax.swing.JPanel finalTimePanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton pauseButton;
    private javax.swing.JComboBox<com.wigerlabs.tidetempo.util.ComboItem> projectCombo;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton startStopButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JComboBox<com.wigerlabs.tidetempo.util.ComboItem> taskCombo;
    private javax.swing.JLabel timerLabel;
    // End of variables declaration//GEN-END:variables
}
