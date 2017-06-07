package com.mommoo.permission.repository;

import android.support.annotation.NonNull;

import com.mommoo.permission.utils.observer.PermissionEventCode;

import java.util.List;

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
 * @since 2017-06-07
 *
 */

public class ProxyData{
    private final PermissionEventCode eventCode;
    private final List<String> grantedPermissionList;
    private final List<DenyInfo> deniedPermissionList;

    public ProxyData(PermissionEventCode eventCode, @NonNull List<String> grantedPermissionList, @NonNull List<DenyInfo> deniedPermissionList){
        this.eventCode = eventCode;
        this.grantedPermissionList = grantedPermissionList;
        this.deniedPermissionList = deniedPermissionList;
    }

    public PermissionEventCode getEventCode(){
        return eventCode;
    }

    public List<String> getGrantedPermissionList(){
        return grantedPermissionList;
    }

    public List<DenyInfo> getDeniedPermissionList(){
        return deniedPermissionList;
    }
}
