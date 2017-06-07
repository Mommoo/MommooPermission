package com.mommoo.permission;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.mommoo.permission.listener.OnPermissionDenied;
import com.mommoo.permission.listener.OnPermissionGranted;
import com.mommoo.permission.listener.OnUserDirectPermissionDeny;
import com.mommoo.permission.listener.OnUserDirectPermissionGrant;
import com.mommoo.permission.repository.ProxyData;
import com.mommoo.permission.utils.observer.PermissionEventCode;
import com.mommoo.permission.utils.observer.PermissionEventProvider;
import com.mommoo.permission.utils.observer.PermissionSubscriber;

import java.util.Collection;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mommoo.permission.AutoPermissionExtraKey.OFFER_GRANT_PERMISSION_MESSAGE_EXTRA_KEY;
import static com.mommoo.permission.AutoPermissionExtraKey.OFFER_GRANT_PERMISSION_TITLE_EXTRA_KEY;
import static com.mommoo.permission.AutoPermissionExtraKey.PERMISSION_ARRAY_EXTRA_KEY;
import static com.mommoo.permission.AutoPermissionExtraKey.POST_NOTICE_MESSAGE_EXTRA_KEY;
import static com.mommoo.permission.AutoPermissionExtraKey.POST_NOTICE_TITLE_EXTRA_KEY;
import static com.mommoo.permission.AutoPermissionExtraKey.PRE_NOTICE_MESSAGE_EXTRA_KEY;
import static com.mommoo.permission.AutoPermissionExtraKey.PRE_NOTICE_TITLE_EXTRA_KEY;
import static com.mommoo.permission.AutoPermissionExtraKey.TARGET_APPLICATION_PACKAGE_NAME_EXTRA_KEY;

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

public class MommooPermission {
    private final Builder builder;

    private MommooPermission(final Builder builder){
        this.builder = builder;

        PermissionEventProvider
                .getEventProvider()
                .registerSubscriber(createSelfCheckSubscriber())
                .registerSubscriber(createUserResponseSubscriber());
    }

    private boolean isAppRestatedAndNeedToCheckUserResponse(){
        return PermissionEventProvider.getEventProvider().isExistProxy();
    }

    private PermissionSubscriber createSelfCheckSubscriber(){
        return (eventCode, grantedPermissionList, deniedPermissionList) -> {
            if (eventCode == PermissionEventCode.SELF_CHECK){
                if (isValidCondition(grantedPermissionList, builder.onPermissionGranted))
                    builder.onPermissionGranted.onGranted(grantedPermissionList);
                if (isValidCondition(deniedPermissionList, builder.onPermissionDenied))
                    builder.onPermissionDenied.onDenied(deniedPermissionList);
            }
        };
    }

    private PermissionSubscriber createUserResponseSubscriber(){
        return (eventCode, grantedPermissionList, deniedPermissionList) -> {
            if (eventCode == PermissionEventCode.USER_RESPONSE){
                if (isValidCondition(grantedPermissionList, builder.onUserDirectPermissionGrant))
                    builder.onUserDirectPermissionGrant.onUserDirectGrant(grantedPermissionList);
                if (isValidCondition(deniedPermissionList, builder.onUserDirectPermissionDeny))
                    builder.onUserDirectPermissionDeny.onUserDirectDeny(deniedPermissionList);
            }
        };
    }

    private boolean isValidCondition(Collection<?> collection, Object object){
        return collection.size() > 0 && object != null;
    }

    public void checkPermissions(){
         /* If user activity was destroyed abnormally, user activity re-invoke checkPermissions method
          * So, we have to prevent to re invoked permission check activity
          * And, we have to rollback user response data of permission check
          * */
        if (isAppRestatedAndNeedToCheckUserResponse()){
            PermissionEventProvider eventProvider = PermissionEventProvider.getEventProvider();

            ProxyData proxyData = eventProvider.getProxyData();

            eventProvider.notifyToSubscriber(proxyData.getEventCode(),proxyData.getGrantedPermissionList(),proxyData.getDeniedPermissionList());
            eventProvider.unRegisterAllSubscribers();
            eventProvider.removeProxyData();
            return;
        }

        Intent intent = new Intent(this.builder.context,AutoPermissionActivity.class);
        intent.putExtra(PERMISSION_ARRAY_EXTRA_KEY, this.builder.permissions);
        intent.putExtra(PRE_NOTICE_TITLE_EXTRA_KEY, this.builder.preNoticeTitle);
        intent.putExtra(PRE_NOTICE_MESSAGE_EXTRA_KEY, this.builder.preNoticeMessage);
        intent.putExtra(POST_NOTICE_TITLE_EXTRA_KEY, this.builder.postNoticeTitle);
        intent.putExtra(POST_NOTICE_MESSAGE_EXTRA_KEY, this.builder.postNoticeMessage);
        intent.putExtra(OFFER_GRANT_PERMISSION_TITLE_EXTRA_KEY, this.builder.offerGrantPermissionTitle);
        intent.putExtra(OFFER_GRANT_PERMISSION_MESSAGE_EXTRA_KEY, this.builder.offerGrantPermissionMessage);
        intent.putExtra(TARGET_APPLICATION_PACKAGE_NAME_EXTRA_KEY, this.builder.context.getPackageName());

        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(FLAG_ACTIVITY_MULTIPLE_TASK);

        this.builder.context.startActivity(intent);
    }

    public static class Builder{
        private Context context;
        private String[] permissions;
        private String preNoticeTitle, preNoticeMessage;
        private String postNoticeTitle, postNoticeMessage;
        private String offerGrantPermissionTitle, offerGrantPermissionMessage;
        private OnPermissionGranted onPermissionGranted;
        private OnPermissionDenied onPermissionDenied;
        private OnUserDirectPermissionGrant onUserDirectPermissionGrant;
        private OnUserDirectPermissionDeny onUserDirectPermissionDeny;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setPermissions(String... permissions){
            this.permissions = permissions;
            return this;
        }

        public Builder setPreNoticeDialogData(@NonNull String preNoticeTitle, @NonNull String preNoticeMessage){
            this.preNoticeTitle = preNoticeTitle;
            this.preNoticeMessage = preNoticeMessage;
            return this;
        }

        public Builder setPostNoticeDialogData(@NonNull String postNoticeTitle, @NonNull String postNoticeMessage){
            this.postNoticeTitle = postNoticeTitle;
            this.postNoticeMessage = postNoticeMessage;
            return this;
        }

        public Builder setOfferGrantPermissionData(@NonNull String offerGrantPermissionTitle, @NonNull String offerGrantPermissionMessage){
            this.offerGrantPermissionTitle = offerGrantPermissionTitle;
            this.offerGrantPermissionMessage = offerGrantPermissionMessage;
            return this;
        }

        public Builder setOnPermissionGranted(OnPermissionGranted onPermissionGranted){
            this.onPermissionGranted = onPermissionGranted;
            return this;
        }

        public Builder setOnPermissionDenied(OnPermissionDenied onPermissionDenied){
            this.onPermissionDenied = onPermissionDenied;
            return this;
        }

        public Builder setOnUserDirectPermissionGrant(OnUserDirectPermissionGrant onUserDirectPermissionGrant){
            this.onUserDirectPermissionGrant = onUserDirectPermissionGrant;
            return this;
        }

        public Builder setOnUserDirectPermissionDeny(OnUserDirectPermissionDeny onUserDirectPermissionDeny){
            this.onUserDirectPermissionDeny = onUserDirectPermissionDeny;
            return this;
        }

        public MommooPermission build(){
            return new MommooPermission(this);
        }
    }
}
