package com.monash.paindiary.helper;

public class UserInfo {
    private static UserInfo INSTANCE;
    private String userEmail;
    private String userFullName;
    private int todayEntryUID = -1;

    public static void setINSTANCE(String userEmail, boolean forceOverride) {
        if (INSTANCE == null || forceOverride)
            INSTANCE = new UserInfo(userEmail);
    }

    public static UserInfo getInstance() {
        return INSTANCE;
    }

    private UserInfo(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        if (INSTANCE != null)
            return INSTANCE.userFullName;
        return "";
    }

    public boolean setUserFullName(String userFullName) {
        if (INSTANCE != null) {
            INSTANCE.userFullName = userFullName;
            return true;
        }
        return false;
    }

    public int getTodayEntryUID() {
        if (INSTANCE != null)
            return INSTANCE.todayEntryUID;
        return -1;
    }

    public boolean setTodayEntryUID(int todayEntryUID) {
        if (INSTANCE != null) {
            INSTANCE.todayEntryUID = todayEntryUID;
            return true;
        }
        return false;
    }

    public static String getUserEmail() {
        if (INSTANCE == null)
            return "";
        else
            return INSTANCE.userEmail;
    }
}
