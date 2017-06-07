package com.mommoo.permission.listener;

import com.mommoo.permission.DenyInfo;

import java.util.List;

/**
 * Created by mommoo on 2017-06-05.
 */

public interface OnPermissionDenied {
    public void onDenied(List<DenyInfo> deniedPermissionList);
}
