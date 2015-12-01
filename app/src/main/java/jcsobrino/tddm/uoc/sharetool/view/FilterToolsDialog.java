package jcsobrino.tddm.uoc.sharetool.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.ToolOrderEnum;
import jcsobrino.tddm.uoc.sharetool.common.UtilFunctions;
import jcsobrino.tddm.uoc.sharetool.view.form.FilterListTools;

/**
 * Representa el diálogo desde donde el usuario filtra u ordena el listado de herramientas
 * Created by JoséCarlos on 22/11/2015.
 */
public final class FilterToolsDialog extends AlertDialog {

    // contiene los valores de filtros y ordenación seleccionados
    private FilterListTools filterListTools = new FilterListTools();
    
    // permite notificar cambios en los filtros
    private NoticeDialogListener mListener;
    
    // elementos de UI 
    private final CheckBox mDistanceCheckBox;
    private final CheckBox mPriceCheckBox;
    private final CheckBox mDateDaysCheckBox;
    private final RadioButton mNearerToolsFirstOrder;
    private final RadioButton mCheaperToolsFirstOrder;
    private final SeekBar mDistanceSeekBar;
    private final EditText mDateEditText;
    private final EditText mDaysEditText;
    private final EditText mPriceEditText;
    private final TextView mDistanceTextView;

    public FilterToolsDialog(Context context, final NoticeDialogListener listener) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.filter_tool_list, null);
        setView(v);

        mListener = listener;

        UtilFunctions.DATE_FORMAT.setLenient(true);

        mDistanceCheckBox = (CheckBox) v.findViewById(R.id.distanciaCheckBox);
        mPriceCheckBox = (CheckBox) v.findViewById(R.id.precioCheckBox);
        mDateDaysCheckBox = (CheckBox) v.findViewById(R.id.fechaCheckBox);

        mNearerToolsFirstOrder = (RadioButton) v.findViewById(R.id.cercanos);
        mCheaperToolsFirstOrder = (RadioButton) v.findViewById(R.id.baratos);

        mDistanceSeekBar = (SeekBar) v.findViewById(R.id.distanciaSeekBar);
        mDistanceTextView = (TextView) v.findViewById(R.id.distanceTextView);
        mDateEditText = (EditText) v.findViewById(R.id.fechaEditText);
        mDaysEditText = (EditText) v.findViewById(R.id.diasEditText);
        mPriceEditText = (EditText) v.findViewById(R.id.precioEditText);

        mDistanceSeekBar.setEnabled(false);
        mDistanceTextView.setEnabled(false);
        mDateEditText.setEnabled(false);
        mDaysEditText.setEnabled(false);
        mPriceEditText.setEnabled(false);

        mDistanceTextView.setText(String.format(context.getString(R.string.seekbar_distance_kilometers), 0));
        
        mDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDistanceTextView.setText(String.format(FilterToolsDialog.this.getContext().getString(R.string.seekbar_distance_kilometers), progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDistanceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDistanceSeekBar.setEnabled(isChecked);
                mDistanceTextView.setEnabled(isChecked);
            }
        });

        mDateDaysCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDateEditText.setEnabled(isChecked);
                mDaysEditText.setEnabled(isChecked);
            }
        });

        mPriceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPriceEditText.setEnabled(isChecked);
            }
        });

        setButton(BUTTON_POSITIVE, context.getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = FilterToolsDialog.this.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (validateFields()) {
                            copyFilterValuesFromUIToBean();
                            if (listener != null) {
                                listener.onDialogPositiveClick(FilterToolsDialog.this);
                            }
                            FilterToolsDialog.this.dismiss();
                        }
                    }
                });
            }
        });


        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                copyFilterValuesFromBeanToUI();
                if (listener != null) {
                    listener.onDialogNegativeClick(FilterToolsDialog.this);
                }
            }
        });


        copyFilterValuesFromBeanToUI();
    }

    private void copyFilterValuesFromBeanToUI() {

        mDistanceCheckBox.setChecked(filterListTools.getDistanceFilter());
        mPriceCheckBox.setChecked(filterListTools.getPriceFilter());
        mDateDaysCheckBox.setChecked(filterListTools.getDateDaysFilter());

        mNearerToolsFirstOrder.setChecked(filterListTools.getToolOrderEnum() == ToolOrderEnum.NEAR_TOOL);
        mCheaperToolsFirstOrder.setChecked(filterListTools.getToolOrderEnum() == ToolOrderEnum.MIN_PRICE);

        mDistanceSeekBar.setProgress(filterListTools.getMaxDistance() != null ? filterListTools.getMaxDistance().intValue() : 0);
        mDateEditText.setText(filterListTools.getDate() != null ? UtilFunctions.DATE_FORMAT.format(filterListTools.getDate()) : null);
        mDaysEditText.setText(filterListTools.getDays() != null ? filterListTools.getDays().toString() : null);
        mPriceEditText.setText(filterListTools.getMaxPrice() != null ? filterListTools.getMaxPrice().toString() : null);
    }

    private void copyFilterValuesFromUIToBean() {

        filterListTools.setDistanceFilter(mDistanceCheckBox.isChecked());
        filterListTools.setPriceFilter(mPriceCheckBox.isChecked());
        filterListTools.setDateDaysFilter(mDateDaysCheckBox.isChecked());

        filterListTools.setToolOrderEnum(mNearerToolsFirstOrder.isChecked() ? ToolOrderEnum.NEAR_TOOL : ToolOrderEnum.MIN_PRICE);

        filterListTools.setMaxDistance((float) mDistanceSeekBar.getProgress());
        filterListTools.setMaxPrice(TextUtils.isEmpty(mPriceEditText.getText()) ? null : Float.parseFloat(mPriceEditText.getText().toString()));
        filterListTools.setDays(TextUtils.isEmpty(mDaysEditText.getText()) ? null : Integer.parseInt(mDaysEditText.getText().toString()));
        try {
            filterListTools.setDate(TextUtils.isEmpty(mDateEditText.getText()) ? null : UtilFunctions.DATE_FORMAT.parse(mDateEditText.getText().toString()));
        } catch (ParseException pe) {
            Toast.makeText(getContext(), String.format("Error parsing date %s", mDateEditText.getText()), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateFields() {

        boolean result = true;

        if (mDateDaysCheckBox.isChecked()) {

            if (UtilFunctions.isEmpty(mDaysEditText.getText())) {

                mDaysEditText.setError(getContext().getString(R.string.error_field_required));
                mDaysEditText.requestFocus();
                result = false;
            }

            if (UtilFunctions.isEmpty(mDateEditText.getText())) {

                mDateEditText.setError(getContext().getString(R.string.error_field_required));
                mDateEditText.requestFocus();
                result = false;
            }

            if (!UtilFunctions.isEmpty(mDateEditText.getText())) {
                try {
                    UtilFunctions.DATE_FORMAT.parse(mDateEditText.getText().toString());
                } catch (ParseException pe) {
                    mDateEditText.setError(getContext().getString(R.string.format_error));
                    mDateEditText.requestFocus();
                    result = false;
                }
            }
        }

        if (mPriceCheckBox.isChecked()) {

            if (UtilFunctions.isEmpty(mPriceEditText.getText())) {

                mPriceEditText.setError(getContext().getString(R.string.error_field_required));
                mPriceEditText.requestFocus();
                result = false;

            } else if (mPriceEditText.getText().equals(".")) {

                mPriceEditText.setError(getContext().getString(R.string.format_error));
                mPriceEditText.requestFocus();
                result = false;

            }
        }

        return result;
    }

    public FilterListTools getFilterListTools() {
        return filterListTools;
    }
}
