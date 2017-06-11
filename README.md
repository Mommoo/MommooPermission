# MommooPermission

After the version of the Android marshmallow, 

We have to declare permission in Manifest.xml file as well as **check permission at runtime**

Also, **User anytime can turn on/off permission in app-setup**,

So **we need to check permission whenever our app have started**

For that, this "MommooPermisson Libaray" can help easy to check permissions


# Example Image
![example screenshot](./permission_screen_shot3.png)


# Set up

### Gradle
```java
dependencies {
    compile 'com.mommoo.android:mommoo-permission:0.1.3'
}
```

# How to use

### Code Example
```java
new MommooPermission.Builder(this)
                .setPermissions(Manifest.permission.WRITE_CALENDAR, 
                        Manifest.permission.CAMERA, 
                        Manifest.permission.READ_CONTACTS)
                .setOnPermissionDenied(new OnPermissionDenied() {
                    @Override
                    public void onDenied(List<DenyInfo> deniedPermissionList) {
                        for (DenyInfo denyInfo : deniedPermissionList){
                            System.out.println("isDenied : " + denyInfo.getPermission() +" , "+ 
                                               "userNeverSeeChecked : " + denyInfo.isUserNeverAskAgainChecked());
                        }
                    }
                })
                .setPreNoticeDialogData("Pre Notice","Please accept all permission to using this app")
                .setOfferGrantPermissionData("Move To App Setup","1. Touch the 'SETUP'\n" +
                        "2. Touch the 'Permission' tab\n"+
                        "3. Grant all permissions by dragging toggle button")
                .build()
                .checkPermissions();
```

### API for cusmizing
1. Show Dialog
 * ```setPreNoticeDialogData(String preNoticeTitle, String preNoticeMessage)```
   * If use this, app will show pre notice dialog before permission dialog is shown
 * ```setPostNoticeDialogData(String postNoticeTitle, String postNoticeMessage)```
   * If use this, app will show post notice dialog after permission dialog is closed
 * ```setOfferGrantPermissionData(String offerGrantPermissionTitle, String offerGrantPermissionDialog)```
   * If use this, app will show guide dialog that help user to grant permission direclty at setup screen
 
