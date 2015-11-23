package jcsobrino.tddm.uoc.sharetool.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.ApiFactory;
import jcsobrino.tddm.uoc.sharetool.dto.ITool;
import jcsobrino.tddm.uoc.sharetool.dto.IUser;
import jcsobrino.tddm.uoc.sharetool.service.ApiService;
import jcsobrino.tddm.uoc.sharetool.view.form.FilterListTools;

public class ListActivity extends AppCompatActivity implements NoticeDialogListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SearchView.OnQueryTextListener {

    private ApiService mAPI = ApiFactory.INSTANCE.getApi();
    private IUser mLoggedUser;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private ListView mToolsListView;
    private ArrayAdapter mToolsListArraysAdapter;
    private FilterToolsDialog mFilterToolsDialog;
    private LocationRequest mLocationRequest;
    private SwipeRefreshLayout mRefreshLayout;
    private FilterListTools filters;
    private View mEmptyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        buildGoogleApiClient();
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mToolsListView = (ListView) findViewById(R.id.listaView);

        mFilterToolsDialog = new FilterToolsDialog(this, this);

/*
        LayoutInflater inflater = LayoutInflater.from(this);
        mEmptyListView = inflater.inflate(R.layout.tool_list_empty, null);
*/

        mToolsListView.setEmptyView(findViewById(R.id.emptyListTextView));

        mToolsListArraysAdapter = new ToolArrayAdapter(this, new ArrayList<ITool>());
        mToolsListView.setAdapter(mToolsListArraysAdapter);

        mLoggedUser = (IUser) getIntent().getSerializableExtra(LoginActivity.LOGGED_USER);

        Toast.makeText(getApplicationContext(), String.format("Bienvenido, %s", mLoggedUser.getName()), Toast.LENGTH_LONG).show();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FindToolsAsyncTask().execute(filters);
                mRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                mFilterToolsDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        new FindToolsAsyncTask().execute();

    }

    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
    }

    @Override
    public void onConnectionSuspended(int var1) {

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String text) {

        //new FindToolsAsyncTask().execute();
        return false;
    }

    public void onConnectionFailed(ConnectionResult var1) {
        Log.e(ListActivity.class.toString(), String.format("Connection failed. ConnectionResult: %s", var1.toString()));
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
            progressDialog = ProgressDialog.show(ListActivity.this, "Cargando", "Espere mientras se cargan las herramientas..", true, true);
            mToolsListArraysAdapter.clear();
        }

        @Override
        protected List<? extends ITool> doInBackground(FilterListTools... params) {

            FilterListTools filters = new FilterListTools();

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
