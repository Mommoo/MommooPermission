package com.mommoo.permission;

import com.mommoo.permission.listener.OnPermissionDenied;
import com.mommoo.permission.listener.OnPermissionGranted;
import com.mommoo.permission.repository.DenyInfo;
import com.mommoo.permission.utils.observer.PermissionEventCode;
import com.mommoo.permission.utils.observer.PermissionSubscriber;

import java.util.List;

/**
 * Copyright 2017 Mommoo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author mommoo
 * @since 2017-06-09
 */

class PermissionSubscriberFactory {

    private PermissionSubscriberFactory(){}

    static PermissionSubscriber createSelfCheckSubscriber(OnPermissionGranted onPermissionGranted, OnPermissionDenied onPermissionDenied) {
        return createCommonSubscriber(PermissionEventCode.SELF_CHECK, onPermissionGranted, onPermissionDenied);
    }

    static PermissionSubscriber createUserResponseSubscriber(OnPermissionGranted onPermissionGranted, OnPermissionDenied onPermissionDenied){
        return createCommonSubscriber(PermissionEventCode.USER_RESPONSE, onPermissionGranted, onPermissionDenied);
    }

    private static PermissionSubscriber createCommonSubscriber(final PermissionEventCode targetCode, final OnPermissionGranted onPermissionGranted, final OnPermissionDenied onPermissionDenied){
        return new PermissionSubscriber() {
            @Override
            public void update(PermissionEventCode permissionEventCode, List<String> grantedPermissionList, List<DenyInfo> deniedPermissionList) {
                if (permissionEventCode == targetCode){
                    if (grantedPermissionList.size() > 0) onPermissionGranted.onGranted(grantedPermissionList);
                    if (deniedPermissionList.size() > 0) onPermissionDenied.onDenied(deniedPermissionList);
                }


            }
        };
    }
}
