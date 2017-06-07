package com.mommoo.permission.utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mommoo.permission.AutoPermissionActivity;
import com.mommoo.permission.DenyInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by mommoo on 2017-06-07.
 */

public class PermissionDivideHandler{
    private final Activity activity;
    private final String[] permissions;
    private final List<String> grantedPermissionList = new ArrayList<>();
    private final List<DenyInfo> deniedPermissionList = new ArrayList<>();

    public PermissionDivideHandler(Activity activity, Bundle savedInstanceState){
        this.activity = activity;
        this.permissions = savedInstanceState == null ?
                activity.getIntent().getStringArrayExtra(AutoPermissionActivity.PERMISSION_ARRAY_EXTRA_KEY)
                : savedInstanceState.getStringArray(AutoPermissionActivity.PERMISSION_ARRAY_EXTRA_KEY);
    }

    private boolean isGranted(String permission){
        return ContextCompat.checkSelfPermission(this.activity, permission) == PERMISSION_GRANTED;
    }

    private boolean isUserNeverSeeCheck(String permission){
        return !ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission);
    }

    public String[] getPermissions(){
        return permissions;
    }

    public String[] getNeedToCheckPermissions(){
        List<String> needToCheckPermissionList = new ArrayList<>();

        for (String permission : permissions){
            if (!isGranted(permission)) needToCheckPermissionList.add(permission);
        }

        return needToCheckPermissionList.toArray(new String[needToCheckPermissionList.size()]);
    }

    public List<String> getGrantedPermissionList(){
        this.grantedPermissionList.clear();

        for (String permission : permissions) {
            if (isGranted(permission)) this.grantedPermissionList.add(permission);
        }

        return Collections.unmodifiableList(this.grantedPermissionList);
    }

    public List<DenyInfo> getDeniedPermissionList(){
        this.deniedPermissionList.clear();

        for (String permission : permissions){
            if (!isGranted(permission)) this.deniedPermissionList.add(new DenyInfo(permission,isUserNeverSeeCheck(permission)));
        }

        return Collections.unmodifiableList(this.deniedPermissionList);
    }
}

