package com.xeng.booktrader.activity;


        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.provider.MediaStore;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.Toast;

        import com.parse.ParseException;
        import com.parse.ParseUser;
        import com.xeng.booktrader.R;
        import com.xeng.booktrader.service.ParseResponseHandler;
        import com.xeng.booktrader.service.ParseAPIService;

        import java.util.HashMap;

public class PostBookActivity extends AppCompatActivity {

    Context self = this;
    EditText etISBN;
    EditText etPrice;
    EditText etNotes;
    EditText etEdition;
    Double price;

    Spinner spinnerCondition, spinnerProvince, spinnerCity;

    ProgressDialog pDialog;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView img00, clickedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_book);

        etISBN = (EditText) findViewById(R.id.etISBN);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etNotes = (EditText) findViewById(R.id.etNotes);
        etEdition = (EditText) findViewById(R.id.etEdition);
        spinnerCondition = (Spinner) findViewById(R.id.spinnerCondition);
        spinnerProvince = (Spinner) findViewById(R.id.spinnerProvince);
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        img00 = (ImageView) findViewById(R.id.img00);
        clickedImageView = img00;

    }

    public void attemptPostBook(View view) {
        if (validate()) {

            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Posting...");
            pDialog.setCancelable(false);
            pDialog.show();

            HashMap<String,Object> params = new HashMap<>();

            String isbn = etISBN.getText().toString();
            String notes = etNotes.getText().toString();
            String edition = etEdition.getText().toString();

            // Call createPosting cloud function
            ParseAPIService.createPosting(isbn, edition, notes, spinnerCondition.getSelectedItem().toString(), spinnerCity.getSelectedItem().toString(),
                    spinnerProvince.getSelectedItem().toString(), price.toString(), ParseUser.getCurrentUser().getObjectId(), new ParseResponseHandler() {
                @Override
                public void onParseCallSuccess(Object result) {
                    Toast.makeText(self, "Your book has been posted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent( PostBookActivity.this, MainActivity.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

                    pDialog.dismiss();
                    self.startActivity( intent );
                }

                @Override
                public void onParseCallError(ParseException e) {
                    pDialog.dismiss();
                    Toast.makeText(self, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public boolean validate() {
        boolean isValid = true;

        if (etISBN.getText().length() == 10 || etISBN.getText().length() == 13 ) {
            isValid = true;
        } else {
            isValid = false;
            etISBN.setError("ISBN should be 10 or 13 digits.");
        }


        String priceString = etPrice.getText().toString();
        try {
            price = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            isValid = false;
            etPrice.setError("Please enter a valid price.");
        }

        return isValid;
    }

    public void addPicture(View view) {
        clickedImageView = (ImageView) view;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            clickedImageView.setImageBitmap(imageBitmap);
        }
    }
}
