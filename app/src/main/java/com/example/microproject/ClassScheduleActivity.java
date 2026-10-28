
package com.example.microproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ClassScheduleActivity extends Activity {

    DatabaseHelper db;
    ListView listClasses;
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule);

        db = new DatabaseHelper(this);
        listClasses = findViewById(R.id.listClasses);

        findViewById(R.id.btnAddClass).setOnClickListener(
                v -> showAddDialog());

        listClasses.setOnItemClickListener((parent, view, position, id) ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete class?")
                        .setMessage(items.get(position))
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (d, w) -> {
                            db.delete("classes", ids.get(position));
                            loadClasses();
                        }).show());

        loadClasses();
    }

    private EditText field(LinearLayout layout, String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        layout.addView(e);
        return e;
    }

    private void showAddDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources()
                .getDisplayMetrics().density);
        form.setPadding(pad, 0, pad, 0);

        EditText subject = field(form, "Subject name");
        EditText day = field(form, "Day (e.g. Monday)");
        EditText start = field(form, "Start time (e.g. 09:00 AM)");
        EditText end = field(form, "End time (e.g. 10:00 AM)");
        EditText room = field(form, "Classroom");

        new AlertDialog.Builder(this)
                .setTitle("Add Class")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    if (subject.getText().toString().trim().isEmpty()
                            || day.getText().toString().trim().isEmpty()
                            || start.getText().toString().trim().isEmpty()
                            || end.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this,
                                "Fill in subject, day and times",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ContentValues values = new ContentValues();
                    values.put("subject", subject.getText().toString().trim());
                    values.put("day", day.getText().toString().trim());
                    values.put("start_time", start.getText().toString().trim());
                    values.put("end_time", end.getText().toString().trim());
                    values.put("room", room.getText().toString().trim());

                    if (db.insert("classes", values) == -1) {
                        Toast.makeText(this, "Could not save class",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        loadClasses();
                    }
                }).show();
    }

    private void loadClasses() {
        ids.clear();
        items.clear();

        try (Cursor c = db.getAll("classes")) {
            while (c.moveToNext()) {
                ids.add(c.getInt(c.getColumnIndexOrThrow("id")));
                items.add(
                        c.getString(c.getColumnIndexOrThrow("subject"))
                                + "\n" + c.getString(c.getColumnIndexOrThrow("day"))
                                + " | "
                                + c.getString(c.getColumnIndexOrThrow("start_time"))
                                + " - "
                                + c.getString(c.getColumnIndexOrThrow("end_time"))
                                + "\nRoom: "
                                + c.getString(c.getColumnIndexOrThrow("room")));
            }
        }

        listClasses.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, items));
    }
}
