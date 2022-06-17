package com.cherrydev.airsend.app.database;

import com.cherrydev.airsendcore.utils.RxHelper;
import com.cherrydev.common.MyResult;

public class DatabaseQuery {
    private Runnable runnable;
    private boolean runInBackground;

    public DatabaseQuery() {
    }

    private void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    private void setRunInBackground(boolean runInBackground) {
        this.runInBackground = runInBackground;
    }

    public static class Builder {
        private DatabaseQuery databaseQuery;
        private Runnable runnable;
        private boolean runInBackground;

        public Builder() {
            databaseQuery = new DatabaseQuery();
        }

        public Builder setQuery(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public Builder runInBackground() {
            runInBackground = true;
            return this;
        }

        public void run() {
            databaseQuery.setRunnable(runnable);
            databaseQuery.setRunInBackground(runInBackground);
            databaseQuery.run();
        }
    }


    private void run() {
        if (runInBackground) {
//            Observable.fromRunnable(runnable).subscribe(t -> {
//
//            }, throwable -> {
//
//            });

            RxHelper.submitToRx(() -> {
                runnable.run();
                return MyResult.success();
            });

        } else {
            runnable.run();
        }
    }
}
