package com.mommoo.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mommoo.permission.utils.PermissionDivideHandler;
import com.mommoo.permission.utils.dialog.PromiseDialog;
import com.mommoo.permission.utils.dialog.PromiseDialogFactory;
import com.mommoo.permission.utils.dialog.SequenceDialogHandler;
import com.mommoo.permission.utils.observer.PermissionEventChecker;
import com.mommoo.permission.utils.observer.PermissionEventCode;
import com.mommoo.permission.utils.observer.PermissionEventProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by mommoo on 2017-05-31.
 */

public class AutoPermissionActivity extends AppCompatActivity{
    private static final int MARSHMALLOW_SDK_VERSION_INT = 23;
    private static final int PERMISSION_REQUEST_CODE = 0;

    public static final int USER_PERMISSION_ACTION_RESULT_CODE = 0;

    public static final String PERMISSION_ARRAY_EXTRA_KEY = "Permissions";
    public static final String PRE_NOTICE_TITLE_EXTRA_KEY = "preNoticeTitle";
    public static final String PRE_NOTICE_MESSAGE_EXTRA_KEY = "preNoticeMessage";
    public static final String POST_NOTICE_TITLE_EXTRA_KEY = "postNoticeTitle";
    public static final String POST_NOTICE_MESSAGE_EXTRA_KEY = "postNoticeMessage";
    public static final String OFFER_GRANT_PERMISSION_TITLE_EXTRA_KEY = "offerGrantPermissionTitle";
    public static final String OFFER_GRANT_PERMISSION_MESSAGE_EXTRA_KEY = "offerGrantPermissionMessage";
    public static final String TARGET_APPLICATION_PACKAGE_NAME_EXTRA_KEY = "targetApplicationPackageName";

    private PermissionDivideHandler permissionDivideHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);

        if (Build.VERSION.SDK_INT < MARSHMALLOW_SDK_VERSION_INT) {
            grantAllPermissions();
            return;
        }

        permissionDivideHandler = new PermissionDivideHandler(this, savedInstanceState);

        if (isNeedToHandleUserResponse()){
            handleUserResponse();
            return;
        }

        PermissionEventChecker.getChecker(this).start();

        new SequenceDialogHandler()
                .showDialog(createPreNoticeDialog())
                .then(isDialogShown -> ActivityCompat.requestPermissions(AutoPermissionActivity.this, permissionDivideHandler.getNeedToCheckPermissions() , PERMISSION_REQUEST_CODE));
    }

    private boolean isNeedToHandleUserResponse(){
        return !PermissionEventChecker.getChecker(this).isCompleteEvent();
    }
    /* 다이얼로그 팩토리 만들기...! */
    private PromiseDialog createPreNoticeDialog(){
        String preNoticeTitle = getIntent().getStringExtra(PRE_NOTICE_TITLE_EXTRA_KEY);
        String preNoticeMessage = getIntent().getStringExtra(PRE_NOTICE_MESSAGE_EXTRA_KEY);
        return PromiseDialogFactory.createPreNoticeDialog(this, preNoticeTitle, preNoticeMessage);
    }

    private PromiseDialog createPostNoticeDialog(){
        String postNoticeTitle = getIntent().getStringExtra(POST_NOTICE_TITLE_EXTRA_KEY);
        String postNoticeMessage = getIntent().getStringExtra(POST_NOTICE_MESSAGE_EXTRA_KEY);
        return PromiseDialogFactory.createPostNoticeDialog(this, postNoticeTitle, postNoticeMessage);
    }

    private PromiseDialog createOfferGrantNotice(){
        if (!isShouldShowOfferGrantDialog())
            return PromiseDialogFactory.createInvalidDialog();

        String offerGrantNoticeTitle = getIntent().getStringExtra(OFFER_GRANT_PERMISSION_TITLE_EXTRA_KEY);
        String offerGrantNoticeMessage = getIntent().getStringExtra(OFFER_GRANT_PERMISSION_MESSAGE_EXTRA_KEY);
        String packageName = getIntent().getStringExtra(TARGET_APPLICATION_PACKAGE_NAME_EXTRA_KEY);
        return PromiseDialogFactory.createOfferGrantNoticeDialog(this, offerGrantNoticeTitle, offerGrantNoticeMessage, packageName);
    }

    private boolean isShouldShowOfferGrantDialog(){
        boolean isExistDeniedPermission = permissionDivideHandler.getDeniedPermissionList().size() > 0;
        boolean isExistDeniedMessage = getIntent().getStringExtra(OFFER_GRANT_PERMISSION_MESSAGE_EXTRA_KEY) != null;
        return isExistDeniedPermission && isExistDeniedMessage;
    }

    private void grantAllPermissions(){
        notifySelfCheckDone(Arrays.asList(permissionDivideHandler.getPermissions()), new ArrayList<>());
    }

    private void notifySelfCheckDone(List<String> grantedPermissionList, List<DenyInfo> deniedPermissionList){
        PermissionEventProvider
                .getEventProvider()
                .notifyToSubscriber(PermissionEventCode.SELF_CHECK,grantedPermissionList,deniedPermissionList);
    }

    private void notifyUserResponseDone(List<String> grantedPermissionList, List<DenyInfo> deniedPermissionList){
        PermissionEventProvider
                .getEventProvider()
                .notifyToSubscriber(PermissionEventCode.USER_RESPONSE,grantedPermissionList,deniedPermissionList);
    }

    private void handleUserResponse(){
        notifyUserResponseDone(permissionDivideHandler.getGrantedPermissionList(), permissionDivideHandler.getDeniedPermissionList());
        permissionEventDone();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(PERMISSION_ARRAY_EXTRA_KEY, getIntent().getStringArrayExtra(PERMISSION_ARRAY_EXTRA_KEY));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode != PERMISSION_REQUEST_CODE) return;

        new SequenceDialogHandler()
                .showDialog(createPostNoticeDialog())
                .then(isDialogShown -> notifySelfCheckDone(permissionDivideHandler.getGrantedPermissionList(),permissionDivideHandler.getDeniedPermissionList()))
                .showDialog(createOfferGrantNotice())
                .then(isDialogShown -> {
                    if(!isDialogShown) permissionEventDone();
                });
    }

    public void permissionEventDone(){
        PermissionEventChecker
                .getChecker(this)
                .complete(()-> {
                    PermissionEventProvider.getEventProvider().unRegisterAllSubscribers();
                    finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_PERMISSION_ACTION_RESULT_CODE){
            handleUserResponse();
        }
    }
    
}
