package com.mommoo.permission.utils.observer;

import android.os.Parcelable;

import com.mommoo.permission.DenyInfo;

import java.util.List;

/**
 * Created by mommoo on 2017-06-01.
 */

public interface PermissionSubscriber{
    public void update(PermissionEventCode permissionEventCode, List<String> grantedPermissionList, List<DenyInfo> deniedPermissionList);
}
