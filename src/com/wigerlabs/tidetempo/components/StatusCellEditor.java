package com.wigerlabs.tidetempo.components;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * Combo box cell editor for a "Status" column. The change is only saved when the
 * user actually picks a different status, and it is applied to the row that was
 * being edited (not whatever row happens to be selected). If the handler reports
 * a failure the edit is cancelled, so the table keeps showing the old status.
 */
public class StatusCellEditor extends DefaultCellEditor {

    public interface StatusChangeHandler {

        /**
         * @param id value of the row's first column (record id)
         * @param newStatus the status name picked by the user
         * @return true if the change was saved
         */
        boolean onStatusChange(String id, String newStatus);
    }

    private final StatusChangeHandler handler;
    private String originalStatus;
    private String rowId;

    public StatusCellEditor(String[] statuses, StatusChangeHandler handler) {
        super(new JComboBox<>(statuses));
        this.handler = handler;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        originalStatus = String.valueOf(value);
        rowId = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(row), 0));
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean stopCellEditing() {
        String newStatus = String.valueOf(getCellEditorValue());
        if (!newStatus.equals(originalStatus) && !handler.onStatusChange(rowId, newStatus)) {
            cancelCellEditing();
            return true;
        }
        return super.stopCellEditing();
    }
}
