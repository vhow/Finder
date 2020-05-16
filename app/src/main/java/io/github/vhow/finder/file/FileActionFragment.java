package io.github.vhow.finder.file;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.vhow.finder.App;
import com.gmail.dailyefforts.filemanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileActionFragment extends BottomSheetDialogFragment {

    private static final String TAG = "FileActionFragment";

    private static final String KEY_FILE = "file";

    public static DialogFragment newInstance(File file) {
        final DialogFragment fragment = new FileActionFragment();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_FILE, file);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);

        final File file = (File) getArguments().getSerializable(KEY_FILE);
        if (file == null) {
            Log.e(TAG, "onCreateDialog: file is null.");
            dismiss();
            return dialog;
        }
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_file_action_list, null);
        final TextView tv = view.findViewById(R.id.tvFileName);
        tv.setText(file.getName());
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        final Context context = getContext();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
        final FileActionAdapter adapter = new FileActionAdapter(file);
        adapter.setOnPickedListener(new OnActionPickedListener() {
            @Override
            public void onPicked(FileActionEvent.Action action) {
                App.getBus().post(new FileActionEvent(action, file));
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);

        dialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = dialog.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }

        return dialog;
    }

    interface OnActionPickedListener {
        void onPicked(FileActionEvent.Action action);
    }

    static class FileActionAdapter extends RecyclerView.Adapter<FileActionAdapter.ViewHolder> {

        private final File file;
        private List<FileActionEvent.Action> list = new ArrayList<>();
        private OnActionPickedListener listener;

        FileActionAdapter(File file) {
            this.file = file;
            list.add(FileActionEvent.Action.DELETE);
            list.add(FileActionEvent.Action.RENAME);
            list.add(FileActionEvent.Action.SHARE);
            list.add(FileActionEvent.Action.COPY_TO);
            list.add(FileActionEvent.Action.MOVE_TO);
            list.add(FileActionEvent.Action.INFO);
        }

        void setOnPickedListener(OnActionPickedListener listener) {
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_file_action_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final FileActionEvent.Action action = list.get(position);
            switch (action) {
                case DELETE:
                    holder.icon.setImageResource(R.drawable.ic_action_delete);
                    holder.name.setText(R.string.delete);
                    break;
                case RENAME:
                    holder.icon.setImageResource(R.drawable.ic_action_rename);
                    holder.name.setText(R.string.rename);
                    break;
                case SHARE:
                    holder.icon.setImageResource(R.drawable.ic_action_share);
                    holder.name.setText(R.string.share);
                    break;
                case COPY_TO:
                    holder.icon.setImageResource(R.drawable.ic_action_copy_to);
                    holder.name.setText(R.string.copy_to);
                    break;
                case MOVE_TO:
                    holder.icon.setImageResource(R.drawable.ic_action_move_to);
                    holder.name.setText(R.string.move_to);
                    break;
                case INFO:
                    holder.icon.setImageResource(R.drawable.ic_action_info);
                    holder.name.setText(R.string.info);
                    break;
            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileActionAdapter.this.listener.onPicked(action);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public ImageView icon;
            public TextView name;

            ViewHolder(View itemView) {
                super(itemView);
                this.view = itemView;
                this.icon = itemView.findViewById(R.id.tvActionIcon);
                this.name = itemView.findViewById(R.id.tvActionName);
            }
        }
    }
}
