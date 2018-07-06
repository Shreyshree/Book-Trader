package com.xeng.booktrader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.xeng.booktrader.R;
import com.xeng.booktrader.model.Posting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sahajarora on 2017-10-07.
 */

public class PostingsArrayAdapter extends ArrayAdapter<Posting> {

    public PostingsArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PostingsArrayAdapter(Context context, int resource, ArrayList<Posting> postings) {
        super(context, resource, postings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.postings_item, null);
        }

        Posting posting = (Posting) getItem(position);

        if (posting != null) {
            TextView tvBookName = (TextView) v.findViewById(R.id.tvBookName);
            TextView tvEdition = (TextView) v.findViewById(R.id.tvEdition);
            TextView tvPublisher = (TextView) v.findViewById(R.id.tvPublisher);
            TextView tvAuthor = (TextView) v.findViewById(R.id.tvAuthor);
            TextView tvPosterRating = (TextView) v.findViewById(R.id.tvPosterRating);
            TextView tvCondition = (TextView) v.findViewById(R.id.tvCondition);
            TextView tvPosterName = (TextView) v.findViewById(R.id.tvPosterName);
            TextView tvLocation = (TextView) v.findViewById(R.id.tvLocation);
            TextView tvPrice = (TextView) v.findViewById(R.id.tvPrice);

            if (posting.getBook() != null) {
                tvBookName.setText(posting.getBook().getName());

                if (posting.getBook().getEdition().equals(";")) {
                    tvEdition.setText("N/A");
                } else {
                    tvEdition.setText(posting.getBook().getEdition());
                }

                tvPublisher.setText(posting.getBook().getPublisher());
                tvAuthor.setText(posting.getBook().getAuthor());
            }

            tvCondition.setText(posting.getCondition());
            tvPosterName.setText(posting.getPoster().getFirstName() + " " + posting.getPoster().getLastName());
            tvPosterRating.setText("(" + posting.getPoster().getRating() + "/5)");
            tvLocation.setText(posting.getCity() + ", " + posting.getProvince());
            tvPrice.setText("$" + posting.getPrice());

        }

        return v;
    }


}
