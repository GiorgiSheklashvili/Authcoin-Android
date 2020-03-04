package com.authcoinandroid.ui.fragment.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenges;
import org.spongycastle.util.encoders.Hex;

import static android.support.v4.content.ContextCompat.getColor;

public class SignatureFragment extends Fragment {

    private View.OnClickListener signCallback;
    private ChallengeResponseRecord challengeResponse;
    private TextView challengeResponseView;
    private TextView applicationName;
    private TextView applicationUrl;
    private EditText lifespan;
    private TextView validSignature;
    private TextView textViewDays;
    private String challengeType;
    private Button signButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        challengeType = getArguments().getString("challengeType");
        View view = inflater.inflate(R.layout.aa_fragment_signature, container, false);
        ButterKnife.bind(this, view);

        lifespan = (EditText) view.findViewById(R.id.et_lifespan);
        textViewDays = (TextView) view.findViewById(R.id.tv_enter_days);
        if(challengeType.equals("MFA Sign Content & Facial recognition")){
            lifespan.setVisibility(View.INVISIBLE);
            textViewDays.setVisibility(View.INVISIBLE);
        }
        ChallengeRecord challengeRecord = challengeResponse.getChallenge();
        EntityIdentityRecord targetEir = challengeRecord.getTarget();
        boolean isValid = Challenges.getVerifier(challengeRecord.getType()).verify(targetEir, challengeRecord.getChallenge(), challengeResponse.getResponse());

        validSignature = (TextView) view.findViewById(R.id.tv_valid_signature);
        validSignature.setText(isValid ? R.string.valid : R.string.invalid);
        validSignature.setTextColor(isValid
                ? getColor(getContext(), R.color.textColorSuccess)
                : getColor(getContext(), R.color.textColorError));

        challengeResponseView = (TextView) view.findViewById(R.id.tv_challenge_response);
        challengeResponseView.setText(Hex.toHexString(challengeResponse.getResponse()));

        signButton = (Button) view.findViewById(R.id.sign_button);
        signButton.setOnClickListener(signCallback);

        Uri uri = getActivity().getIntent().getData();
        applicationName = (TextView) view.findViewById(R.id.tv_app_name);
        applicationUrl = (TextView) view.findViewById(R.id.tv_app_url);
        applicationName.setText("" + uri.getQueryParameter("appName"));
        applicationUrl.setText("" + uri.getQueryParameter("serverUrl"));

        return view;
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        getActivity().finish();
    }

    public int getLifespan() {
        return Integer.parseInt(lifespan.getText().toString());
    }

    public void setApproveSignatureListener(View.OnClickListener signCallback) {
        this.signCallback = signCallback;
    }

    public void setChallengeResponse(ChallengeResponseRecord challengeResponse) {
        this.challengeResponse = challengeResponse;
    }
}
