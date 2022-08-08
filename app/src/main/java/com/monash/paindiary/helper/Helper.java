package com.monash.paindiary.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isPasswordValid(String password) {
        //complete pattern that should force the user to use digits, lower case, upper case and special characters.
        // ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,20}$
        String expression = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
