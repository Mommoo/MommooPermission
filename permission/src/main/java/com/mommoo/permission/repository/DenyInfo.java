package com.mommoo.permission.repository;

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

public class DenyInfo {
    private final String PERMISSION;
    private final boolean IS_USER_NEVER_ASK_AGAIN_CHECKED;

    public DenyInfo(String permission, boolean isUserNeverAskAgainChecked){
        this.PERMISSION = permission;
        this.IS_USER_NEVER_ASK_AGAIN_CHECKED = isUserNeverAskAgainChecked;
    }

    public String getPermission(){
        return this.PERMISSION;
    }

    public boolean isUserNeverAskAgainChecked(){
        return IS_USER_NEVER_ASK_AGAIN_CHECKED;
    }
}
