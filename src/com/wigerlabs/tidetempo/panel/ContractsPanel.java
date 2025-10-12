/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.wigerlabs.tidetempo.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.wigerlabs.tidetempo.components.action_table.ActionTableCellEditor;
import com.wigerlabs.tidetempo.components.action_table.ActionTableCellRender;
import com.wigerlabs.tidetempo.components.action_table.ActionTableEvent;
import com.wigerlabs.tidetempo.components.contracts.BasicInfoPanel;
import com.wigerlabs.tidetempo.components.contracts.FinancialTermsPanel;
import com.wigerlabs.tidetempo.components.contracts.ProjectDetailsPanel;
import com.wigerlabs.tidetempo.components.contracts.TermsAndConditionsPanel;
import com.wigerlabs.tidetempo.connection.MySQL;
import com.wigerlabs.tidetempo.dialog.ConfirmationDialog;
import com.wigerlabs.tidetempo.dialog.ContractViewDialog;
import com.wigerlabs.tidetempo.dialog.SuccessDialog;
import com.wigerlabs.tidetempo.gui.HomeScreen;
import com.wigerlabs.tidetempo.util.Colors;
import com.wigerlabs.tidetempo.util.ComboItem;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.Border;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import com.wigerlabs.tidetempo.util.Client;
import com.wigerlabs.tidetempo.util.CurrentTimeGenerator;
import com.wigerlabs.tidetempo.util.User;
import com.wigerlabs.tidetempo.util.SessionManager;
import com.wigerlabs.tidetempo.validation.Validator;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author malee
 */
public class ContractsPanel extends javax.swing.JPanel {

    private HomeScreen homeScreen;
    public CardLayout contentPanelLayout;
    private BasicInfoPanel basicInfoPanel;
    private ProjectDetailsPanel projectDetailsPanel;
    private FinancialTermsPanel financialTermsPanel;
    private TermsAndConditionsPanel termsAndConditionsPanel;
    private final User userData;
    private boolean isEditing;

    /**
     * Creates new form ProjectsPanel
     *
     * @param parent
     */
    public ContractsPanel(HomeScreen parent) {
        initComponents();
        this.homeScreen = parent;
        init();
        loadContracts();
        setupActionColumn();
        userData = SessionManager.getUserSession();
    }

    private void init() {
        JTableHeader header = contractsTable.getTableHeader();
        header.setBackground(Colors.LIGHT_BLUE);
        header.setPreferredSize(new Dimension(0, 50));
        Border border = BorderFactory.createLineBorder(Colors.LIGHT_BLUE);
        tableScrollPane.setBorder(border);

        Icon fileIcon = new FlatSVGIcon("com/wigerlabs/tidetempo/img/file_icon.svg", 25, 25);

        contractValueIcon.setIcon(fileIcon);
        contractValuePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        pendingSignaturesIcon.setIcon(fileIcon);
        pendingSignaturesPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        signedContractsIcon.setIcon(fileIcon);
        signedContractsPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        if (contentPanelLayout == null && contentPanel.getLayout() instanceof CardLayout) {
            this.contentPanelLayout = (CardLayout) contentPanel.getLayout();
        }
        this.basicInfoPanel = new BasicInfoPanel(this);
        this.projectDetailsPanel = new ProjectDetailsPanel(this);
        this.financialTermsPanel = new FinancialTermsPanel(this);
        this.termsAndConditionsPanel = new TermsAndConditionsPanel(this);
        contentPanel.add(basicInfoPanel, "basicinfo_panel");
        contentPanel.add(projectDetailsPanel, "project_details_panel");
        contentPanel.add(financialTermsPanel, "financial_terms_panel");
        contentPanel.add(termsAndConditionsPanel, "terms_and_contions_panel");
        SwingUtilities.updateComponentTreeUI(contentPanel);
    }

    public synchronized void loadContracts() {
        int totalContractsValue = 0;
        int signedContracts = 0;
        int pendingSignature = 0;

        if (contractsTable.isEditing()) {
            contractsTable.getCellEditor().stopCellEditing();
        }

        DefaultTableModel dtm = (DefaultTableModel) contractsTable.getModel();
        dtm.setRowCount(0);
        try {
            ResultSet rs = MySQL.execute("SELECT c.id, c.date, pt.name AS project_template, cl.name AS client, p.title AS project_title, pa.name AS payment_schedule, "
                    + "c.total_amount, c.hourly_rate, c.estimated_hours, c.number_of_revisions, c.cancellation_policy, c.intellectual_policy, s.name AS status , c.created_at, c.updated_at "
                    + "FROM contract c "
                    + "INNER JOIN project_template pt ON c.project_template_id = pt.id "
                    + "INNER JOIN client cl ON c.client_id = cl.id "
                    + "INNER JOIN project p ON c.project_id = p.id "
                    + "INNER JOIN payment_schedule pa ON c.payment_schedule_id = pa.id "
                    + "INNER JOIN status s ON c.status_id = s.id "
                    + "ORDER BY p.id ASC");

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("date"));
                row.add(rs.getString("project_template"));
                row.add(rs.getString("client"));
                row.add(rs.getString("project_title"));
                row.add(rs.getString("total_amount"));
                String status = rs.getString("status");
                row.add(status);
                dtm.addRow(row);
                totalContractsValue += rs.getInt("total_amount");
                if (status.equalsIgnoreCase("signed")) {
                    signedContracts++;
                } else {
                    pendingSignature++;
                }
            }
            SwingUtilities.updateComponentTreeUI(contractsTable);
            contractsTable.revalidate();
            contractsTable.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalContractValueLabel.setText("LKR " + NumberFormat.getNumberInstance(Locale.US).format(totalContractsValue));
        signedContractsLabel.setText(String.valueOf(signedContracts));
        pendingSignaturesLabel.setText(String.valueOf(pendingSignature));
    }

    private void setupActionColumn() {
        ActionTableEvent event = new ActionTableEvent() {
            @Override
            public void onEdit(int contractId) {
//                ProjectAddEditDialog projectEditDialog = new ProjectAddEditDialog(homeScreen, true, true, contractId, ContractsPanel.this);
//                projectEditDialog.setVisible(true);
            }

            @Override
            public void onView(int contractId) {
                ContractViewDialog contractViewDialog = new ContractViewDialog(homeScreen, true, contractId);
                contractViewDialog.setVisible(true);
            }

            @Override
            public void onDelete(int contractId) {
                ConfirmationDialog.show("Are you sure you want to delete this project?");
                int confirmationAnswer = ConfirmationDialog.getResult();
                if (confirmationAnswer == 0) {
                    try {
                        MySQL.execute("DELETE FROM contract WHERE id='" + contractId + "'");
                        SwingUtilities.invokeLater(() -> {
                            loadContracts();
                            SuccessDialog.show("Contract deleted successfully!");
                        });
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                homeScreen,
                                "Contract Deleting Failed! Please try again later.",
                                "Contract Deleteing Failed!",
                                JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStatusChange(int contractId) {
                ConfirmationDialog.show("Are you sure you want to change the status?");
                int confirmationAnswer = ConfirmationDialog.getResult();
                if (confirmationAnswer == 0) {
                    try {
                        ResultSet rs = MySQL.execute("SELECT status_id FROM contract WHERE id='" + contractId + "'");
                        if (rs.next()) {
                            int statusId = rs.getInt("status_id") == 3 ? 4 : 3;
                            MySQL.execute("UPDATE contract SET status_id='" + statusId + "' WHERE id='" + contractId + "'");
                            SwingUtilities.invokeLater(() -> {
                                loadContracts();
                                SuccessDialog.show("Contract status have changed successfully!");
                            });
                        } else {
                            JOptionPane.showMessageDialog(
                                    homeScreen,
                                    "Status change failed. Please try again later.",
                                    "Failed to load contract status!",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                homeScreen,
                                "Contract Status Updating Failed! Please try again later.",
                                "Contract Status Updating Failed!",
                                JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                }
            }
        };
        contractsTable.getColumnModel().getColumn(7).setCellRenderer(new ActionTableCellRender(6));
        contractsTable.getColumnModel().getColumn(7).setCellEditor(new ActionTableCellEditor(event, 6));
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void generateAgreement() {
        Client client = (Client) basicInfoPanel.getClientCombo().getSelectedItem();
        String contractDate = basicInfoPanel.getContractDatePicker().getDateStringOrEmptyString();
        ComboItem projectTemplate = (ComboItem) basicInfoPanel.getProjectTemplate().getSelectedItem();
        String projectName = projectDetailsPanel.getProjectName().getText();
        String projectDescription = projectDetailsPanel.getProjectDescription().getText();
        double totalProjectAmount = Double.parseDouble(financialTermsPanel.getTotalProjectAmountField().getText().replace(",", ""));
        double hourlyRate = Double.parseDouble(financialTermsPanel.getHourlyRateField().getText().replace(",", ""));
        double estimatedHours = Double.parseDouble(financialTermsPanel.getEstimatedHoursField().getText().replace(",", ""));
        ComboItem paymentSchedule = (ComboItem) financialTermsPanel.getPaymentScheduleCombo().getSelectedItem();
        double numberOfRevisions = Double.parseDouble(termsAndConditionsPanel.getNumberOfRevisionsField().getText());
        String cancellationPolicy = termsAndConditionsPanel.getCancellationPolicyField().getText();
        String intellectualPropertyRights = termsAndConditionsPanel.getIntellectualPropertyRightsField().getText();

        Timestamp nowTime = CurrentTimeGenerator.getInstance().getCurrentTime();

        int contractId = addEditProject(projectName, projectDescription, client, nowTime);

        if (contractId != 0) {
            try {
                if (!isEditing) {
                    MySQL.execute("INSERT INTO `contract` (date, project_template_id, client_id, "
                            + "project_id, payment_schedule_id, total_amount, hourly_rate, estimated_hours, number_of_revisions, "
                            + "cancellation_policy, intellectual_policy, created_at) VALUES "
                            + "('" + contractDate + "','" + projectTemplate.getId() + "','" + client.getId() + "','"
                            + contractId + "','" + paymentSchedule.getId() + "','" + totalProjectAmount + "','" + hourlyRate + "','"
                            + estimatedHours + "','" + numberOfRevisions + "','" + cancellationPolicy + "','" + intellectualPropertyRights + "','" + nowTime + "')");
                    System.out.println("Contract added successfully!");
                } else {
                    MySQL.execute("UPDATE `contract` "
                            + "SET `date` = '" + contractDate + "', "
                            + "`project_template_id` = '" + projectTemplate.getId() + "', `client_id` = '" + userData.id + "', "
                            + "`payment_schedule_id` = '" + paymentSchedule.getId() + "', `total_amount` = '" + totalProjectAmount + "', "
                            + "`hourly_rate` = '" + hourlyRate + "', `estimated_hours` = '" + estimatedHours + "', "
                            + "`number_of_revisions` = '" + numberOfRevisions + "', `cancellation_policy` = '" + cancellationPolicy + "', "
                            + "`intellectual_policy` = '" + intellectualPropertyRights + "', `updated_at` = '" + nowTime + "' "
                            + "WHERE `project_id` = '" + contractId + "'");
                    System.out.println("Contract updated successfully!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Please try again to add the same project.", "Project adding failed!", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            try {
                InputStream filePath = getClass().getClassLoader().getResourceAsStream("com/wigerlabs/tidetempo/report/SoftwareAgreement.jrxml");
                HashMap<String, Object> parameters = new HashMap<>();

                // Filling the HashMap with your variables
                parameters.put("clientName", client.getName());
                parameters.put("contractDate", contractDate);
                parameters.put("projectTemplate", projectTemplate.getName());
                parameters.put("projectName", projectName);
                parameters.put("projectDescription", projectDescription);
                parameters.put("totalProjectAmount", totalProjectAmount);
                parameters.put("hourlyRate", hourlyRate);
                parameters.put("estimatedHours", estimatedHours);
                parameters.put("paymentSchedule", paymentSchedule.getName());
                parameters.put("numberOfRevisions", numberOfRevisions);
                parameters.put("termination", cancellationPolicy);
                parameters.put("intellectualProperty", intellectualPropertyRights);
                parameters.put("clientEmail", client.getEmail());
                parameters.put("userName", userData.name);
                parameters.put("userEmail", userData.email);

                JasperReport compileReport = JasperCompileManager.compileReport(filePath);

                JasperPrint fillReport = JasperFillManager.fillReport(compileReport, parameters, new JREmptyDataSource());
                JasperViewer.viewReport(fillReport);
            } catch (JRException e) {
                e.printStackTrace();
            }
        }
    }

    private int addEditProject(String projectTitle, String projectDescription, Client client, Timestamp nowTime) {
        int contractId = 0;

        if (!Validator.isInputFieldValid("Project Name", projectTitle)) {
            return 0;
        } else if (!Validator.isInputFieldValid("Project Description", projectDescription)) {
            return 0;
        } else if (!Validator.isComboBoxValid("Client", client.getId())) {
            return 0;
        }

        try {
            if (!isEditing) {
                MySQL.execute("INSERT INTO `project` (title, description, priority_id, user_id, client_id, status_id, created_at) VALUES "
                        + "('" + projectTitle + "','" + projectDescription + "','" + 1 + "','"
                        + userData.id + "','" + client.getId() + "','" + 4 + "','" + nowTime + "')");
                System.out.println("Project added successfully!");
            } else {
                MySQL.execute("UPDATE `project` "
                        + " SET title='" + projectTitle + "', description='" + projectDescription
                        + "', client_id='" + client.getId() + "', updated_at='" + nowTime + "' WHERE id='" + contractId + "'");
                System.out.println("Project updated successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Please try again to add the same project.", "Project adding failed!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        try {
            ResultSet rs = MySQL.execute("SELECT id from `project` WHERE title='" + projectTitle + "'");
            if (rs.next()) {
                contractId = rs.getInt("id");
                System.out.println("Project id gotted: " + contractId);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Please try again to get project ID.", "Project ID getting failed!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return contractId;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        contentPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        contractsTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        contractValuePanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        totalContractValueLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        contractValueIcon = new javax.swing.JLabel();
        signedContractsPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        signedContractsLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        signedContractsIcon = new javax.swing.JLabel();
        pendingSignaturesPanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        pendingSignaturesLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        pendingSignaturesIcon = new javax.swing.JLabel();

        setBackground(new java.awt.Color(2, 8, 23));
        setMinimumSize(new java.awt.Dimension(708, 588));

        jPanel1.setOpaque(false);

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Generate Contract", jPanel1);

        jPanel2.setOpaque(false);

        contractsTable.setAutoCreateRowSorter(true);
        contractsTable.setBackground(new java.awt.Color(2, 8, 23));
        contractsTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        contractsTable.setForeground(new java.awt.Color(255, 255, 255));
        contractsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Date", "Project Template", "Client", "Project", "Total Amount", "Status", "Actions"
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
        contractsTable.setGridColor(new java.awt.Color(28, 39, 56));
        contractsTable.setRowHeight(40);
        contractsTable.setShowGrid(false);
        contractsTable.setShowHorizontalLines(true);
        tableScrollPane.setViewportView(contractsTable);
        if (contractsTable.getColumnModel().getColumnCount() > 0) {
            contractsTable.getColumnModel().getColumn(0).setMaxWidth(50);
            contractsTable.getColumnModel().getColumn(7).setMinWidth(130);
        }

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridLayout(1, 3, 15, 0));

        contractValuePanel.setBackground(new java.awt.Color(59, 93, 246));

        jPanel7.setOpaque(false);

        totalContractValueLabel.setFont(new java.awt.Font("Segoe UI Black", 0, 28)); // NOI18N
        totalContractValueLabel.setText("LKR 0");

        jPanel4.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Total Contract Value");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(contractValueIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(contractValueIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(totalContractValueLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(totalContractValueLabel)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout contractValuePanelLayout = new javax.swing.GroupLayout(contractValuePanel);
        contractValuePanel.setLayout(contractValuePanelLayout);
        contractValuePanelLayout.setHorizontalGroup(
            contractValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contractValuePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        contractValuePanelLayout.setVerticalGroup(
            contractValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contractValuePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        jPanel3.add(contractValuePanel);

        signedContractsPanel.setBackground(new java.awt.Color(0, 204, 0));

        jPanel8.setOpaque(false);

        signedContractsLabel.setFont(new java.awt.Font("Segoe UI Black", 0, 28)); // NOI18N
        signedContractsLabel.setForeground(new java.awt.Color(255, 255, 255));
        signedContractsLabel.setText("0");

        jPanel5.setOpaque(false);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Signed Contracts");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(signedContractsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(signedContractsIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(signedContractsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(signedContractsLabel)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout signedContractsPanelLayout = new javax.swing.GroupLayout(signedContractsPanel);
        signedContractsPanel.setLayout(signedContractsPanelLayout);
        signedContractsPanelLayout.setHorizontalGroup(
            signedContractsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signedContractsPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        signedContractsPanelLayout.setVerticalGroup(
            signedContractsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signedContractsPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        jPanel3.add(signedContractsPanel);

        pendingSignaturesPanel.setBackground(new java.awt.Color(255, 153, 0));

        jPanel9.setOpaque(false);

        pendingSignaturesLabel.setFont(new java.awt.Font("Segoe UI Black", 0, 28)); // NOI18N
        pendingSignaturesLabel.setForeground(new java.awt.Color(255, 255, 255));
        pendingSignaturesLabel.setText("0");

        jPanel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.setOpaque(false);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Pending Signatures");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(pendingSignaturesIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pendingSignaturesIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pendingSignaturesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pendingSignaturesLabel)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout pendingSignaturesPanelLayout = new javax.swing.GroupLayout(pendingSignaturesPanel);
        pendingSignaturesPanel.setLayout(pendingSignaturesPanelLayout);
        pendingSignaturesPanelLayout.setHorizontalGroup(
            pendingSignaturesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pendingSignaturesPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        pendingSignaturesPanelLayout.setVerticalGroup(
            pendingSignaturesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pendingSignaturesPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        jPanel3.add(pendingSignaturesPanel);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableScrollPane)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Contract History", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jTabbedPane1)
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jTabbedPane1)
                .addGap(25, 25, 25))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel contractValueIcon;
    private javax.swing.JPanel contractValuePanel;
    private javax.swing.JTable contractsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel pendingSignaturesIcon;
    private javax.swing.JLabel pendingSignaturesLabel;
    private javax.swing.JPanel pendingSignaturesPanel;
    private javax.swing.JLabel signedContractsIcon;
    private javax.swing.JLabel signedContractsLabel;
    private javax.swing.JPanel signedContractsPanel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JLabel totalContractValueLabel;
    // End of variables declaration//GEN-END:variables
}
