package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.service.qtum.model.SendRawTransactionResponse;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.activity.MainActivity;
import com.authcoinandroid.util.AndroidUtil;
import com.authcoinandroid.util.Util;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.MINED;

public class NewChallengeFragment extends Fragment {
    private final static String LOG_TAG = "NewChallengeFragment";

    @BindView(R.id.s_eir_verifier)
    Spinner verifierEir;
    @BindView(R.id.et_eir_target)
    TextView targetEirId;
    @BindView(R.id.s_challenge_type)
    Spinner challengeType;

    public NewChallengeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_challenge_fragment, container, false);
        ButterKnife.bind(this, view);

        AuthCoinApplication application = (AuthCoinApplication) getActivity().getApplication();
        List<EntityIdentityRecord> minedIdentities = application.getEirRepository().findByStatus(MINED);

        // Uses the toString method of EntityIdentityRecord to display value in ArrayAdapter
        ArrayAdapter<EntityIdentityRecord> verifierEirAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, minedIdentities);
        verifierEirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        verifierEir.setAdapter(verifierEirAdapter);

        List<String> challengeTypes = new ArrayList<>(Challenges.getAllTypes());
        ArrayAdapter<String> challengeTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, challengeTypes);
        challengeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        challengeType.setAdapter(challengeTypeAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_send_challenge})
    void onSendChallenge(View view) {
        EntityIdentityRecord verifier = (EntityIdentityRecord) verifierEir.getSelectedItem();
        String targetId = String.valueOf(targetEirId.getText());
        String challengeTypeValue = String.valueOf(challengeType.getSelectedItem());

        createAndSendChallenge(verifier, targetId, challengeTypeValue);
    }

    private void createAndSendChallenge(EntityIdentityRecord verifier, String targetId, String challengeTypeValue) {
        ((AuthCoinApplication) getActivity().getApplication()).getIdentityService()
                .getEirById(targetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<EntityIdentityRecord>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onNext(EntityIdentityRecord target) {
                        try {
                            String[] challengetypeStringArray;
                            challengetypeStringArray = new String[]{"Sign Content", "MFA Sign Content & Facial recognition"};
                            if(challengeTypeValue.equals("Sign Content")){
                                challengetypeStringArray = new String[]{"Sign Content"};
                            }
                            for(int i=0; i<challengetypeStringArray.length; i++){
                            Challenge challenge = Challenges.get(challengetypeStringArray[i]);
                            byte[] crId = Util.generateId();
                            ChallengeRecord challengeRecord =  new ChallengeRecord(crId, Util.generateId(), challenge.getType(), challenge.getContent(getContext()), verifier, target);

                            ((AuthCoinApplication) getActivity().getApplication())
                                    .getChallengeService()
                                    .saveChallengeToBc(((AuthCoinApplication)getActivity().getApplication()).getWalletService().getReceiveKey(), challengeRecord)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableObserver<SendRawTransactionResponse>() {
                                        @Override
                                        public void onNext(SendRawTransactionResponse response) {
                                            Log.d(LOG_TAG, response.getTxid() + " - " + response.getResult());
                                            ((MainActivity) getActivity()).applyFragment(ChallengeFragment.class, false, false);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            AndroidUtil.displayNotification(getContext(), e.getMessage());
                                            Log.d(LOG_TAG, e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                            }
                        } catch (Exception e) {
                            AndroidUtil.displayNotification(getContext(), e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidUtil.displayNotification(getContext(), e.getMessage());
                        Log.d(LOG_TAG, e.getMessage());
                    }
                });
    }
}
