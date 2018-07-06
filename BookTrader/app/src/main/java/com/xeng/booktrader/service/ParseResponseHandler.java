package com.xeng.booktrader.service;


import com.parse.ParseException;

/**
 * Parse Service provides callbacks on successful or unsuccessful Parse Cloud calls.
 * Created by sahajarora1286 on 04-10-2017.
 */

public interface ParseResponseHandler {
    void onParseCallSuccess(Object result);
    void onParseCallError(ParseException e);
}

