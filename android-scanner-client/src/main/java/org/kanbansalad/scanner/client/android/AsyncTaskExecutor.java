/**
 * Copyright 2013 Jim Hurne and Joseph Kramer
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
 */
package org.kanbansalad.scanner.client.android;

import org.kanbansalad.scanner.client.BackgroundAction;
import org.kanbansalad.scanner.client.BackgroundExecutor;

import android.app.Activity;
import android.os.AsyncTask;

/**
 * 
 */
public class AsyncTaskExecutor implements BackgroundExecutor {

    private final Activity context;

    public AsyncTaskExecutor(Activity context) {
        this.context = context;
    }

    @Override
    public <S> void execute(final BackgroundAction<S> action) {
        // XXX Replace the ProgressDialog with something else that follows
        // the current Android design recommendations (particularly since we
        // don't know what to set the title and message to).
        final ActivityIndicatorDialog activityIndicator = new ActivityIndicatorDialog();
        activityIndicator
                .show(context.getFragmentManager(), "ActivityIndicator");

        new AsyncTask<Void, Void, Result<S>>() {
            @Override
            protected Result<S> doInBackground(Void... params) {
                try {
                    return new Result<S>(action.doAction());
                } catch (Exception e) {
                    return new Result<S>(e);
                }
            }

            @Override
            protected void onPostExecute(Result<S> result) {
                activityIndicator.dismiss();
                if (result.e != null) {
                    action.onException(result.e);
                } else {
                    action.onComplete(result.result);
                }
            }
        }.execute();
    }

    private static class Result<T> {
        private final Exception e;
        private final T result;

        public Result(T result) {
            this.result = result;
            this.e = null;
        }

        public Result(Exception e) {
            this.result = null;
            this.e = e;
        }
    }
}
