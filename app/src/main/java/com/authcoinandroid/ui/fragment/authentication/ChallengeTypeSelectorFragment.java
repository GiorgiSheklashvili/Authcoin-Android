package com.authcoinandroid.ui.fragment.authentication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.module.challenges.Challenges;

import java.util.ArrayList;
import java.util.Set;

public class ChallengeTypeSelectorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button selectChallengeButton;
    private ArrayAdapter<String> challengeAdapter;
    private Spinner challengeSpinner;
    private View.OnClickListener selectChallengeButtonListener;
    private String selectedChallenge;
    private TextView applicationName;
    private TextView applicationUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_fragment_select_challenge, container, false);
        ButterKnife.bind(this, view);

        Set<String> types = Challenges.getAllTypes();

        challengeAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(types));
        challengeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        challengeSpinner = (Spinner) view.findViewById(R.id.s_challenge);
        challengeSpinner.setAdapter(challengeAdapter);
        challengeSpinner.setOnItemSelectedListener(this);

        selectChallengeButton = (Button) view.findViewById(R.id.select_challenge);
        selectChallengeButton.setOnClickListener(selectChallengeButtonListener);

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

    public void setSelectChallengeButtonListener(View.OnClickListener selectChallengeButtonListener) {
        this.selectChallengeButtonListener = selectChallengeButtonListener;
    }

    public String getSelectedChallenge() {
        return selectedChallenge;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.selectedChallenge = challengeAdapter.getItem(position);
//        synchronized (VAProcessRunnable.lock) {
//            VAProcessRunnable.queue.add(new ChallengeTypeMessageResponse(challenge));
//            VAProcessRunnable.lock.notify();
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
