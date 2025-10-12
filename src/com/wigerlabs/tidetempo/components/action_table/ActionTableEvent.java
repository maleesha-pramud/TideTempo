/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wigerlabs.tidetempo.components.action_table;

/**
 *
 * @author malee
 */
public interface ActionTableEvent {

    public void onEdit(int projectId);

    public void onView(int projectId);

    public void onDelete(int projectId);
    
    public void onStatusChange(int projectId);
}
