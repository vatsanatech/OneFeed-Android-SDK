package com.onefeedsdk.job;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 17-July-2018
 * Time: 15:50
 */
public abstract class BaseJob extends Job {

    protected BaseJob(Params params) {
        super(params);
    }

    @Override
    protected int getRetryLimit() {
        return 2;
    }
}
