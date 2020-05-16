package io.github.vhow.finder.search;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.github.vhow.finder.App;
import io.github.vhow.finder.FileOperator;
import com.gmail.dailyefforts.filemanager.R;
import io.github.vhow.finder.event.OpenDirEvent;
import io.github.vhow.finder.file.DelConfirmDiaFragment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private static final String TAG = SearchResultFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int URL_LOADER = 0;
    private final String mTimeOrder;
    private final String mNameOrder;
    private final String mSizeOrder;
    public String[] mFromColumns = {
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE
    };
    public int[] mToFields = {
            R.id.name, R.id.time, R.id.size
    };
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ListView mListView;
    private SimpleCursorAdapter mAdapter;
    private LoaderManager loaderManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String[] projection = new String[]{
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE
    };
    private String selection = MediaStore.Files.FileColumns.MIME_TYPE + " NOT NULL AND "
            + MediaStore.Files.FileColumns.SIZE + " > 0 AND "
            + MediaStore.Files.FileColumns.DISPLAY_NAME + " NOT LIKE '.%'";
    private String mCurrentSortOrder;
    private String mKey;

    public SearchResultFragment() {
        // Required empty public constructor
        mTimeOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
        mNameOrder = MediaStore.Files.FileColumns.DISPLAY_NAME;
        mSizeOrder = MediaStore.Files.FileColumns.SIZE + " DESC";
        mCurrentSortOrder = mTimeOrder;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(String param1, String param2) {
        final SearchResultFragment fragment = new SearchResultFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                mCurrentSortOrder = mNameOrder;
                loaderManager.restartLoader(0, null, this);
                return true;
            case R.id.action_sort_by_time:
                mCurrentSortOrder = mTimeOrder;
                loaderManager.restartLoader(0, null, this);
                return true;
            case R.id.action_sort_by_size:
                mCurrentSortOrder = mSizeOrder;
                loaderManager.restartLoader(0, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRefreshing(final boolean refreshing) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                final Activity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed();
                }
                return false;
            }
        });
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1);
            mParam2 = arguments.getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called with: " + "inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        loaderManager = getLoaderManager();
        loaderManager.initLoader(URL_LOADER, null, this);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        final Context context = view.getContext();
        final int color1 = ActivityCompat.getColor(context, R.color.light_green);
        final int color2 = ActivityCompat.getColor(context, R.color.purple);
        final int color3 = ActivityCompat.getColor(context, R.color.light_blue);
        mSwipeRefreshLayout.setColorSchemeColors(color1, color2, color3);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ActivityCompat.getColor(context, R.color.swipeBg));
        mSwipeRefreshLayout.setEnabled(false);
        mListView = view.findViewById(R.id.list);
        mListView.setEmptyView(view.findViewById(R.id.viewEmpty));
        registerForContextMenu(mListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick() called with: " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
                open(position);
            }
        });
        mAdapter = new SimpleCursorAdapter(
                view.getContext(),                // Current context
                R.layout.fragment_search_file_item,  // Layout for a single row
                null,                // No Cursor yet
                mFromColumns,        // Cursor columns to use
                mToFields,           // Layout fields to use
                0                    // No flags
        );

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.name:
                        final TextView nameTv = (TextView) view;
                        final String name = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                        nameTv.setText(name);
                        return true;
                    case R.id.time:
                        final TextView timeTv = (TextView) view;
                        final long time = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                        timeTv.setText(DateUtils.getRelativeTimeSpanString(time * 1000));
                        return true;
                    case R.id.size:
                        final TextView sizeTv = (TextView) view;
                        final long len = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                        sizeTv.setText(Formatter.formatFileSize(getActivity(), len));
                        return true;
                }

                return false;
            }
        });
        mListView.setAdapter(mAdapter);
        setRefreshing(true);
        return view;
    }

    private void open(int position) {
        final File file = getFile(position);
        if (file.exists()) {
            FileOperator.getInstance().tryOpen(getActivity(), file);
        }
    }

    private File getFile(int position) {
        final Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
        return new File(path);
    }

    private void share(int position) {
        FileOperator.getInstance().tryShare(getActivity(), getFile(position));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "onDetach() called with: " + "");
        if (mListView != null) {
            unregisterForContextMenu(mListView);
        }
    }

//    public void hideSoftKeyboard() {
//        View view = getActivity().getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getActivity()
//                    .getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
//        }
//    }

//    public void showSoftKeyboard(View view) {
//        if (view.requestFocus()) {
//            InputMethodManager imm = (InputMethodManager) getActivity()
//                    .getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//        }
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.file_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        Log.d(TAG, "onContextItemSelected() called with: " + info.id + ", " + info.position);
        switch (item.getItemId()) {
            case R.id.action_open:
                open(info.position);
                return true;
            case R.id.action_share:
                share(info.position);
                return true;
            case R.id.action_delete:
                del(info.position);
                return true;
            case R.id.action_open_folder:
                final File file = getFile(info.position);
                getActivity().onBackPressed();
                App.getBus().post(new OpenDirEvent(file));
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void del(int pos) {
        final File file = getFile(pos);
        tryDel(file);
    }

    private void tryDel(final File file) {
        if (DelConfirmDiaFragment.needTip()) {
            final CharSequence[] arr = new CharSequence[1];
            arr[0] = file.getName();
            final DelConfirmDiaFragment fragment = DelConfirmDiaFragment.newInstance(arr);
            fragment.setListener(new DelConfirmDiaFragment.DelConfirmDialogListener() {
                @Override
                public void onConfirm() {
                    new FileWorker().execute(file);
                }

                @Override
                public void onCancel() {
                }
            });
            fragment.show(getFragmentManager(), "");
        } else {
            new FileWorker().execute(file);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.d(TAG, "onCreateLoader() called with: " + "id = [" + id + "], bundle = [" + bundle + "] " + mKey);
        final Uri uri = MediaStore.Files.getContentUri("external");
        String selection = this.selection;
        String[] selectionArgs = null;
        if (mKey != null && mKey.length() > 0) {
            selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String[]{"%" + mKey.trim() + "%"};
        }
        setRefreshing(true);
        return new CursorLoader(getActivity(), uri, this.projection,
                selection, selectionArgs, mCurrentSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange() called with: " + "newText = [" + newText + "]");
        setRefreshing(true);
        mKey = newText;
        loaderManager.restartLoader(0, null, this);
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class FileWorker extends AsyncTask<File, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            setRefreshing(false);
        }

        @Override
        protected Boolean doInBackground(File... params) {
            try {
                FileUtils.forceDelete(params[0]);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
    }
}
