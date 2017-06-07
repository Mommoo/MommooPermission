package com.mommoo.permission.utils.dialog;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mommoo.library.toolkit.MommooThread;
import com.mommoo.library.widget.alert.AlertDialog;

import java.util.ArrayList;

/**
 * Created by mommoo on 2017-06-04.
 */

public class SequenceDialogHandler {
    public interface Task {
        public void execute(boolean isDialogShown);
    }

    public SequenceDialogHandler(){

    }

    public Promise showDialog(@NonNull PromiseDialog promiseDialog){
        return new Promise(promiseDialog).execute();
    }

    public class Promise{
        private final ArrayList<Task> taskList = new ArrayList<>();
        private final PromiseDialog promiseDialog;
        private Promise nextPromise;

        private Promise(PromiseDialog promiseDialog){
            this.promiseDialog = promiseDialog;
        }

        private Promise execute(){
            this.promiseDialog.show();
            waitForDialogToFinish();
            return this;
        }

        private Promise waitForDialogToFinish(){

            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    while(promiseDialog.isShowing()) MommooThread.sleep(100);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    boolean isDialogShown = promiseDialog != PromiseDialogFactory.createInvalidDialog();
                    taskList.forEach(task -> task.execute(isDialogShown));
                    if (nextPromise != null) nextPromise.execute();
                }
            }.execute();

            return this;
        }

        public Promise then(Task task){
            taskList.add(task);
            return this;
        }

        public Promise showDialog(PromiseDialog promiseDialog){
            nextPromise = new Promise(promiseDialog);
            return nextPromise;
        }
    }
}
