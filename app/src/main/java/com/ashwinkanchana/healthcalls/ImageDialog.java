package com.ashwinkanchana.healthcalls;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.ashwinkanchana.healthcalls.Constants.PREF_KEY_IMAGE;

public class ImageDialog extends DialogFragment {
    private String name;
    private ImageView imageView;
    private int index;
    private ImageDialogListener listener;
    private LoadData loadData;
    private ArrayList<Medication> medicationList;
    public ImageDialog(String name, int index) {
        this.name = name;
        this.index = index;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        loadData = new LoadData(getActivity());
        medicationList = loadData.load();
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_image, null);
        imageView = view.findViewById(R.id.image_view);
        SharedPreferences pref = getActivity().getSharedPreferences(name,MODE_PRIVATE);
        String previouslyEncodedImage = pref.getString(PREF_KEY_IMAGE, "");

        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageView.setImageBitmap(bitmap);
        }


        //build dialog
        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(medicationList.get(index).getName())
                .setMessage(medicationList.get(index).getUnit())
                .setView(view)
                .setPositiveButton("OK",null)
                .setNeutralButton("Remove", null)
                .setNegativeButton("Edit", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {

            Button button1 = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            button1.setOnClickListener(view1 -> {
                listener.option(1,index);
                dialog.dismiss();


            });
            Button button2 = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            button2.setOnClickListener(view1 -> {
                listener.option(2,index);
                dialog.dismiss();


            });
            Button button3 = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            button3.setOnClickListener(view1 -> {
                listener.option(3,index);
                dialog.dismiss();


            });


        });
        return dialog;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ImageDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogListener");
        }
    }

    public interface ImageDialogListener {
        void option(int option,int index);
    }
}
