package jcsobrino.tddm.uoc.sharetool.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.ToolOrderEnum;
import jcsobrino.tddm.uoc.sharetool.view.form.FilterListTools;

/**
 * Created by JoséCarlos on 22/11/2015.
 */
public class FilterToolsDialog extends AlertDialog {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private FilterListTools filterListTools = new FilterListTools();

    private NoticeDialogListener mListener;

    private final CheckBox distanceCheckBox;
    private final CheckBox priceCheckBox;
    private final CheckBox dateDaysCheckBox;
    private final RadioButton nearerToolsFirstOrder;
    private final RadioButton cheaperToolsFirstOrder;
    private final RadioGroup orderRadioGroup;
    private final SeekBar distanceSeekBar;
    private final EditText dateEditText;
    private final EditText daysEditText;
    private final EditText priceEditText;
    private final TextView distanceTextView;

    public FilterToolsDialog(Context context, final NoticeDialogListener listener) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.filter_tool_list, null);
        setView(v);

        mListener = listener;

        distanceCheckBox = (CheckBox) v.findViewById(R.id.distanciaCheckBox);
        priceCheckBox = (CheckBox) v.findViewById(R.id.precioCheckBox);
        dateDaysCheckBox = (CheckBox) v.findViewById(R.id.fechaCheckBox);

        nearerToolsFirstOrder = (RadioButton) v.findViewById(R.id.cercanos);
        cheaperToolsFirstOrder = (RadioButton) v.findViewById(R.id.baratos);
        orderRadioGroup = (RadioGroup) v.findViewById(R.id.orderRadioGroup);

        distanceSeekBar = (SeekBar) v.findViewById(R.id.distanciaSeekBar);
        distanceTextView = (TextView) v.findViewById(R.id.distanceTextView);
        dateEditText = (EditText) v.findViewById(R.id.fechaEditText);
        daysEditText = (EditText) v.findViewById(R.id.diasEditText);
        priceEditText = (EditText) v.findViewById(R.id.precioEditText);

        distanceSeekBar.setEnabled(false);
        distanceTextView.setEnabled(false);
        dateEditText.setEnabled(false);
        daysEditText.setEnabled(false);
        priceEditText.setEnabled(false);


        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceTextView.setText(String.format("%d kms",progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        distanceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                distanceSeekBar.setEnabled(isChecked);
                distanceTextView.setEnabled(isChecked);
            }
        });

        dateDaysCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dateEditText.setEnabled(isChecked);
                daysEditText.setEnabled(isChecked);
            }
        });

        priceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                priceEditText.setEnabled(isChecked);
            }
        });

        setButton(BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                copyFilterValuesFromUIToBean();
                if (listener != null) {
                    listener.onDialogPositiveClick(FilterToolsDialog.this);
                }
            }
        });

        setButton(BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {

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

        distanceCheckBox.setChecked(filterListTools.getDistanceFilter());
        priceCheckBox.setChecked(filterListTools.getPriceFilter());
        dateDaysCheckBox.setChecked(filterListTools.getDateDaysFilter());

        nearerToolsFirstOrder.setChecked(filterListTools.getToolOrderEnum() == ToolOrderEnum.NEAR_TOOL);
        cheaperToolsFirstOrder.setChecked(filterListTools.getToolOrderEnum() == ToolOrderEnum.MIN_PRICE);

        distanceSeekBar.setProgress(filterListTools.getMaxDistance() != null ? filterListTools.getMaxDistance().intValue() : 0);
        dateEditText.setText(filterListTools.getDate() != null ? sdf.format(filterListTools.getDate()) : null);
        daysEditText.setText(filterListTools.getDays() != null ? filterListTools.getDays().toString() : null);
        priceEditText.setText(filterListTools.getMaxPrice() != null ? filterListTools.getMaxPrice().toString() : null);
    }

    private void copyFilterValuesFromUIToBean() {

        filterListTools.setDistanceFilter(distanceCheckBox.isChecked());
        filterListTools.setPriceFilter(priceCheckBox.isChecked());
        filterListTools.setDateDaysFilter(dateDaysCheckBox.isChecked());

        filterListTools.setToolOrderEnum(nearerToolsFirstOrder.isChecked() ? ToolOrderEnum.NEAR_TOOL : ToolOrderEnum.MIN_PRICE);

        filterListTools.setMaxDistance((float)distanceSeekBar.getProgress());
        filterListTools.setMaxPrice(TextUtils.isEmpty(priceEditText.getText()) ? null : Float.parseFloat(priceEditText.getText().toString()));
        filterListTools.setDays(TextUtils.isEmpty(daysEditText.getText()) ? null : Integer.parseInt(daysEditText.getText().toString()));
        try {
            filterListTools.setDate(TextUtils.isEmpty(dateEditText.getText()) ? null : sdf.parse(dateEditText.getText().toString()));
        } catch (ParseException pe) {
            Toast.makeText(getContext(), String.format("Error parsing date %s", dateEditText.getText()), Toast.LENGTH_SHORT).show();
        }
    }

    public FilterListTools getFilterListTools() {
        return filterListTools;
    }
}
