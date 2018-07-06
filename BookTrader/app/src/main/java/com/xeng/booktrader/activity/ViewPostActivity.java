package com.xeng.booktrader.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.xeng.booktrader.R;
import com.xeng.booktrader.model.Posting;
import com.xeng.booktrader.service.ParseService;
import com.xeng.booktrader.service.PostingService;

import java.util.HashMap;

public class ViewPostActivity extends AppCompatActivity {

    Context self = this;
    TextView tvBookName;
    TextView tvEdition;
    TextView tvPrice;
    TextView tvLocation;
    TextView tvPublisher;
    TextView tvAuthor;
    TextView tvISBN;
    TextView tvNotes;
    TextView tvSummary;
    TextView tvPoster;
    TextView tvSellerRating;

    String postingId;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        tvBookName = (TextView) findViewById(R.id.tvBookName);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvPublisher = (TextView) findViewById(R.id.tvPublisher);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvISBN = (TextView) findViewById(R.id.tvISBN);
        tvEdition = (TextView) findViewById(R.id.tvEdition);
        tvNotes = (TextView) findViewById(R.id.tvNotes);
        tvSummary = (TextView) findViewById(R.id.tvSummary);
        tvPoster = (TextView) findViewById(R.id.tvPoster);
        tvSellerRating = (TextView) findViewById(R.id.tvSellerRating);

        postingId = getIntent().getStringExtra("postingId");

        fetchPosting(postingId);
    }

    // Get Posting from backend and update view.
    //@params: postingId - objectId of Posting
    public void fetchPosting(final String postingId){

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        PostingService.getPostingById(postingId, new ParseService() {
            @Override
            public void onParseCallSuccess(Object result) {
                Posting posting = (Posting) result;

                tvPoster.setText(posting.getPoster().getFirstName() + " " + posting.getPoster().getLastName());
                tvSellerRating.setText("(" + posting.getPoster().getRating() + "/5)");
                tvBookName.setText(posting.getBook().getLongName());
                tvPrice.setText("$" + posting.getPrice().toString());
                tvLocation.setText(posting.getCity() + ", " + posting.getProvince());
                tvPublisher.setText(posting.getBook().getPublisher());
                tvAuthor.setText(posting.getBook().getAuthor());
                tvISBN.setText(posting.getBook().getISBN());

                if (posting.getBook().getEdition().equals(";")) {
                    tvEdition.setText("N/A");
                } else {
                    tvEdition.setText(posting.getBook().getEdition());
                }

                tvNotes.setText(posting.getNotes());
                tvSummary.setText(posting.getBook().getSummary());

                pDialog.dismiss();
            }

            @Override
            public void onParseCallError(ParseException e) {
                Toast.makeText(self, "Could not fetch Posting info. Please try again later.", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }

        });
    }
}
