package com.xeng.booktrader.utility;

import com.xeng.booktrader.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sahajarora on 2017-10-28.
 */

public class Constants {

    // SEARCH FILTER NAMES
    public static String LOWER_PRICE_RANGE_FILTER_NAME = "lowerPriceRange";
    public static String UPPER_PRICE_RANGE_FILTER_NAME = "upperPriceRange";
    public static String LOWER_SELLER_RATING_FILTER_NAME = "lowerSellerRating";
    public static String UPPER_SELLER_RATING_FILTER_NAME = "upperSellerRating";
    public static String LOCATION_PROVINCE_FILTER_NAME = "province";
    public static String LOCATION_CITY_FILTER_NAME = "city";

    // SEARCH FILTER DISPLAY NAMES

    // PROVINCES
    public static ArrayList<String> provinces = new ArrayList<>(Arrays.asList("Ontario"));

    // CITIES
    public static ArrayList<String> cities = new ArrayList<>(Arrays.asList("Hamilton", "London", "Ottawa", "Oshawa", "Toronto"));

}
