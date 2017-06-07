package com.mommoo.permission.listener;

import java.util.List;

/**
 * Created by mommoo on 2017-06-05.
 */

public interface OnPermissionGranted {
    public void onGranted(List<String> permissionList);
}
