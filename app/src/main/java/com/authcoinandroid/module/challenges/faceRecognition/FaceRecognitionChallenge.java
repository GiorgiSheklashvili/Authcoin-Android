package com.authcoinandroid.module.challenges.faceRecognition;

import android.content.Context;
import android.content.SharedPreferences;

import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.ui.fragment.authentication.CameraImage;


public class FaceRecognitionChallenge implements Challenge {
    @Override
    public String getType() {
        return "Face Recognition";
    }

    @Override
    public byte[] getContent(Context context) {
        SharedPreferences settings = context.getSharedPreferences("image", Context.MODE_PRIVATE);
        String stringArray = settings.getString("imageByteArray", null);
        byte[] array;
        if (stringArray != null) {
            String[] split = stringArray.substring(1, stringArray.length()-1).split(", ");
            array = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                array[i] = Byte.parseByte(split[i]);
            }
            return array;
        }
        return null;
    }
}
