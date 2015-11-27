package jcsobrino.tddm.uoc.sharetool.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.dto.ITool;

/**
 * Created by JoséCarlos on 14/11/2015.
 */
public class ToolArrayAdapter<T extends ITool> extends ArrayAdapter<T> {

    private Context mContext;

    public ToolArrayAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            listItemView = inflater.inflate(
                    R.layout.tool_list_item,
                    parent,
                    false);
        }

        //Obteniendo instancias de los elementos
        TextView nameTool = (TextView) listItemView.findViewById(R.id.nameToolListItem);
        TextView distanceTool = (TextView) listItemView.findViewById(R.id.distanceToolListItem);
        TextView pricePerDayTool = (TextView) listItemView.findViewById(R.id.pricePerDayToolListItem);
        ImageView imageTool = (ImageView) listItemView.findViewById(R.id.imageToolListItem);


        //Obteniendo instancia de la Tarea en la posición actual
        ITool tool = getItem(position);

        nameTool.setText(tool.getName());
        distanceTool.setText(formatDistanceInKilometers(tool.getDistanceInKilometers()));
        pricePerDayTool.setText(String.format("%.2f €", tool.getPricePerDay()));
        Picasso.with(mContext).load(String.format("http://lorempixel.com/600/300/?id=%s", tool.getId())).fit().into(imageTool);

        //Devolver al ListView la fila creada
        return listItemView;

    }

    private String formatDistanceInKilometers(final Float distanceInKilometers) {

        if (distanceInKilometers == null) {
            return "<Desconocido>";
        }

        return String.format("%.2f km", distanceInKilometers);

    }
}
