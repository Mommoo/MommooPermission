package com.mommoo.permission.utils.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.mommoo.library.widget.alert.AlertDialog;
import com.mommoo.library.widget.alert.OnPositiveListener;
import com.mommoo.library.widget.alert.Theme;
import com.mommoo.permission.AutoPermissionActivity;

/**
 * Created by mommoo on 2017-06-05.
 */

public class PromiseDialogFactory {

    private static PromiseDialog INVALID_DIALOG = new PromiseDialog() {
        @Override public void show() {}
        @Override public boolean isShowing() {
            return false;
        }
    };

    private PromiseDialogFactory(){}

    private static PromiseDialog createPromiseDialog(final AlertDialog alertDialog){
        return new PromiseDialog() {
            @Override
            public void show() {
                alertDialog.show();
            }

            @Override
            public boolean isShowing() {
                return alertDialog.isShowing();
            }
        };
    }

    private static AlertDialog.Builder createBasicDialogBuilder(Context context){
        return new AlertDialog.Builder(context)
                .setTheme(Theme.DARK)
                .setNegativeHide(true);
    }

    private static boolean isInValid(String... strings){
        for (String string : strings){
            if (string == null || string.trim().equals("")) return true;
        }
        return false;
    }

    public static PromiseDialog createPreNoticeDialog(Context context,@Nullable String preNoticeTitle, @Nullable String preNoticeMessage){
        if (isInValid(preNoticeTitle, preNoticeMessage)) return createInvalidDialog();

        final AlertDialog preNoticeDialog = createBasicDialogBuilder(context)
                .setTitle(preNoticeTitle)
                .setContent(preNoticeMessage)
                .setPositiveText("OK")
                .build();

        return createPromiseDialog(preNoticeDialog);
    }

    public static PromiseDialog createPostNoticeDialog(Context context, @Nullable String postNoticeTitle, @Nullable String postNoticeMessage){
        if (isInValid(postNoticeTitle, postNoticeMessage)) return createInvalidDialog();

        final AlertDialog postNoticeDialog = createBasicDialogBuilder(context)
                .setTitle(postNoticeTitle)
                .setContent(postNoticeMessage)
                .setPositiveText("OK")
                .build();

        return createPromiseDialog(postNoticeDialog);
    }

    public static PromiseDialog createOfferGrantNoticeDialog(final Activity context
            ,@Nullable String title, @Nullable String message, @Nullable final String packageName) {
        if (isInValid(title, message, packageName)) return createInvalidDialog();

        final AlertDialog offerGrantNoticeDialog = createBasicDialogBuilder(context)
                .setTitle(title)
                .setContent(message)
                .setPositiveText("SETUP")
                .setOnPositiveListener(()->{
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:"+packageName));
                    context.startActivityForResult(intent, AutoPermissionActivity.USER_PERMISSION_ACTION_RESULT_CODE);
                })
                .build();

        return createPromiseDialog(offerGrantNoticeDialog);
    }

    public static PromiseDialog createInvalidDialog(){
        return INVALID_DIALOG;
    }
}
