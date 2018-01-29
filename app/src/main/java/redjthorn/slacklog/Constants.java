package redjthorn.slacklog;

import android.provider.BaseColumns;

/**
 * Created by jaricthorning on 29/1/18.
 */
public interface Constants extends BaseColumns{
    public static final String WORKSPACES_TABLE_NAME = "workspaces";
    public static final String WORKSPACES_NAME = "name";
    public static final String WORKSPACES_KEY = "key";


    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_UID = "uid";
    public static final String USERS_NAME = "name";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_WORKSPACE = "workspace";




}
