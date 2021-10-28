package com.infernodevelopers.sendsms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class sheet_data_adapter extends ArrayAdapter<sheet_items_data> {
    Context context;
    public sheet_data_adapter(@NonNull Context context, List<sheet_items_data> sheet_data) {
        super(context, 0, sheet_data);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        sheet_items_data sheet_data = (sheet_items_data) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sms_list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = convertView.findViewById(R.id.name);
        TextView number = convertView.findViewById(R.id.number);
        TextView message = convertView.findViewById(R.id.message_body);
        // Populate the data into the template view using the data object
        name.setText(sheet_data.name);
        number.setText(sheet_data.number);
        message.setText(sheet_data.message);
        // Return the completed view to render on screen
        return convertView;
    }
}
