package com.mommoo.mommoopermission;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mommoo.permission.MommooPermission;
import com.mommoo.permission.listener.OnPermissionDenied;
import com.mommoo.permission.listener.OnPermissionGranted;
import com.mommoo.permission.listener.OnUserDirectPermissionDeny;
import com.mommoo.permission.listener.OnUserDirectPermissionGrant;
import com.mommoo.permission.repository.DenyInfo;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        new MommooPermission.Builder(this)
                .setPermissions(Manifest.permission.WRITE_CALENDAR, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS)
                .setOnUserDirectPermissionGrant(new OnUserDirectPermissionGrant() {
                    @Override
                    public void onUserDirectGrant(List<String> permissionList) {
                        for (String permission : permissionList) System.out.println("userGrant " + permission);
                    }
                })
                .setOnUserDirectPermissionDeny(new OnUserDirectPermissionDeny() {
                    @Override
                    public void onUserDirectDeny(List<DenyInfo> deniedPermissionList) {
                        for (DenyInfo denyInfo : deniedPermissionList){
                            System.out.println("userDeny " + denyInfo.getPermission() +" , userNeverSeeChecked " + denyInfo.isUserNeverAskAgainChecked());
                        }
                    }
                })
                .setOnPermissionDenied(new OnPermissionDenied() {
                    @Override
                    public void onDenied(List<DenyInfo> deniedPermissionList) {
                        for (DenyInfo denyInfo : deniedPermissionList){
                            System.out.println("isDenied " + denyInfo.getPermission() +" , userNeverSeeChecked " + denyInfo.isUserNeverAskAgainChecked());
                        }
                    }
                })
                .setOnPermissionGranted(new OnPermissionGranted() {
                    @Override
                    public void onGranted(List<String> permissionList) {
                        for (String permission : permissionList) System.out.println("granted " + permission);
                    }
                })
                .setPreNoticeDialogData("Pre Notice","Please accept all permission to using this app")
                .setPostNoticeDialogData("Post Notice","If you don't accept permission\nyou have to grant permission directly")
                .setOfferGrantPermissionData("Move To App Setup","1. Touch the 'SETUP'\n" +
                        "2. Touch the 'Permission' tab\n"+
                        "3. Grant all permissions by dragging toggle button")
                .build()
                .checkPermissions();
    }
}
