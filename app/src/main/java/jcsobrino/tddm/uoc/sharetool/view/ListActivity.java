package jcsobrino.tddm.uoc.sharetool.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.ApiFactory;
import jcsobrino.tddm.uoc.sharetool.common.IntentExtraInfoEnum;
import jcsobrino.tddm.uoc.sharetool.common.LocationService;
import jcsobrino.tddm.uoc.sharetool.dto.ITool;
import jcsobrino.tddm.uoc.sharetool.dto.IUser;
import jcsobrino.tddm.uoc.sharetool.service.ApiService;
import jcsobrino.tddm.uoc.sharetool.view.form.FilterListTools;

public class ListActivity extends AppCompatActivity implements NoticeDialogListener {

    public static final long MS_BETWEEN_TWO_CALLS = 2000;
    private ApiService mAPI = ApiFactory.INSTANCE.getApi();
    private IUser mLoggedUser;

    private Location mCurrentLocation;
    private ListView mToolsListView;
    private ArrayAdapter mToolsListArraysAdapter;
    private FilterToolsDialog mFilterToolsDialog;
    private SwipeRefreshLayout mRefreshLayout;
    private FilterListTools mFilters = new FilterListTools();
    private String mQuerySearchTools = "";
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    long mLastQuerySubmit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        startService(new Intent(this, LocationService.class));

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mToolsListView = (ListView) findViewById(R.id.listaView);
        mFilterToolsDialog = new FilterToolsDialog(this, this);
        mToolsListView.setEmptyView(findViewById(R.id.emptyListTextView));
        mToolsListArraysAdapter = new ToolArrayAdapter(this, new ArrayList<ITool>());
        mToolsListView.setAdapter(mToolsListArraysAdapter);
        mLoggedUser = (IUser) getIntent().getSerializableExtra(IntentExtraInfoEnum.LOGGED_USER.name());
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FindToolsAsyncTask().execute(mFilters);
                mRefreshLayout.setRefreshing(false);
            }
        });

        mToolsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ITool selectedTool = (ITool) parent.getAdapter().getItem(position);
                Intent intent = new Intent(ListActivity.this, ToolDetailsActivity.class);
                intent.putExtra(IntentExtraInfoEnum.TOOL.name(), selectedTool);
                intent.putExtra(IntentExtraInfoEnum.TOOL_ID.name(), selectedTool.getId());
                if (mFilters.getDateDaysFilter()) {
                    intent.putExtra(IntentExtraInfoEnum.TOOL_DAYS.name(), mFilters.getDays());
                }
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        Toast.makeText(getApplicationContext(), String.format(getString(R.string.welcome_user), mLoggedUser.getName()), Toast.LENGTH_LONG).show();
        new FindToolsAsyncTask().execute(mFilters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //debido a un bug en Android este método se llama dos veces seguidas con el mismo parámetro
                if(mLastQuerySubmit+MS_BETWEEN_TWO_CALLS < System.currentTimeMillis()) {

                    mQuerySearchTools = query;
                    mFilters.setToolName(mQuerySearchTools);
                    new FindToolsAsyncTask().execute(mFilters);
                    mLastQuerySubmit = System.currentTimeMillis();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mFilters.setToolName(null);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.location:
                mCurrentLocation = LocationService.getCurrentLocation();
                if (mCurrentLocation != null) {
                    Toast.makeText(getApplicationContext(), String.format("Lat: %.3f, Lng: %.3f", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_available_location, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.filter:
                mFilterToolsDialog.show();
                return true;
            case R.id.logout:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDialogPositiveClick(Dialog dialog) {

        if (dialog == mFilterToolsDialog) {

            mFilters = mFilterToolsDialog.getFilterListTools();
            mFilters.setToolName(mQuerySearchTools);
            new FindToolsAsyncTask().execute(mFilters);
        }
    }

    @Override
    public void onDialogNegativeClick(Dialog dialog) {

    }

    private class FindToolsAsyncTask extends AsyncTask<FilterListTools, Integer, List<? extends ITool>> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ListActivity.this, getString(R.string.loading), getString(R.string.wating_loading_tools), true, true);
            mToolsListArraysAdapter.clear();
        }

        @Override
        protected List<? extends ITool> doInBackground(FilterListTools... params) {

            if (params != null && params.length > 0 && params[0] != null) {
                mFilters = params[0];
            }

            String toolName = mFilters.getToolName();
            Float maxPrice = mFilters.getPriceFilter() ? mFilters.getMaxPrice() : null;
            Float maxDistance = mFilters.getDistanceFilter() ? mFilters.getMaxDistance() : null;
            Float lat = mCurrentLocation != null ? (float) mCurrentLocation.getLatitude() : null;
            Float lng = mCurrentLocation != null ? (float) mCurrentLocation.getLongitude() : null;

            return mAPI.findTools(toolName, maxPrice, maxDistance, lat, lng, mFilters.getToolOrderEnum());

        }

        @Override
        protected void onPostExecute(List<? extends ITool> result) {
            super.onPostExecute(result);
            mToolsListArraysAdapter.addAll(result);
            progressDialog.dismiss();
        }

    }
}
