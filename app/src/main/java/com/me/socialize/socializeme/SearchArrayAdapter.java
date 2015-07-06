package com.me.socialize.socializeme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Toni on 9.6.2015..
 * custom array adapter za listview
 */
public class SearchArrayAdapter extends ArrayAdapter<Person> {

    Context context;
    int layoutResourceId;
    ArrayList<Person> data = null;

    public SearchArrayAdapter(Context context, int layoutResourceId, ArrayList<Person> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SearchHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SearchHolder();
            holder.textViewPersonName = (TextView)row.findViewById(R.id.textViewPersonName);
            holder.textViewPersonCountry = (TextView)row.findViewById(R.id.textViewCountry);

            row.setTag(holder);
        }
        else
        {
            holder = (SearchHolder)row.getTag();
        }

        Person person = data.get(position);
        holder.textViewPersonName.setText(person.m_Firstname + " " + person.m_Lastname);
        holder.textViewPersonCountry.setText(person.m_Country);

        return row;
    }

    static class SearchHolder
    {
        TextView textViewPersonName;
        TextView textViewPersonCountry;
    }
}
