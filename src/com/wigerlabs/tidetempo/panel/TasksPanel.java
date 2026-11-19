/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.wigerlabs.tidetempo.panel;

import com.wigerlabs.tidetempo.components.StatusCellEditor;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.JTableHeader;
import javax.swing.border.Border;

/**
 *
 * @author malee
 */
public class TasksPanel extends javax.swing.JPanel {

    private static final String[] STATUSES = {"Pending", "In Progress", "Completed", "Cancelled"};
    private static final Color HIGH_PRIORITY_BG = new Color(88, 28, 36);

    private HomeScreen homeScreen;
    private JTextField searchField;
    private JTabbedPane statusTabs;
    private final Set<String> highPriorityTaskIds = new HashSet<>();

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
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadTasks();
            }
        });

        setupToolbar();
        setupHighlightRenderer();
    }

    // Replaces the generated layout at runtime with: [search | New Task] / status tabs / table.
    private void setupToolbar() {
        removeAll();
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 6, 6, 6));

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search tasks...");
        searchField.setPreferredSize(new Dimension(0, 35));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });

        addTaskBtn.setPreferredSize(new Dimension(122, 35));
        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);
        topRow.add(searchField, BorderLayout.CENTER);
        topRow.add(addTaskBtn, BorderLayout.EAST);

        statusTabs = new JTabbedPane();
        for (String status : STATUSES) {
            JPanel empty = new JPanel();
            empty.setPreferredSize(new Dimension(0, 0));
            statusTabs.addTab(status, empty);
        }
        statusTabs.setSelectedIndex(1); // In Progress by default
        statusTabs.addChangeListener(e -> applyFilter());

        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.setOpaque(false);
        north.add(topRow, BorderLayout.NORTH);
        north.add(statusTabs, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        applyFilter();
    }

    // Highlights rows whose project has High / Urgent priority.
    private void setupHighlightRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String id = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(row), 0));
                    if (highPriorityTaskIds.contains(id)) {
                        c.setBackground(HIGH_PRIORITY_BG);
                    }
                }
                return c;
            }
        };
        for (int i = 0; i <= 8; i++) {
            tasksTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void applyFilter() {
        if (tasksTable == null || !(tasksTable.getRowSorter() instanceof TableRowSorter) || statusTabs == null) {
            return;
        }
        TableRowSorter<?> sorter = (TableRowSorter<?>) tasksTable.getRowSorter();
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        filters.add(RowFilter.regexFilter("^" + Pattern.quote(STATUSES[statusTabs.getSelectedIndex()]) + "$", 8));
        String text = searchField.getText().trim();
        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0, 1, 2, 3, 4, 5, 6, 7, 8));
        }
        ((TableRowSorter<DefaultTableModel>) sorter).setRowFilter(RowFilter.andFilter(filters));
    }

    public synchronized void loadTasks() {

        if (tasksTable.isEditing()) {
            tasksTable.getCellEditor().stopCellEditing();
        }

        DefaultTableModel dtm = (DefaultTableModel) tasksTable.getModel();
        dtm.setRowCount(0);
        highPriorityTaskIds.clear();
        try {
            ResultSet rs = MySQL.execute("SELECT t.id, t.title, t.start_time, t.end_time, IFNULL((SELECT SUM(minutes) FROM time_log WHERE task_id = t.id), 0) AS total_time_minutes, p.title AS project, s.name AS status, pr.name AS priority, t.created_at, t.updated_at "
                    + "FROM task t "
                    + "INNER JOIN project p ON t.project_id = p.id "
                    + "INNER JOIN status s ON t.status_id = s.id "
                    + "LEFT JOIN priority pr ON p.priority_id = pr.id "
                    + "WHERE p.user_id='" + com.wigerlabs.tidetempo.util.SessionManager.getUserId() + "' "
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
                String priority = rs.getString("priority");
                if ("High".equalsIgnoreCase(priority) || "Urgent".equalsIgnoreCase(priority)) {
                    highPriorityTaskIds.add(rs.getString("id"));
                }
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

        };
        tasksTable.getColumnModel().getColumn(9).setCellRenderer(new ActionTableCellRender(8));
        tasksTable.getColumnModel().getColumn(9).setCellEditor(new ActionTableCellEditor(event, 8));
        
        tasksTable.getColumnModel().getColumn(8).setCellEditor(new StatusCellEditor(STATUSES, (taskId, newStatus) -> {
            boolean completed = newStatus.equals("Completed");
            boolean started = newStatus.equals("In Progress") || completed;
            try {
                MySQL.execute("UPDATE task SET status_id=(SELECT id FROM status WHERE name='" + newStatus + "'), "
                        + "start_time=" + (started ? "IFNULL(start_time, NOW())" : "start_time") + ", "
                        + "end_time=" + (completed ? "NOW()" : "NULL") + " "
                        + "WHERE id='" + taskId + "'");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(homeScreen, "Failed to update task status. Please try again.", "Status Update Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // Jump to the new status' tab and reload so end time / updated at are fresh.
            SwingUtilities.invokeLater(() -> {
                statusTabs.setSelectedIndex(java.util.Arrays.asList(STATUSES).indexOf(newStatus));
                loadTasks();
            });
            return true;
        }));
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
                false, false, false, false, false, false, false, false, true, true
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
