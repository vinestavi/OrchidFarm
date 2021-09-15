package com.vina.orchidfarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ControlDialog extends AppCompatDialogFragment {

    private EditText meja1, meja2;
    private CreateDialogListener listener;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.control_dialog, null);

        builder.setView(view)
                .setTitle("Set Threshold")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sMeja1 = meja1.getText().toString();
                        String sMeja2 = meja2.getText().toString();
                        listener.post(sMeja1,sMeja2);
                    }
                });

        meja1 = view.findViewById(R.id.et_meja1);
        meja2 = view.findViewById(R.id.et_meja2);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CreateDialogListener) context;
        } catch (ClassCastException e) {

        }
    }

    public interface CreateDialogListener {
        void post(String meja1, String meja2);
    }
}
