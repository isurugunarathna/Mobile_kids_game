package com.example.mad;
import android.provider.BaseColumns;

public final class UserMaster {
    public UserMaster(){}

    public static class Users implements BaseColumns{
        public static final String TABLE_NAME_SCORE = "Game_Score";
       // public static final String TABLE_NAME_LOGIN = "Game_Login";
       // public static final String COLUMN_NAME_ID1 = "ID";
        public static final String COLUMN_NAME_ID2 = "ID";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_SCORE = "score";

    }
}
