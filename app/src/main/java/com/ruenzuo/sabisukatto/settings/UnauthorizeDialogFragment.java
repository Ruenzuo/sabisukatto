package com.ruenzuo.sabisukatto.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class UnauthorizeDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener listener;

    public UnauthorizeDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Unauthorize application?")
                .setPositiveButton("Unauthorize", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onFragmentInteraction();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
