package com.mommoo.permission.utils.observer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.mommoo.library.toolkit.MommooSharedPreference;

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

public class PermissionEventChecker {
    private static final String PERMISSION_EVENT_CHECKER_SHARED_PREFERENCE_NAME = PermissionEventChecker.class.getSimpleName();
    private static final String COMPLETE_PERMISSION_CHECK_KEY = "isCompletePermissionCheck";
    private final MommooSharedPreference permissionEventChecker;


    private static PermissionEventChecker instance;

    private PermissionEventChecker(Context context){
        this.permissionEventChecker = new MommooSharedPreference(context,PERMISSION_EVENT_CHECKER_SHARED_PREFERENCE_NAME);
        if (!this.permissionEventChecker.isContainKey(COMPLETE_PERMISSION_CHECK_KEY)){
            permissionEventChecker.putBoolean(COMPLETE_PERMISSION_CHECK_KEY, true);
            permissionEventChecker.commit();
        }
    }

    public static PermissionEventChecker getChecker(Context context){
        if(instance == null) instance = new PermissionEventChecker(context);
        return instance;
    }

    public boolean isCompleteEvent(){
        return permissionEventChecker.getBoolean(COMPLETE_PERMISSION_CHECK_KEY, false);
    }

    public void start(){
        if (!isCompleteEvent()) return;
        permissionEventChecker.putBoolean(COMPLETE_PERMISSION_CHECK_KEY, false);
        permissionEventChecker.commit();
    }

    public void complete(@Nullable Runnable runnable){
        permissionEventChecker.putBoolean(COMPLETE_PERMISSION_CHECK_KEY, true);
        permissionEventChecker.commit();
        if(runnable != null) runnable.run();
    }
}
