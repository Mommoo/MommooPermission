package com.mommoo.permission.utils.dialog;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.mommoo.library.toolkit.MommooThread;

import java.util.ArrayList;

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
 * @since 2017-06-04
 *
 */

public class SequenceDialogHandler {
    public interface Task {
        public void execute(boolean isDialogShown);
    }

    public static Promise showDialog(@NonNull PromiseDialog promiseDialog){
        return new Promise(promiseDialog).execute();
    }

    public static class Promise{
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
