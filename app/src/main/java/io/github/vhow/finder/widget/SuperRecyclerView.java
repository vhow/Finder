package io.github.vhow.finder.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class SuperRecyclerView extends RecyclerView {

    public static final String TAG = SuperRecyclerView.class.getSimpleName();

    private View mEmptyView;

    private final AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public SuperRecyclerView(Context context) {
        super(context);
    }

    public SuperRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void checkIfEmpty() {
        if (mEmptyView == null || getAdapter() == null) {
            return;
        }
        final boolean isEmpty = getAdapter().getItemCount() == 0;
        mEmptyView.setVisibility(isEmpty ? VISIBLE : GONE);
        setVisibility(isEmpty ? GONE : VISIBLE);
        if (isEmpty) {
            mEmptyView.setAlpha(0);
            mEmptyView.animate().alpha(1).setDuration(600).start();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }
}
