/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wigerlabs.tidetempo.components.action_table;

import com.wigerlabs.tidetempo.panel.ActionPanel;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author malee
 */
public class ActionTableCellRender extends DefaultTableCellRenderer {

    private final int actionColumnIndex;

    public ActionTableCellRender(int actionColumnIndex) {
        this.actionColumnIndex = actionColumnIndex;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TableModel model = table.getModel();
        int projectId = Integer.parseInt((String) model.getValueAt(row, 0));
        String statusValue = (String) model.getValueAt(row, actionColumnIndex);
        int statusId = statusValue.equalsIgnoreCase("Active") ? 1 : statusValue.equalsIgnoreCase("Inactive") ? 2 : statusValue.equalsIgnoreCase("Signed") ? 3 : 4;

        ActionPanel action = new ActionPanel(projectId, statusId);
        action.setBackground(com.getBackground());
        return action;
    }

}
