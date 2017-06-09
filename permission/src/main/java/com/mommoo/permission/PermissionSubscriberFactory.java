package com.mommoo.permission;

import com.mommoo.permission.repository.DenyInfo;
import com.mommoo.permission.utils.observer.PermissionEventCode;
import com.mommoo.permission.utils.observer.PermissionSubscriber;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    static PermissionSubscriber createSelfCheckSubscriber(Consumer<List<String>> grantedConsumer, Consumer<List<DenyInfo>> deniedConsumer) {
        return createCommonSubscriber(PermissionEventCode.SELF_CHECK,grantedConsumer,deniedConsumer);
    }

    static PermissionSubscriber createUserResponseSubscriber(Consumer<List<String>> grantedConsumer, Consumer<List<DenyInfo>> deniedConsumer){
        return createCommonSubscriber(PermissionEventCode.USER_RESPONSE,grantedConsumer,deniedConsumer);
    }

    private static PermissionSubscriber createCommonSubscriber(PermissionEventCode targetCode, Consumer<List<String>> grantedConsumer, Consumer<List<DenyInfo>> deniedConsumer){
        return (eventCode, grantedPermissionList, deniedPermissionList) -> {
            if(targetCode != eventCode) return;
            if (grantedPermissionList.size() > 0) grantedConsumer.accept(grantedPermissionList);
            if (deniedPermissionList.size() > 0) deniedConsumer.accept(deniedPermissionList);
        };
    }
}
