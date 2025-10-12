/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.wigerlabs.tidetempo.panel;

import com.wigerlabs.tidetempo.components.action_table.ActionTableCellEditor;
import com.wigerlabs.tidetempo.components.action_table.ActionTableCellRender;
import com.wigerlabs.tidetempo.components.action_table.ActionTableEvent;
import com.wigerlabs.tidetempo.connection.MySQL;
import com.wigerlabs.tidetempo.dialog.ClientAddEditDialog;
import com.wigerlabs.tidetempo.dialog.ClientViewDialog;
import com.wigerlabs.tidetempo.dialog.ConfirmationDialog;
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
public class ClientsPanel extends javax.swing.JPanel {

    private HomeScreen homeScreen;

    /**
     * Creates new form ClientsPanel
     *
     * @param parent
     */
    public ClientsPanel(HomeScreen parent) {
        initComponents();
        this.homeScreen = parent;
        init();
        loadClients();
        setupActionColumn();
    }
    
    private void init() {
        JTableHeader header = clientsTable.getTableHeader();
        header.setBackground(Colors.LIGHT_BLUE);
        header.setPreferredSize(new Dimension(0, 50));
        Border border = BorderFactory.createLineBorder(Colors.LIGHT_BLUE);
        tableScrollPane.setBorder(border);
    }

    public synchronized void loadClients() {

        if (clientsTable.isEditing()) {
            clientsTable.getCellEditor().stopCellEditing();
        }

        DefaultTableModel dtm = (DefaultTableModel) clientsTable.getModel();
        dtm.setRowCount(0);
        try {
            ResultSet rs = MySQL.execute("SELECT c.id, c.name, c.company, c.email, c.phone, c.created_at, c.updated_at "
                    + "FROM client c "
                    + "ORDER BY c.id ASC");

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("company"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("created_at"));
                row.add(rs.getString("updated_at"));
                dtm.addRow(row);
            }
            SwingUtilities.updateComponentTreeUI(clientsTable);
            clientsTable.revalidate();
            clientsTable.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupActionColumn() {
        ActionTableEvent event = new ActionTableEvent() {
            @Override
            public void onEdit(int clientId) {
                ClientAddEditDialog clientAddDialog = new ClientAddEditDialog(homeScreen, true, true, clientId, ClientsPanel.this);
                clientAddDialog.setVisible(true);
            }

            @Override
            public void onView(int clientId) {
                ClientViewDialog clientViewDialog = new ClientViewDialog(homeScreen, true, clientId);
                clientViewDialog.setVisible(true);
            }

            @Override
            public void onDelete(int clientId) {
                ConfirmationDialog.show("Are you sure you want to delete this client?");
                int confirmationAnswer = ConfirmationDialog.getResult();
                if (confirmationAnswer == 0) {
//                    try {
//                        MySQL.execute("SELECT COUNT(*) FROM`contract` ")
//                    }catch (SQLException e) {
//                        e.printStackTrace();
//                    }
                    try {
                        MySQL.execute("DELETE FROM client WHERE id='" + clientId + "'");
                        SwingUtilities.invokeLater(() -> {
                            loadClients();
                            SuccessDialog.show("Client deleted successfully!");
                        });
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                homeScreen,
                                "Client Deleting Failed! Please try again later.",
                                "Client Deleteing Failed!",
                                JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStatusChange(int clientId) {
//                ConfirmationDialog.show("Are you sure you want to change the status?");
//                int confirmationAnswer = ConfirmationDialog.getResult();
//                if (confirmationAnswer == 0) {
//                    try {
//                        ResultSet rs = MySQL.execute("SELECT status_id FROM project WHERE id='" + clientId + "'");
//                        if (rs.next()) {
//                            int statusId = rs.getInt("status_id") == 1 ? 2 : 1;
//                            MySQL.execute("UPDATE project SET status_id='" + statusId + "' WHERE id='" + clientId + "'");
//                            SwingUtilities.invokeLater(() -> {
//                                loadClients();
//                                SuccessDialog.show("Project status have changed successfully!");
//                            });
//                        } else {
//                            JOptionPane.showMessageDialog(
//                                    homeScreen,
//                                    "Status change failed. Please try again later.",
//                                    "Failed to load project status!",
//                                    JOptionPane.ERROR_MESSAGE
//                            );
//                        }
//                    } catch (SQLException e) {
//                        JOptionPane.showMessageDialog(
//                                homeScreen,
//                                "Project Status Updating Failed! Please try again later.",
//                                "Project Status Updating Failed!",
//                                JOptionPane.ERROR_MESSAGE
//                        );
//                        e.printStackTrace();
//                    }
//                }
            }
        };
        clientsTable.getColumnModel().getColumn(7).setCellRenderer(new ActionTableCellRender(6));
        clientsTable.getColumnModel().getColumn(7).setCellEditor(new ActionTableCellEditor(event, 6));
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
        clientsTable = new javax.swing.JTable();
        addClientBtn = new javax.swing.JButton();

        setBackground(new java.awt.Color(2, 8, 23));
        setMinimumSize(new java.awt.Dimension(708, 588));

        clientsTable.setAutoCreateRowSorter(true);
        clientsTable.setBackground(new java.awt.Color(2, 8, 23));
        clientsTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        clientsTable.setForeground(new java.awt.Color(255, 255, 255));
        clientsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Name", "Company", "Email", "Phone", "Added at", "Updated at", "Actions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        clientsTable.setGridColor(new java.awt.Color(28, 39, 56));
        clientsTable.setRowHeight(40);
        clientsTable.setShowGrid(false);
        clientsTable.setShowHorizontalLines(true);
        tableScrollPane.setViewportView(clientsTable);
        if (clientsTable.getColumnModel().getColumnCount() > 0) {
            clientsTable.getColumnModel().getColumn(0).setMaxWidth(50);
            clientsTable.getColumnModel().getColumn(7).setMinWidth(180);
            clientsTable.getColumnModel().getColumn(7).setMaxWidth(180);
        }

        addClientBtn.setBackground(new java.awt.Color(59, 130, 246));
        addClientBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addClientBtn.setText("New Client");
        addClientBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addClientBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addClientBtnActionPerformed(evt);
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
                        .addComponent(addClientBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(addClientBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addClientBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addClientBtnActionPerformed
        ClientAddEditDialog clientAddDialog = new ClientAddEditDialog(homeScreen, true, this);
        clientAddDialog.setVisible(true);
    }//GEN-LAST:event_addClientBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addClientBtn;
    private javax.swing.JTable clientsTable;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables
}
