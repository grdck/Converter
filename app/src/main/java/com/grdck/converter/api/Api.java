package com.grdck.converter.api;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Api {

    private static final String URL = "http://cbr.ru/scripts/XML_daily.asp";

    @NonNull
    public static String getCourses() throws IOException {
        final URL url = new URL(URL);

        final InputStream inputStream = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        String line = "";
        final StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            result.append(line);

        inputStream.close();
        return result.toString();
    }

}
