package jcsobrino.tddm.uoc.sharetool.view;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.IntentExtraInfoEnum;
import jcsobrino.tddm.uoc.sharetool.common.LocationService;
import jcsobrino.tddm.uoc.sharetool.common.UtilFunctions;
import jcsobrino.tddm.uoc.sharetool.domain.Tool;
import jcsobrino.tddm.uoc.sharetool.service.impl.ApiServiceImpl;

public class ToolDetailsActivity extends AppCompatActivity {

    private TextView mDistanceToolTextView;
    private TextView mPricePerDayTextView;
    private TextView mTotalPriceTextView;
    private TextView mDescriptionTextView;
    private ImageView mImageToolImageView;

    private Tool mTool;
    private Long mToolId;
    private Integer mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_details);

        mImageToolImageView = (ImageView) findViewById(R.id.toolImageView);
        mDistanceToolTextView = (TextView) findViewById(R.id.distanceToolDataTextView);
        mTotalPriceTextView = (TextView) findViewById(R.id.totalPriceToolDataTextView);
        mPricePerDayTextView = (TextView) findViewById(R.id.pricePerDayToolDataTextView);
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionToolDataTextView);

        mTool = (Tool) getIntent().getSerializableExtra(IntentExtraInfoEnum.TOOL.name());
        mToolId = (Long) getIntent().getSerializableExtra(IntentExtraInfoEnum.TOOL_ID.name());
        mDays = (Integer) getIntent().getSerializableExtra(IntentExtraInfoEnum.TOOL_DAYS.name());

        Picasso.with(this).load(String.format("http://lorempixel.com/600/300/?id=%s", mToolId)).fit().into(mImageToolImageView);

        setTitle(mTool.getName());

        Location currentLocation = LocationService.getCurrentLocation();

        if(currentLocation != null) {
            mDistanceToolTextView.setText(String.format("%.2f km", UtilFunctions.calculateDistance(mTool)));
        }else {
            mDistanceToolTextView.setText("<Localización no disponible>");
        }

        mDescriptionTextView.setText(mTool.getDescription());
        mPricePerDayTextView.setText(String.format("%.2f €", mTool.getPricePerDay()));
        mTotalPriceTextView.setText(mDays == null ? "<período de alquiler no indicado>" : String.format("%.2f €", mTool.getPricePerDay() * mDays));

        //overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/

/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
*/


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }


}
