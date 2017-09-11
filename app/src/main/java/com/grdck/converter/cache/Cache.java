package com.grdck.converter.cache;

import android.content.Context;
import android.content.SharedPreferences;

public class Cache {

    private static final String PREFERENCES_KEY = "ConverterPreferences";
    private static final String COURSES_KEY = "Courses";
    private final SharedPreferences preferences;

    public Cache(final Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public void putCourses(final String courses) {
        preferences.edit().putString(COURSES_KEY, courses).apply();
    }

    public String getCachedCourses() {
        return preferences.getString(COURSES_KEY, null);
    }

}
