package com.example.studymate;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
public class AddActivity extends AppCompatActivity {

    int type = 1; // Hard-coded type for demonstration
    EditText title, course, description, myTime, myDate;
    MyCoreDatabase myDataset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Initialize UI components
        title = findViewById(R.id.title_text);
        course = findViewById(R.id.course_text);
        description = findViewById(R.id.description_text);
        myDate = findViewById(R.id.date_text);
        myTime = findViewById(R.id.time_text);

        // Customize the ActionBar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF018786")));
        }
        // Initialize database helper
        myDataset = new MyCoreDatabase(this);
        // Set up the DatePickerDialog when clicking the myDate EditText
        myDate.setOnClickListener(v -> showDatePickerDialog());
        // Set up the TimePickerDialog when clicking the myTime EditText
        myTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it in the EditText
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    myDate.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show TimePickerDialog with 24-hour format
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Set the time in the EditText in the format HH:mm
                    @SuppressLint("DefaultLocale") String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    myTime.setText(selectedTime);
                }, hour, minute, true); // 'true' for 24-hour format

        timePickerDialog.show();
    }


    public void doSave(View view) {
        // Define date and time formatters
        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .toFormatter();

        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ofPattern("HH:mm"))
                .toFormatter();

        LocalDate localDate = null;
        LocalTime localTime = null;

        // Validate and parse date and time
        if (myDate.getText().toString().length() != 0 && myTime.getText().toString().length() != 0) {
            if (!myDate.getText().toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(getApplicationContext(), "Date not in proper format", Toast.LENGTH_SHORT).show();
                return;
            } else {
                localDate = LocalDate.parse(myDate.getText().toString(), dateFormatter);
            }

            if (!myTime.getText().toString().matches("\\d{2}:\\d{2}")) {
                Toast.makeText(getApplicationContext(), "Time not in proper format", Toast.LENGTH_SHORT).show();
                return;
            } else {
                localTime = LocalTime.parse(myTime.getText().toString(), timeFormatter);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Date and time not specified properly", Toast.LENGTH_SHORT).show();
            Log.i("Database", "doSave: Date and Time not properly specified.");
            return;
        }

        // Insert data into the database
        boolean status = myDataset.insertData(type,
                title.getText().toString(),
                course.getText().toString(),
                description.getText().toString(),
                localDate,
                localTime
        );

        if (status) {
            // Clear fields on successful save
            title.setText("");
            course.setText("");
            description.setText("");
            myDate.setText("");
            myTime.setText("");
            Toast.makeText(getApplicationContext(), "Study Plan saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to save study plan", Toast.LENGTH_SHORT).show();
        }
    }

    public void doLoad(View view) {
        // Load and display all data from the database
        myDataset.getAll();
    }
}
