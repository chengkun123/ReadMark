package com.mycompany.readmark.detail;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.mycompany.readmark.R;

/**
 * Created by Lenovo on 2017/3/22.
 */
public class SavingDialogFragment extends DialogFragment {

    private OnPositiveClickListener mOnPositiveClickListener;


    public interface OnPositiveClickListener{
        void onPositiveClick();
    }

    public void setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener){
        mOnPositiveClickListener = onPositiveClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_saving_dialog, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mOnPositiveClickListener.onPositiveClick();
                            }
                        })
                .setNegativeButton("取消", null);

        return builder.create();
    }
}
