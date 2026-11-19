package com.wigerlabs.tidetempo.validation;

public enum Validation {
    EMAIL() {
        @Override
        public String validate() {
            return "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$";
        }
    },
    // Sri Lankan mobile: 0 + operator code (70,71,72,74,75,76,77,78) + 7 digits, e.g. 0771234567
    MOBILE() {
        @Override
        public String validate() {
            return "^07[01245678][0-9]{7}$";
        }
    },
    // Sri Lankan landline: 0 + 2-digit area code (not 07x) + 7 digits, e.g. 0112345678
    LANDLINE() {
        @Override
        public String validate() {
            return "^0(?!7)[1-9][0-9]{8}$";
        }
    },
    NAME() {
        @Override
        public String validate() {
            return "^\\p{L}+(?:[ .'-]\\p{L}+)*\\.?$";
        }
    },
    PASSWORD() {
        @Override
        public String validate() {
            return "^.*(?=.{4,8})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";
        }
    };
    public String validate(){
        return "";
    }
}
