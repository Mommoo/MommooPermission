package com.mommoo.permission.utils.observer;

import android.content.Context;
import android.support.annotation.Nullable;

import com.mommoo.library.toolkit.MommooSharedPreference;

/**
 * Created by mommoo on 2017-06-07.
 */

public class PermissionEventChecker {
    private static final String PERMISSION_EVENT_CHECKER_SHARED_PREFERENCE_NAME = PermissionEventChecker.class.getSimpleName();
    private static final String COMPLETE_PERMISSION_CHECK_KEY = "isCompletePermissionCheck";
    private final MommooSharedPreference permissionEventChecker;


    private static PermissionEventChecker instance;

    private PermissionEventChecker(Context context){
        this.permissionEventChecker = new MommooSharedPreference(context,PERMISSION_EVENT_CHECKER_SHARED_PREFERENCE_NAME);
        if (!this.permissionEventChecker.isContainKey(PERMISSION_EVENT_CHECKER_SHARED_PREFERENCE_NAME)){
            permissionEventChecker.putBoolean(COMPLETE_PERMISSION_CHECK_KEY, true);
            permissionEventChecker.commit();
        }
    }

    public static PermissionEventChecker getChecker(Context context){
        if(instance == null) instance = new PermissionEventChecker(context);
        return instance;
    }

    public boolean isCompleteEvent(){
        return permissionEventChecker.getBoolean(COMPLETE_PERMISSION_CHECK_KEY, false);
    }

    public void start(){
        if (!isCompleteEvent()) return;
        permissionEventChecker.putBoolean(COMPLETE_PERMISSION_CHECK_KEY, false);
        permissionEventChecker.commit();
    }

    public void complete(@Nullable Runnable runnable){
        permissionEventChecker.putBoolean(COMPLETE_PERMISSION_CHECK_KEY, true);
        permissionEventChecker.commit();
        if(runnable != null) runnable.run();
    }
}
