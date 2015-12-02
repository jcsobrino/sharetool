package jcsobrino.tddm.uoc.sharetool.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.IntentExtraInfoEnum;
import jcsobrino.tddm.uoc.sharetool.common.LocationService;
import jcsobrino.tddm.uoc.sharetool.common.UtilFunctions;
import jcsobrino.tddm.uoc.sharetool.domain.impl.Tool;

/**
 * Panel de detalle de una herramienta. Se muestra posterior a la selección en el listado de herramientas
 */
public class ToolDetailsActivity extends AppCompatActivity {

    private TextView mDistanceToolTextView;
    private TextView mPricePerDayTextView;
    private TextView mTotalPriceTextView;
    private TextView mDescriptionTextView;
    private ImageView mImageToolImageView;
    private TextView mUserToolTextView;
    private Button mRentToolButton;

    private Tool mTool;
    private Long mToolId;
    private Integer mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_details);

        mUserToolTextView = (TextView) findViewById(R.id.userToolDataTextView);
        mImageToolImageView = (ImageView) findViewById(R.id.toolImageView);
        mDistanceToolTextView = (TextView) findViewById(R.id.distanceToolDataTextView);
        mTotalPriceTextView = (TextView) findViewById(R.id.totalPriceToolDataTextView);
        mPricePerDayTextView = (TextView) findViewById(R.id.pricePerDayToolDataTextView);
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionToolDataTextView);
        mRentToolButton = (Button) findViewById(R.id.rentToolButton);

        mRentToolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRentToolDialogConfirm();
            }
        });

        mTool = (Tool) getIntent().getSerializableExtra(IntentExtraInfoEnum.SELECTED_TOOL.name());
        mToolId = (Long) getIntent().getSerializableExtra(IntentExtraInfoEnum.SELECTED_TOOL_ID.name());
        mDays = (Integer) getIntent().getSerializableExtra(IntentExtraInfoEnum.SELECTED_FILTER_RENT_DAYS.name());

        Picasso.with(this).load(UtilFunctions.getImagePlaceholder(mToolId)).fit().into(mImageToolImageView);

        setTitle(mTool.getName());

        Location currentLocation = LocationService.getCurrentLocation();

        if (currentLocation != null) {
            mDistanceToolTextView.setText(String.format(getString(R.string.distance_kilometers), UtilFunctions.calculateDistance(mTool)));
        } else {
            mDistanceToolTextView.setText(getString(R.string.no_available_location));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserToolTextView.setText(mTool.getUser().getName());
        mDescriptionTextView.setText(mTool.getDescription());
        mPricePerDayTextView.setText(String.format(getString(R.string.tool_price), mTool.getPricePerDay()));
        mTotalPriceTextView.setText(mDays == null ? getString(R.string.no_available_rent_data) : String.format(getString(R.string.tool_price), mTool.getPricePerDay() * mDays));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBackParentActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        goBackParentActivity();
    }

    private void createRentToolDialogConfirm() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.rent_tool_title)
                .setMessage(R.string.rent_tool_confirm)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        goBackParentActivity();
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.rent_tool_message), mTool.getName()), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void goBackParentActivity() {
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }


}
