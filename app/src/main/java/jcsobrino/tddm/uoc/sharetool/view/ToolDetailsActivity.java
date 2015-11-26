package jcsobrino.tddm.uoc.sharetool.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.IntentExtraInfoEnum;
import jcsobrino.tddm.uoc.sharetool.dto.ITool;
import jcsobrino.tddm.uoc.sharetool.dto.IUser;

public class ToolDetailsActivity extends AppCompatActivity {

    private TextView mNameToolTextView;
    private TextView mTotalPriceTextView;
    private TextView mDescriptionTextView;
    private ImageView mImageToolImageView;

    private ITool mTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_details);

        mImageToolImageView = (ImageView) findViewById(R.id.toolImageView);
        mNameToolTextView = (TextView) findViewById(R.id.nameToolTextView);
        mTotalPriceTextView = (TextView) findViewById(R.id.priceToolTextView);
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionToolTextView);

        mTool = (ITool) getIntent().getSerializableExtra(IntentExtraInfoEnum.TOOL.name());

        Picasso.with(this).load(String.format("http://lorempixel.com/600/300/?id=%s", mTool.getId())).into(mImageToolImageView);
        mNameToolTextView.setText(mTool.getName());
        mDescriptionTextView.setText(mTool.getDescription());
        mTotalPriceTextView.setText(mTool.getPricePerDay().toString());
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
