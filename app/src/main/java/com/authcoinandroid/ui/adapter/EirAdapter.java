package com.authcoinandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;

import java.util.List;

public class EirAdapter extends ArrayAdapter<EntityIdentityRecord> {

    public EirAdapter(Context context, List<EntityIdentityRecord> eirs) {
        super(context, 0, eirs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EntityIdentityRecord eir = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }
        TextView eirAlias = (TextView) convertView.findViewById(R.id.tv_title);
        eirAlias.setTag(position);
        eirAlias.setText(eir.getKeyStoreAlias());

        TextView eirStatus = (TextView) convertView.findViewById(R.id.tv_subtitle);
        eirStatus.setTag(position);
        eirStatus.setText(eir.getStatus().toString());
        return convertView;
    }
}
