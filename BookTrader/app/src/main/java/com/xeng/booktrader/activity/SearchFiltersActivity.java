package com.xeng.booktrader.activity;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.xeng.booktrader.R;
import com.xeng.booktrader.model.SearchFilter;
import com.xeng.booktrader.utility.Common;
import com.xeng.booktrader.utility.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchFiltersActivity extends AppCompatActivity {

    Spinner spinnerLowerPriceRange, spinnerUpperPriceRange, spinnerLowerSellerRating, spinnerUpperSellerRating,
            spinnerCitySearch, spinnerProvinceSearch;

    private final int ANY_LOCATION_SPINNER_INDEX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filters);

        // Initialize view elements
        spinnerLowerPriceRange = (Spinner) findViewById(R.id.spinnerLowerPriceRange);
        spinnerUpperPriceRange = (Spinner) findViewById(R.id.spinnerUpperPriceRange);
        spinnerLowerSellerRating = (Spinner) findViewById(R.id.spinnerLowerSellerRating);
        spinnerUpperSellerRating = (Spinner) findViewById(R.id.spinnerUpperSellerRating);
        spinnerProvinceSearch = (Spinner) findViewById(R.id.spinnerProvinceSearch);
        spinnerCitySearch = (Spinner) findViewById(R.id.spinnerCitySearch);

        // Prepare list of prices
        ArrayList<Integer> prices = new ArrayList<>();
        for (int i = 0; i < 1000; i=i+10) {
            prices.add(i);
        }

         // Set spinner adapters
        ArrayAdapter lowerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, prices);
        lowerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter higherAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, prices);
        higherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLowerPriceRange.setAdapter(lowerAdapter);
        spinnerUpperPriceRange.setAdapter(higherAdapter);

        // Set Initial Values
        if (Common.getSharedInstance().getSearchFilters().size() > 0) {
            for (int i  = 0; i<Common.getSharedInstance().getSearchFilters().size(); i++) {

                // Lower Price Range
                if (Common.getSharedInstance().getSearchFilters().get(i).getName().equals(Constants.LOWER_PRICE_RANGE_FILTER_NAME)) {
                    int lowerPriceRange = Integer.parseInt(Common.getSharedInstance().getSearchFilters().get(i).getValue());
                    spinnerLowerPriceRange.setSelection(getPriceSpinnerIndexByValue(lowerPriceRange));
                }

                // Upper Price Range
                if (Common.getSharedInstance().getSearchFilters().get(i).getName().equals(Constants.UPPER_PRICE_RANGE_FILTER_NAME)) {
                    int upperPriceRange = Integer.parseInt(Common.getSharedInstance().getSearchFilters().get(i).getValue());
                    spinnerUpperPriceRange.setSelection(getPriceSpinnerIndexByValue(upperPriceRange));
                }

                // Lower Seller Rating
                if (Common.getSharedInstance().getSearchFilters().get(i).getName().equals(Constants.LOWER_SELLER_RATING_FILTER_NAME)) {
                    int rating = Integer.parseInt(Common.getSharedInstance().getSearchFilters().get(i).getValue());
                    spinnerLowerSellerRating.setSelection(getRatingSpinnerIndexByValue(rating));
                }

                // Upper Seller Rating
                if (Common.getSharedInstance().getSearchFilters().get(i).getName().equals(Constants.UPPER_SELLER_RATING_FILTER_NAME)) {
                    int rating = Integer.parseInt(Common.getSharedInstance().getSearchFilters().get(i).getValue());
                    spinnerUpperSellerRating.setSelection(getRatingSpinnerIndexByValue(rating));
                }

                // Province
                if (Common.getSharedInstance().getSearchFilters().get(i).getName().equals(Constants.LOCATION_PROVINCE_FILTER_NAME)) {
                    String province = Common.getSharedInstance().getSearchFilters().get(i).getValue();
                    spinnerProvinceSearch.setSelection(getProvincesSpinnerIndexByValue(province));
                }

                // City
                if (Common.getSharedInstance().getSearchFilters().get(i).getName().equals(Constants.LOCATION_CITY_FILTER_NAME)) {
                    String city = Common.getSharedInstance().getSearchFilters().get(i).getValue();
                    spinnerCitySearch.setSelection(getCitiesSpinnerIndexByValue(city));
                }
            }
        } else {
            spinnerUpperPriceRange.setSelection(prices.size()-1);
            spinnerUpperSellerRating.setSelection(4); // This will set value to 5
            spinnerProvinceSearch.setSelection(ANY_LOCATION_SPINNER_INDEX);
            spinnerCitySearch.setSelection(ANY_LOCATION_SPINNER_INDEX);
        }

    }

    // Returns index of item in price range spinner based on value that's passed in as parameter
    public int getPriceSpinnerIndexByValue(int value) {
        return value/10;
    }

    // Returns index of item in seller rating spinner based on value that's passed in as parameter
    public int getRatingSpinnerIndexByValue(int value) {
        int index = value - 1;
        return index >= 0 ? index : 0;
    }

    // Returns index of item in provinces spinner based on province value that's passed in as a parameter
    public int getProvincesSpinnerIndexByValue(String value) {
        String province = value.toLowerCase();
        for (int i = 0; i<Constants.provinces.size(); i++) {
            if (province.equals(Constants.provinces.get(i).toLowerCase())) {
                return i+1;
            }
        }

        return ANY_LOCATION_SPINNER_INDEX;
    }

    // Returns index of item in cities spinner based on province value that's passed in as a parameter
    public int getCitiesSpinnerIndexByValue(String value) {
        String city = value.toLowerCase();
        for (int i = 0; i<Constants.cities.size(); i++) {
            if (city.equals(Constants.cities.get(i).toLowerCase())) {
                return i+1;
            }
        }

        return ANY_LOCATION_SPINNER_INDEX;
    }

    // Apply Search filters: Clear Singleton's searchFilters list, create new list with the selected filters, fire the 'filtersChanged' event, and finish the activity.
    public void applyFilters(View view) {

        int lowerPriceRange =  (Integer) spinnerLowerPriceRange.getSelectedItem();
        int upperPriceRange =  (Integer) spinnerUpperPriceRange.getSelectedItem();

        if (upperPriceRange < lowerPriceRange) {
            int price = upperPriceRange;
            upperPriceRange = lowerPriceRange;
            lowerPriceRange = price;
        }

        int lowerSellerRating = Integer.parseInt((String) spinnerLowerSellerRating.getSelectedItem());
        int upperSellerRating = Integer.parseInt((String) spinnerUpperSellerRating.getSelectedItem());

        if (upperSellerRating < lowerSellerRating) {
            int price = upperSellerRating;
            upperSellerRating = lowerSellerRating;
            lowerSellerRating = price;
        }

        // Create Filters
        SearchFilter lowerPriceRangeFilter = new SearchFilter(Constants.LOWER_PRICE_RANGE_FILTER_NAME, lowerPriceRange+"", "$" + lowerPriceRange + " - $" + upperPriceRange);
        SearchFilter upperPriceRangeFilter = new SearchFilter(Constants.UPPER_PRICE_RANGE_FILTER_NAME, upperPriceRange+"", "$" + lowerPriceRange + " - $" + upperPriceRange);
        SearchFilter lowerSellerRatingFilter = new SearchFilter(Constants.LOWER_SELLER_RATING_FILTER_NAME, lowerSellerRating+"", "Seller Rating: " + lowerSellerRating + " - " + upperSellerRating);
        SearchFilter upperSellerRatingFilter = new SearchFilter(Constants.UPPER_SELLER_RATING_FILTER_NAME, upperSellerRating+"", "Seller Rating: " + lowerSellerRating + " - " + upperSellerRating);

        ArrayList<SearchFilter> filters = new ArrayList<>();
        filters.add(lowerPriceRangeFilter);
        filters.add(upperPriceRangeFilter);
        filters.add(lowerSellerRatingFilter);
        filters.add(upperSellerRatingFilter);

        if (spinnerProvinceSearch.getSelectedItemPosition() != ANY_LOCATION_SPINNER_INDEX) {
            SearchFilter provinceFilter = new SearchFilter(Constants.LOCATION_PROVINCE_FILTER_NAME, (String) spinnerProvinceSearch.getSelectedItem(), (String) spinnerProvinceSearch.getSelectedItem());
            filters.add(provinceFilter);
        }
        if (spinnerCitySearch.getSelectedItemPosition() != ANY_LOCATION_SPINNER_INDEX) {
            SearchFilter cityFilter = new SearchFilter(Constants.LOCATION_CITY_FILTER_NAME, (String) spinnerCitySearch.getSelectedItem(), (String) spinnerCitySearch.getSelectedItem());
            filters.add(cityFilter);
        }

        Common.getSharedInstance().clearSearchFilters();
        Common.getSharedInstance().setSearchFilters(filters);

        Intent eventIntent = new Intent(getString(R.string.event_filters_changed));
        LocalBroadcastManager.getInstance(this).sendBroadcast(eventIntent);

        finish();

//        Intent intent = new Intent( SearchFiltersActivity.this, SearchActivity.class );
//        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        startActivity(intent);
    }
}
