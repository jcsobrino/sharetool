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

    private ApiService mAPI = ApiFactory.INSTANCE.getApi();
    private IUser mLoggedUser;

    private Location mCurrentLocation;
    private ListView mToolsListView;
    private ArrayAdapter mToolsListArraysAdapter;
    private FilterToolsDialog mFilterToolsDialog;
    private SwipeRefreshLayout mRefreshLayout;
    private FilterListTools filters = new FilterListTools();
    private View mEmptyListView;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

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
                new FindToolsAsyncTask().execute(filters);
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
                if (filters.getDateDaysFilter()) {
                    intent.putExtra(IntentExtraInfoEnum.TOOL_DAYS.name(), filters.getDays());
                }
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        Toast.makeText(getApplicationContext(), String.format(getString(R.string.welcome_user), mLoggedUser.getName()), Toast.LENGTH_LONG).show();
        new FindToolsAsyncTask().execute(filters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

/*
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
*/
        mSearchMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.setIconified(true);
                filters.setToolName(query);
                new FindToolsAsyncTask().execute(filters);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                filters.setToolName(null);
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

            filters = mFilterToolsDialog.getFilterListTools();
            new FindToolsAsyncTask().execute(filters);
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
                filters = params[0];
            }

            String toolName = filters.getToolName();
            Float maxPrice = filters.getPriceFilter() ? filters.getMaxPrice() : null;
            Float maxDistance = filters.getDistanceFilter() ? filters.getMaxDistance() : null;
            Float lat = mCurrentLocation != null ? (float) mCurrentLocation.getLatitude() : null;
            Float lng = mCurrentLocation != null ? (float) mCurrentLocation.getLongitude() : null;

            return mAPI.findTools(toolName, maxPrice, maxDistance, lat, lng, filters.getToolOrderEnum());

        }

        @Override
        protected void onPostExecute(List<? extends ITool> result) {
            super.onPostExecute(result);
            mToolsListArraysAdapter.addAll(result);
            progressDialog.dismiss();
        }

    }
}
