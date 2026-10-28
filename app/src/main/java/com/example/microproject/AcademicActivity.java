package com.example.microproject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AcademicActivity extends Activity {

    DatabaseHelper db;
    ListView listActivities;
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic);

        db = new DatabaseHelper(this);
        listActivities = findViewById(R.id.listActivities);

        findViewById(R.id.btnAddActivity).setOnClickListener(
                v -> showAddDialog());

        listActivities.setOnItemClickListener((parent, view, position, id) ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete activity?")
                        .setMessage(items.get(position))
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.delete("activities", ids.get(position));
                            loadActivities();
                        }).show());

        loadActivities();
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

        EditText title = field(form, "Activity title");
        EditText type = field(form, "Type (Exam/Assignment/Seminar)");
        EditText date = field(form, "Due date (YYYY-MM-DD)");
        EditText notes = field(form, "Notes (optional)");

        new AlertDialog.Builder(this)
                .setTitle("Add Academic Activity")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = title.getText().toString().trim();
                    String category = type.getText().toString().trim();
                    String dueDate = date.getText().toString().trim();
                    String note = notes.getText().toString().trim();

                    if (name.isEmpty() || category.isEmpty()
                            || dueDate.isEmpty()) {
                        Toast.makeText(this,
                                "Complete the required fields",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ContentValues values = new ContentValues();
                    values.put("title", name);
                    values.put("type", category);
                    values.put("date", dueDate);
                    values.put("notes", note);

                    if (db.insert("activities", values) == -1) {
                        Toast.makeText(this,
                                "Could not save activity",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        loadActivities();
                    }
                }).show();
    }

    private void loadActivities() {
        ids.clear();
        items.clear();

        try (Cursor c = db.getAll("activities")) {
            while (c.moveToNext()) {
                ids.add(c.getInt(c.getColumnIndexOrThrow("id")));

                String title = c.getString(
                        c.getColumnIndexOrThrow("title"));
                String type = c.getString(
                        c.getColumnIndexOrThrow("type"));
                String date = c.getString(
                        c.getColumnIndexOrThrow("date"));
                String notes = c.getString(
                        c.getColumnIndexOrThrow("notes"));

                items.add(title + "\n" + type + " | " + date
                        + (notes == null || notes.trim().isEmpty()
                        ? "" : "\n" + notes));
            }
        }

        listActivities.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, items));
    }
}
