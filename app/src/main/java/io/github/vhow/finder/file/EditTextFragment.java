package io.github.vhow.finder.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gmail.dailyefforts.filemanager.R;

import org.apache.commons.io.FilenameUtils;

public class EditTextFragment extends DialogFragment {

    private static final String TAG = "EditTextFragment";

    private static final String MSG_RES_ID = "msg_res_id";
    private static final String OK_RES_ID = "ok_res_id";
    private static final String HINT = "hint";

    private OnSubmitListener mOnSubmitListener;

    public static EditTextFragment newInstance(int msgResId, int okResId, @Nullable String hint) {
        final EditTextFragment fragment = new EditTextFragment();
        final Bundle args = new Bundle();
        args.putInt(MSG_RES_ID, msgResId);
        args.putInt(OK_RES_ID, okResId);
        if (hint != null) {
            args.putString(HINT, hint);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnSubmitListener(OnSubmitListener listener) {
        mOnSubmitListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_edit_dialog, null);
        final EditText et = view.findViewById(R.id.editText);
        final Bundle args = getArguments();
        final String oldName = args.getString(HINT, null);
        if (oldName != null) {
            et.setText(oldName);
            final String baseName = FilenameUtils.getBaseName(oldName);
            et.setSelection(0, baseName.length());
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setMessage(args.getInt(MSG_RES_ID));
        builder.setPositiveButton(args.getInt(OK_RES_ID, R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnSubmitListener.onSubmit(String.valueOf(et
                                .getText()));
                    }
                });
        builder.setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    interface OnSubmitListener {
        void onSubmit(String name);
    }

}
