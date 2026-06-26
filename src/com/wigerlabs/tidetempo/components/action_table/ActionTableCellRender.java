package com.wigerlabs.tidetempo.components.action_table;

import com.wigerlabs.tidetempo.panel.ActionPanel;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

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

        ActionPanel action = new ActionPanel(projectId);
        action.setBackground(com.getBackground());
        return action;
    }
}
