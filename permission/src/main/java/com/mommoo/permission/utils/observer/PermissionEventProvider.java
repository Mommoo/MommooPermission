package com.mommoo.permission.utils.observer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.mommoo.permission.repository.DenyInfo;
import com.mommoo.permission.repository.ProxyData;

import java.util.ArrayList;
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
 * @since 2017-06-01
 *
 */

public class PermissionEventProvider{
    private static final PermissionEventProvider eventProvider = new PermissionEventProvider();
    private ArrayList<PermissionSubscriber> permissionSubscriberList = new ArrayList<>();
    private ProxyData proxyData;

    private PermissionEventProvider(){}

    public static PermissionEventProvider getEventProvider(){
        return eventProvider;
    }

    public PermissionEventProvider registerSubscriber(@Nullable PermissionSubscriber permissionSubscriber) {
        if(permissionSubscriber != null) this.permissionSubscriberList.add(permissionSubscriber);
        System.out.println(this.permissionSubscriberList.size());
        return this;
    }

    public void unRegisterAllSubscribers() {
        this.permissionSubscriberList.clear();
    }

    public void removeProxyData(){
        this.proxyData = null;
    }

    public void setProxyDataSet(PermissionEventCode eventCode, @NonNull List<String> grantedPermissionList, @NonNull List<DenyInfo> deniedPermissionList){
        proxyData = new ProxyData(eventCode, grantedPermissionList, deniedPermissionList);
    }

    public ProxyData getProxyData(){
        return proxyData;
    }

    public boolean isExistProxy(){
        return proxyData != null;
    }

    public void notifyToSubscriber(PermissionEventCode eventCode, @NonNull List<String> grantedPermissionList, @NonNull List<DenyInfo> deniedPermissionList){
        for (PermissionSubscriber subscriber : permissionSubscriberList)
            subscriber.update(eventCode, grantedPermissionList, deniedPermissionList);
    }
}
