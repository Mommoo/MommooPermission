package com.mommoo.permission;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.mommoo.permission.repository.DenyInfo;
import com.mommoo.permission.utils.dialog.PromiseDialog;
import com.mommoo.permission.utils.dialog.PromiseDialogFactory;
import com.mommoo.permission.utils.dialog.SequenceDialogHandler;
import com.mommoo.permission.utils.observer.PermissionEventChecker;
import com.mommoo.permission.utils.observer.PermissionEventCode;
import com.mommoo.permission.utils.observer.PermissionEventProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mommoo.permission.AutoPermissionExtraKey.*;

/**
 * Copyright 2017 Mommoo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author mommoo
 * @since 2017-05-31
 *
 */

class AutoPermissionActivity extends AppCompatActivity{
    private static final int MARSHMALLOW_SDK_VERSION_INT = 23;
    private static final int PERMISSION_REQUEST_CODE = 0;

    private PermissionClassifier permissionClassifier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);

        permissionClassifier = new PermissionClassifier(this, savedInstanceState);

        if (Build.VERSION.SDK_INT < MARSHMALLOW_SDK_VERSION_INT || permissionClassifier.getNeedToCheckPermissions().length == 0){
            grantAllPermissions();
            permissionEventDone();
            return;
        }

        /* This activity can be destroyed abnormally by user deny directly at app setting permission screen
         * If this activity was destroyed then, user activity is destroyed too.
         * So, we have to handle that sending callback-event to programmer who using this library
         */
        if (isNeedToHandleUserResponse()){
            handleUserResponse();
            return;
        }

        PermissionEventChecker.getChecker(this).start();

        SequenceDialogHandler
                .showDialog(createPreNoticeDialog())
                .then(isDialogShown -> ActivityCompat.requestPermissions(AutoPermissionActivity.this, permissionClassifier.getNeedToCheckPermissions() , PERMISSION_REQUEST_CODE));
    }

    private boolean isNeedToHandleUserResponse(){
        return !PermissionEventChecker.getChecker(this).isCompleteEvent();
    }

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
        boolean isExistDeniedPermission = permissionClassifier.getDeniedPermissionList().size() > 0;
        boolean isExistDeniedMessage = getIntent().getStringExtra(OFFER_GRANT_PERMISSION_MESSAGE_EXTRA_KEY) != null;
        return isExistDeniedPermission && isExistDeniedMessage;
    }

    private void grantAllPermissions(){
        notifySelfCheckDone(Arrays.asList(permissionClassifier.getPermissions()), new ArrayList<>());
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
        PermissionEventProvider
                .getEventProvider()
                .setProxyDataSet(PermissionEventCode.USER_RESPONSE, permissionClassifier.getGrantedPermissionList(), permissionClassifier.getDeniedPermissionList());
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

        SequenceDialogHandler
                .showDialog(createPostNoticeDialog())
                .then(isDialogShown -> notifySelfCheckDone(permissionClassifier.getGrantedPermissionList(), permissionClassifier.getDeniedPermissionList()))
                .showDialog(createOfferGrantNotice())
                .then(isDialogShown -> {if(!isDialogShown) permissionEventDone();});
    }

    public void permissionEventDone(){
        PermissionEventChecker.getChecker(this)
                .complete(()-> {
                    PermissionEventProvider.getEventProvider().unRegisterAllSubscribers();
                    finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != USER_PERMISSION_ACTION_RESULT_CODE) return;

        notifyUserResponseDone(permissionClassifier.getGrantedPermissionList(), permissionClassifier.getDeniedPermissionList());
        permissionEventDone();
    }
}
