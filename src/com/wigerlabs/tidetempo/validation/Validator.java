package com.wigerlabs.tidetempo.validation;

import com.wigerlabs.tidetempo.dialog.WarningDialog;
import javax.swing.JOptionPane;

public class Validator {

    public static boolean isEmailValid(String value) {
        if (value.isBlank()) {
            JOptionPane.showMessageDialog(null, "Email can't be empty!", "Email Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!value.matches(Validation.EMAIL.validate())) {
            JOptionPane.showMessageDialog(null, "Please enter valide email address!", "Email Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isMobileValid(String value) {
        if (value.isBlank()) {
            JOptionPane.showMessageDialog(null, "Mobile can't be empty!", "Mobile Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!value.matches(Validation.MOBILE.validate())) {
            JOptionPane.showMessageDialog(null, "Please enter valide mobile number!", "Mobile Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPasswordValid(String value) {
        if (value.isBlank()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Password can't be empty!",
                    "Password Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        } else if (!value.matches(Validation.PASSWORD.validate())) {
            JOptionPane.showMessageDialog(null, """
                    Password must be included the following characters. 
                    At least one lowercase, 
                    At lease one uppercase, 
                    a special character, 
                    and at lease one digit 
                    The password must be greater than 4 and less than 8 characters""",
                    "Password Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        } else {
            return true;
        }
    }

    public static boolean isConfirmPassword(String newPassword, String confirmPassword) {
        if (newPassword.isBlank() && confirmPassword.isBlank()) {
            return true;
        } else {
            if (newPassword.isBlank() || confirmPassword.isBlank()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Passwords cannot be empty",
                        "Passwords cannot be empty. Please type same password in both fields. ",
                        JOptionPane.WARNING_MESSAGE
                );
                return false;
            } else if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null,
                        "Passwords doesn't match!",
                        "Passwords doesn't match. Please check spellings again. ",
                        JOptionPane.WARNING_MESSAGE
                );
                return false;
            } else if (!Validator.isPasswordValid(newPassword)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean isInputFieldValid(String inputFieldName, String inputValue) {
        if (inputValue.isBlank()) {
            WarningDialog.show("<html><p style='width: 100%; height: 100%;'>" + inputFieldName + " can not be empty! </p></html>");
            return false;
        }
        return true;
    }

    public static boolean isComboBoxValid(String inputField, int inputValue) {
        if (inputValue == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a valid option for " + inputField,
                    "ComboBox Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }
}
