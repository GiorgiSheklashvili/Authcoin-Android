package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.activity.MainActivity;
import com.authcoinandroid.ui.adapter.ChallengeAdapter;
import com.authcoinandroid.util.AndroidUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.MINED;

public class ChallengeFragment extends Fragment {
    private final static String LOG_TAG = "ChallengeFragment";

    @BindView(R.id.s_eir)
    Spinner currentEir;
    @BindView(R.id.btn_new_challenge)
    FloatingActionButton newChallengeButton;
    @BindView(R.id.lv_challenges)
    ListView challengeList;

    public ChallengeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_fragment, container, false);
        ButterKnife.bind(this, view);

        AuthCoinApplication application = (AuthCoinApplication) getActivity().getApplication();
        List<EntityIdentityRecord> minedIdentities = application.getEirRepository().findByStatus(MINED);
        newChallengeButton.setOnClickListener(addChallengeListener(!minedIdentities.isEmpty()));
        currentEir.setEnabled(!minedIdentities.isEmpty());

        // Uses the toString method of EntityIdentityRecord to display value in ArrayAdapter
        ArrayAdapter<EntityIdentityRecord> currentEirAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.spinner_item_light, minedIdentities);
        currentEirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentEir.setAdapter(currentEirAdapter);
        currentEir.setOnItemSelectedListener(changeCurrentEirListener());

        return view;
    }

    private View.OnClickListener addChallengeListener(boolean minedEirsExist) {
        return listener -> {
            if (!minedEirsExist) {
                AndroidUtil.displayNotification(getContext(), getString(R.string.missing_identities_for_challenge));
            } else {
                ((MainActivity) getActivity()).applyFragment(NewChallengeFragment.class, true, true);
            }
        };
    }

    private AdapterView.OnItemSelectedListener changeCurrentEirListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EntityIdentityRecord currentEir = (EntityIdentityRecord) parent.getItemAtPosition(position);
                populateChallengeList(currentEir);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }


    private void populateChallengeList(EntityIdentityRecord eir) {
        try {
            ChallengeService challengeService = ((AuthCoinApplication) getActivity().getApplication()).getChallengeService();
            List<ChallengeRecord> challengeRecords = new ArrayList<>(challengeService.getByEirId(eir.getId()));

            ChallengeAdapter adapter = new ChallengeAdapter(getContext(), challengeRecords);
            challengeList.setAdapter(adapter);

            challengeList.setOnItemClickListener((parent, view, position, id) -> {
                ChallengeRecord challengeRecord = challengeRecords.get(position);
                Bundle bundle = new Bundle();
                bundle.putByteArray("challengeRecord", challengeRecord.getId());
                Toast.makeText(getContext(), Arrays.toString(challengeRecord.getVaeId()),Toast.LENGTH_SHORT).show();
                if (getContext() instanceof MainActivity) {
                    // TODO Open a new Fragment (perhaps separate ones for sent/received challenges?)
                    // ((MainActivity) getContext()).applyFullFragmentWithBundle(/* CrFragment */, bundle);
                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

//    This method got the CR-s from block-chain
//    private void getChallengeRecordsAndPopulateChallengeList(EntityIdentityRecord eir) {
//        List<ChallengeRecord> challengeRecords = new ArrayList<>();
//
//        ((AuthCoinApplication) getActivity().getApplication()).getChallengeService()
//                .getChallengeRecordsForEir(eir)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DisposableObserver<List<ChallengeRecord>>() {
//                    @Override
//                    public void onComplete() {
//                        ChallengeAdapter adapter = new ChallengeAdapter(getContext(), challengeRecords);
//                        challengeList.setAdapter(adapter);
//
//                        challengeList.setOnItemClickListener((parent, view, position, id) -> {
//                            ChallengeRecord challengeRecord = challengeRecords.get(position);
//                            Bundle bundle = new Bundle();
//                            bundle.putByteArray("challengeRecord", challengeRecord.getId());
//
//                            if (getContext() instanceof MainActivity) {
//                                // TODO Open a new Fragment (perhaps separate ones for sent/received challenges?)
//                                // ((MainActivity) getContext()).applyFullFragmentWithBundle(/* CrFragment */, bundle);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onNext(List<ChallengeRecord> nextChallengeRecords) {
//                        challengeRecords.addAll(nextChallengeRecords);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        AndroidUtil.displayNotification(getContext(), e.getMessage());
//                        Log.d(LOG_TAG, e.getMessage());
//                    }
//                });
//    }
}
