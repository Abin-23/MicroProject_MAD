package com.example.microproject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClasses = findViewById(R.id.btnClasses);
        Button btnRoutine = findViewById(R.id.btnRoutine);
        Button btnActivities = findViewById(R.id.btnActivities);

        btnClasses.setOnClickListener(v ->
                startActivity(new Intent(this,
                        ClassScheduleActivity.class)));

        btnRoutine.setOnClickListener(v ->
                startActivity(new Intent(this,
                        RoutineActivity.class)));

        btnActivities.setOnClickListener(v ->
                startActivity(new Intent(this,
                        AcademicActivity.class)));
    }
}
