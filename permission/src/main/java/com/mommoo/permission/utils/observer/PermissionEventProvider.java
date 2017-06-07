package com.mommoo.permission.utils.observer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mommoo.permission.DenyInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mommoo on 2017-06-01.
 */

public class PermissionEventProvider{
    private static final PermissionEventProvider eventProvider = new PermissionEventProvider();
    private ArrayList<PermissionSubscriber> permissionSubscriberList = new ArrayList<>();

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


    public void notifyToSubscriber(PermissionEventCode eventCode, @NonNull List<String> grantedPermissionList, @NonNull List<DenyInfo> deniedPermissionList){
        System.out.println("notifyToSubscriber item size : " +permissionSubscriberList.size());
        for (PermissionSubscriber subscriber : permissionSubscriberList)
            subscriber.update(eventCode, grantedPermissionList, deniedPermissionList);
    }
}
