package com.authcoinandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChallengeAdapter extends ArrayAdapter<ChallengeRecord> {

    public ChallengeAdapter(Context context, List<ChallengeRecord> challengeRecords) {
        super(context, 0, challengeRecords);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChallengeRecord challengeRecord = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }
        TextView challengeType = (TextView) convertView.findViewById(R.id.tv_title);
        challengeType.setTag(position);
        challengeType.setText(challengeRecord.getType());

        TextView challengeTimestamp = (TextView) convertView.findViewById(R.id.tv_subtitle);
        challengeTimestamp.setTag(position);
        challengeTimestamp.setText(String.valueOf(
                new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(challengeRecord.getTimestamp())))
        );
        return convertView;
    }
}
