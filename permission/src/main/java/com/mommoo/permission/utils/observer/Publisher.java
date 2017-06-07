package com.mommoo.permission.utils.observer;

/**
 * Created by mommoo on 2017-06-01.
 */

public interface Publisher {
    public PermissionEventProvider registerSubscriber(PermissionSubscriber permissionSubscriber);
    public void unRegisterSubscriber();

}
