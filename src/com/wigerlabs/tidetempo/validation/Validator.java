package com.wigerlabs.tidetempo.validation;

import com.wigerlabs.tidetempo.dialog.WarningDialog;
import javax.swing.JOptionPane;

public class Validator {

    private static boolean warn(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
        return false;
    }

    public static boolean isEmailValid(String value) {
        if (value == null || value.isBlank()) {
            return warn("Email Validation", "Email can't be empty!");
        } else if (value.length() > 100 || !value.matches(Validation.EMAIL.validate())) {
            return warn("Email Validation", "Please enter a valid email address (e.g. name@example.com).");
        }
        return true;
    }

    /** Sri Lankan mobile number only, e.g. 0771234567. */
    public static boolean isMobileValid(String value) {
        if (value == null || value.isBlank()) {
            return warn("Mobile Validation", "Mobile number can't be empty!");
        } else if (!value.matches(Validation.MOBILE.validate())) {
            return warn("Mobile Validation", "Please enter a valid Sri Lankan mobile number: 10 digits starting with 0 (e.g. 0771234567).");
        }
        return true;
    }

    /** Sri Lankan mobile (0771234567) or landline (0112345678) number. */
    public static boolean isPhoneValid(String value) {
        if (value == null || value.isBlank()) {
            return warn("Phone Validation", "Phone number can't be empty!");
        } else if (!value.matches(Validation.MOBILE.validate()) && !value.matches(Validation.LANDLINE.validate())) {
            return warn("Phone Validation", "Please enter a valid Sri Lankan phone number: 10 digits starting with 0 (e.g. 0771234567 or 0112345678).");
        }
        return true;
    }

    public static boolean isNameValid(String fieldName, String value) {
        if (!isInputFieldValid(fieldName, value)) {
            return false;
        }
        String v = value.trim();
        if (v.length() < 2 || v.length() > 50) {
            return warn(fieldName + " Validation", fieldName + " must be between 2 and 50 characters.");
        } else if (!v.matches(Validation.NAME.validate())) {
            return warn(fieldName + " Validation", fieldName + " can only contain letters, spaces, and . ' - characters.");
        }
        return true;
    }

    public static boolean isLengthValid(String fieldName, String value, int max) {
        if (value != null && value.trim().length() > max) {
            return warn(fieldName + " Validation", fieldName + " can't be longer than " + max + " characters.");
        }
        return true;
    }

    /** Parses a (possibly comma-formatted) number; shows a warning and returns null if invalid or out of range. */
    public static Double parseNumber(String fieldName, String raw, double min, double max) {
        String v = raw == null ? "" : raw.replace(",", "").trim();
        if (v.isEmpty()) {
            warn(fieldName + " Validation", fieldName + " can't be empty!");
            return null;
        }
        double n;
        try {
            n = Double.parseDouble(v);
        } catch (NumberFormatException e) {
            warn(fieldName + " Validation", fieldName + " must be a valid number.");
            return null;
        }
        if (Double.isNaN(n) || Double.isInfinite(n) || n < min || n > max) {
            warn(fieldName + " Validation", fieldName + " must be between " + min + " and " + max + ".");
            return null;
        }
        return n;
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
        if (inputValue == null || inputValue.isBlank()) {
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
