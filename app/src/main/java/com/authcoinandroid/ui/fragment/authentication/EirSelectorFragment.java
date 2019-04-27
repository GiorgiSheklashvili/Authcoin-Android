package com.authcoinandroid.ui.fragment.authentication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.ui.AuthCoinApplication;

import java.util.List;

public class EirSelectorFragment extends Fragment {

    private Button startAuthentication;
    private ArrayAdapter<EntityIdentityRecord> eirAdapter;
    private Spinner eirSpinner;
    private View.OnClickListener nextButtonListener;
    private TextView applicationName;
    private TextView applicationUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_fragment_select_eir, container, false);
        ButterKnife.bind(this, view);

        EirRepository eirRepository = ((AuthCoinApplication) getActivity().getApplication()).getEirRepository();
        List<EntityIdentityRecord> eirs = eirRepository.findAll();

        eirAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, eirs);
        eirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eirSpinner = (Spinner) view.findViewById(R.id.s_identity);
        eirSpinner.setAdapter(eirAdapter);

        startAuthentication = (Button) view.findViewById(R.id.authenticate);
        startAuthentication.setOnClickListener(nextButtonListener);

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

    public void setNextButtonListener(View.OnClickListener o) {
        this.nextButtonListener = o;
    }

    public EntityIdentityRecord getSelectedEir() {
        return (EntityIdentityRecord) eirSpinner.getSelectedItem();
    }

}


