package com.mommoo.permission;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mommoo.permission.repository.DenyInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.mommoo.permission.AutoPermissionExtraKey.PERMISSION_ARRAY_EXTRA_KEY;

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

class PermissionClassifier {
    private final Activity activity;
    private final String[] permissions;
    private final List<String> grantedPermissionList = new ArrayList<>();
    private final List<DenyInfo> deniedPermissionList = new ArrayList<>();

    PermissionClassifier(Activity activity, Bundle savedInstanceState){
        this.activity = activity;
        this.permissions = savedInstanceState == null ?
                activity.getIntent().getStringArrayExtra(PERMISSION_ARRAY_EXTRA_KEY)
                : savedInstanceState.getStringArray(PERMISSION_ARRAY_EXTRA_KEY);
    }

    private boolean isGranted(String permission){
        return ContextCompat.checkSelfPermission(this.activity, permission) == PERMISSION_GRANTED;
    }

    private boolean isUserNeverAskAgainChecked(String permission){
        return !ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission);
    }

    String[] getPermissions(){
        return permissions;
    }

    String[] getNeedToCheckPermissions(){
        List<String> needToCheckPermissionList = new ArrayList<>();

        for (String permission : permissions){
            if (!isGranted(permission)) needToCheckPermissionList.add(permission);
        }

        return needToCheckPermissionList.toArray(new String[needToCheckPermissionList.size()]);
    }

    List<String> getGrantedPermissionList(){
        this.grantedPermissionList.clear();

        for (String permission : permissions) {
            if (isGranted(permission)) this.grantedPermissionList.add(permission);
        }

        return Collections.unmodifiableList(this.grantedPermissionList);
    }

    List<DenyInfo> getDeniedPermissionList(){
        this.deniedPermissionList.clear();

        for (String permission : permissions){
            if (!isGranted(permission)) this.deniedPermissionList.add(new DenyInfo(permission, isUserNeverAskAgainChecked(permission)));
        }

        return Collections.unmodifiableList(this.deniedPermissionList);
    }
}

