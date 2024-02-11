package com.cherrydev.airsend.app.database;

import io.github.romansj.core.utils.MyResult;
import io.github.romansj.core.utils.TaskRunner;

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
            TaskRunner.submit(() -> {
                runnable.run();
                return MyResult.success();
            });

        } else {
            runnable.run();
        }
    }
}
