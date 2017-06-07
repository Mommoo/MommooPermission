package com.mommoo.permission;

/**
 * Created by mommoo on 2017-06-01.
 */

public class DenyInfo {
    private final String PERMISSION;
    private final boolean IS_USER_NEVER_ASK_AGAIN_CHEKD;

    public DenyInfo(String permission, boolean isUserNeverAskAgainChecked){
        this.PERMISSION = permission;
        this.IS_USER_NEVER_ASK_AGAIN_CHEKD = isUserNeverAskAgainChecked;
    }

    public String getPermission(){
        return this.PERMISSION;
    }

    public boolean isUserNeverAskAgainChecked(){
        return IS_USER_NEVER_ASK_AGAIN_CHEKD;
    }
}
