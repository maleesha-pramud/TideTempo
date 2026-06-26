package com.wigerlabs.tidetempo.components.action_table;

import com.wigerlabs.tidetempo.panel.ActionPanel;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ActionTableCellEditor extends DefaultCellEditor {

    private ActionTableEvent event;
    private final int actionColumnIndex;

    public ActionTableCellEditor(ActionTableEvent event, int actionColumnIndex) {
        super(new JCheckBox());
        this.event = event;
        this.actionColumnIndex = actionColumnIndex;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        TableModel model = table.getModel();

        String projectIdStr = (String) model.getValueAt(row, 0);
        int projectId = projectIdStr != null ? Integer.parseInt(projectIdStr) : -1;
        
        ActionPanel action = new ActionPanel(projectId);

        action.initEvent(event);
        action.setBackground(table.getSelectionBackground());
        return action;
    }
}
