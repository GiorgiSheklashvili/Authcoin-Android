package com.authcoinandroid.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.IdentityService;
import com.authcoinandroid.ui.AuthCoinApplication;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.SUBMITTED;

public class EirUpdater extends JobService {

    public static final String LOG_TAG = "EirUpdater";

    @Override
    public boolean onStartJob(JobParameters params) {
        AuthCoinApplication application = (AuthCoinApplication) getApplication();
        List<EntityIdentityRecord> eirs = application.getEirRepository().findByStatus(SUBMITTED);
        IdentityService identityService = application.getIdentityService();
        Observable.fromIterable(eirs)
                .flatMap(eir -> identityService.updateEirStatusFromBc(eir).toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG, "Finished EIR update!");
                    }

                    @Override
                    public void onNext(Object o) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}