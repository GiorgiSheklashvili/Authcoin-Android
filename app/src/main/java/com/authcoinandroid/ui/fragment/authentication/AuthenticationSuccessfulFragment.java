package com.authcoinandroid.ui.fragment.authentication;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.module.messaging.UserAuthenticatedMessage;
import com.authcoinandroid.util.AndroidUtil;
import org.spongycastle.util.encoders.Hex;

public class AuthenticationSuccessfulFragment extends Fragment {

    private UserAuthenticatedMessage result;
    private TextView targetCr;
    private TextView verifierCr;
    private TextView applicationName;
    private TextView applicationUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_fragment_authentication_successful, container, false);
        ButterKnife.bind(this, view);

        targetCr = (TextView) view.findViewById(R.id.tv_target);
        verifierCr = (TextView) view.findViewById(R.id.tv_verifier);

        targetCr.setText(Hex.toHexString(result.getTargetChallengeRecord().getId()));
        verifierCr.setText(Hex.toHexString(result.getVerifierChallengeRecord().getId()));

        Uri uri = getActivity().getIntent().getData();
        applicationName = (TextView) view.findViewById(R.id.tv_app_name);
        applicationUrl = (TextView) view.findViewById(R.id.tv_app_url);
        applicationName.setText("" + uri.getQueryParameter("appName"));
        applicationUrl.setText("" + uri.getQueryParameter("serverUrl"));

        return view;
    }

    @OnClick(R.id.ll_target_wrapper)
    public void onTargetWrapperClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Target CR", targetCr.getText()));
        AndroidUtil.displayNotification(getContext(), "Target CR copied");
    }

    @OnClick(R.id.ll_verifier_wrapper)
    public void onVerifierWrapperClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Verifier CR", verifierCr.getText()));
        AndroidUtil.displayNotification(getContext(), "Verifier CR copied");
    }

    public void setResult(UserAuthenticatedMessage result) {
        this.result = result;
    }
}
