package com.authcoinandroid.jobs;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class JobsScheduler {

    private static final int UPDATE_EIR_JOB_ID = 1;
    private static final int JOB_INTERVAL = 60 * 1000; // every minute, good enough for dev
    private Context context;

    public JobsScheduler(Context context) {
        this.context = context;
    }

    public void init() {
        this.scheduleJobs();
    }

    private void scheduleJobs() {
        scheduleJob(UPDATE_EIR_JOB_ID, new ComponentName(context.getPackageName(), EirUpdater.class.getName()));
    }

    private void scheduleJob(int jobId, ComponentName componentName) {
//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobInfo jobInfo = new JobInfo.Builder(jobId, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(JOB_INTERVAL)
                .build();
        JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(jobInfo);
    }
}