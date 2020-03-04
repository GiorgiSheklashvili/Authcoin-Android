package com.authcoinandroid.ui.fragment.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.authcoinandroid.R;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class CameraImage extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    Button button;
    Button sendButton;
    ImageView imageView;
    byte[] byteArray;

    private View.OnClickListener sendCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.camera_image,
                container, false);

        button = (Button) rootView.findViewById(R.id.take_photo);
        imageView = (ImageView) rootView.findViewById(R.id.image_view);
        sendButton = (Button) rootView.findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        });

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                SharedPreferences.Editor editor = getActivity().getApplicationContext().getSharedPreferences("image", Context.MODE_PRIVATE).edit();
                editor.putString("imageByteArray", Arrays.toString(byteArray));
                editor.apply();
                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                imageView.setImageBitmap(bitmap);
                sendButton.setVisibility(View.VISIBLE);
                sendButton.setOnClickListener(sendCallback);
            }
        }
    }
    public void setSendButtonListener(View.OnClickListener sendCallback) {
        this.sendCallback = sendCallback;
    }

    public byte[] getByteArray() {
        return byteArray;
    }
}
