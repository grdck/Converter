package com.grdck.converter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grdck.converter.api.Api;
import com.grdck.converter.cache.Cache;
import com.grdck.converter.model.CourseList;
import com.grdck.converter.model.Valute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String EXCEPTION_LOG_TAG = "EXCEPTION";
    private static final String INITIAL_VALUTE_KEY = "initialValute";
    private static final String RESULT_VALUTE_KEY = "resultValute";

    private Cache cache;
    private Valute initialValute;
    private Valute resultValute;
    private Spinner initialValuteSpinner;
    private Spinner resultValuteSpinner;
    private TextView input;
    private String savedInitialValuteId;
    private String savedResultValuteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new Cache(this);
        setContentView(R.layout.activity_main);
        initialValuteSpinner = findViewById(R.id.from_spinner);
        resultValuteSpinner = findViewById(R.id.to_spinner);
        input = findViewById(R.id.sum_input);
        setupListeners();
        if (savedInstanceState == null) {
            tryToLoadNewCourses();
        }
    }

    private void setupListeners() {
        findViewById(R.id.convert_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                convert();
            }
        });
        initialValuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                try {
                    initialValute = Utils.getCoursesFromXml(cache.getCachedCourses()).getValutes().get(i);
                } catch (Exception e) {
                    Log.e(EXCEPTION_LOG_TAG, "Can't parse courses from cache", e);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {

            }
        });
        resultValuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                try {
                    resultValute = Utils.getCoursesFromXml(cache.getCachedCourses()).getValutes().get(i);
                } catch (Exception e) {
                    Log.e(EXCEPTION_LOG_TAG, "Can't parse courses from cache", e);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INITIAL_VALUTE_KEY, initialValute.getId());
        outState.putString(RESULT_VALUTE_KEY, resultValute.getId());
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInitialValuteId = savedInstanceState.getString(INITIAL_VALUTE_KEY);
        savedResultValuteId = savedInstanceState.getString(RESULT_VALUTE_KEY);
        tryToLoadNewCourses();
    }

    private void convert() {
        if (initialValute == null || resultValute == null || TextUtils.isEmpty(input.getText())) {
            return;
        }
        final double initialSum = Double.parseDouble(input.getText().toString());
        final double initialSumInRoubles = initialSum * Double.parseDouble(initialValute.getValue().replace(",", ".")) / initialValute.getNominal();
        final double result = initialSumInRoubles * resultValute.getNominal() / Double.parseDouble(resultValute.getValue().replace(",", "."));
        input.setText(String.valueOf(result));
        final int fromPosition = initialValuteSpinner.getSelectedItemPosition();
        initialValuteSpinner.setSelection(resultValuteSpinner.getSelectedItemPosition());
        resultValuteSpinner.setSelection(fromPosition);
    }

    private void tryToLoadNewCourses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final CourseList courses = getCourses();
                    final ArrayList<String> valuteNames = new ArrayList<>();
                    final List<Valute> valutes = courses.getValutes();
                    int selectedInitialValuteIndex = -1;
                    int selectedResultValuteIndex = -1;
                    for (int i = 0; i < valutes.size(); i++) {
                        final Valute valute = valutes.get(i);
                        valuteNames.add(valute.getName());
                        if (valute.getId().equals(savedInitialValuteId)) {
                            selectedInitialValuteIndex = i;
                        }
                        if (valute.getId().equals(savedResultValuteId)) {
                            selectedResultValuteIndex = i;
                        }
                    }
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, valuteNames);
                    setupAdapter(adapter, selectedInitialValuteIndex, selectedResultValuteIndex);
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, R.string.load_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void setupAdapter(final ArrayAdapter<String> adapter, final int selectedInitialValuteIndex, final int selectedResultValuteIndex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initialValuteSpinner.setAdapter(adapter);
                if (selectedInitialValuteIndex > -1) {
                    initialValuteSpinner.setSelection(selectedInitialValuteIndex);
                }
                resultValuteSpinner.setAdapter(adapter);
                if (selectedResultValuteIndex > -1) {
                    resultValuteSpinner.setSelection(selectedResultValuteIndex);
                }
            }
        });
    }

    private CourseList getCourses() throws IOException {
        try {
            final String courses = Api.getCourses();
            cache.putCourses(courses);
            return Utils.getCoursesFromXml(courses);
        } catch (Exception e) {
            Log.e(EXCEPTION_LOG_TAG, "Can't get courses from api", e);
        }
        try {
            return Utils.getCoursesFromXml(cache.getCachedCourses());
        } catch (Exception e) {
            Log.e(EXCEPTION_LOG_TAG, "Can't get courses from cache", e);
        }
        throw new IOException("Can't get courses");
    }

}
