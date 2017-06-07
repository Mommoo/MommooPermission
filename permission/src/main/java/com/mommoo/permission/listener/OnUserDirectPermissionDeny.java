package com.mommoo.permission.listener;

import com.mommoo.permission.DenyInfo;

import java.util.List;

/**
 * Created by mommoo on 2017-06-05.
 */

public interface OnUserDirectPermissionDeny {
    public void onUserDirectDeny(List<DenyInfo> deniedPermissionList);
}
