package redjthorn.slacklog;

import android.provider.BaseColumns;

/**
 * Created by jaricthorning on 29/1/18.
 */
public interface Constants extends BaseColumns{

    public  static final String DATABASE_NAME = "workspaces.db";


    public static final String WORKSPACES_TABLE_NAME = "workspaces";
    public static final String WORKSPACES_NAME = "name";
    public static final String WORKSPACES_KEY = "key";


    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_UID = "uid";
    public static final String USERS_NAME = "name";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_WORKSPACE = "workspace";
    public static final String USERS_REAL_NAME = "realname";

    public static final String LOG_TABLE_NAME = "logs";
    public static final String LOG_ID = "id";
    public static final String LOG_UID = "uid";
    public static final String LOG_USERNAME = "username";
    public static final String LOG_DATE_FIRST = "datefirst";
    public static final String LOG_DATE_LAST = "datelast";
    public static final String LOG_COUNT = "count";
    public static final String LOG_IP = "ip";
    public static final String LOG_UAGENT = "useragent";
    public static final String LOG_ISP = "isp";
    public static final String LOG_COUNTRY = "country";
    public static final String LOG_REGION = "region";





}
