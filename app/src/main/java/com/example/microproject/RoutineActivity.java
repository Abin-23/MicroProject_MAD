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

public class RoutineActivity extends Activity {

    DatabaseHelper db;
    ListView listRoutine;
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Boolean> completed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        db = new DatabaseHelper(this);
        listRoutine = findViewById(R.id.listRoutine);

        findViewById(R.id.btnAddRoutine).setOnClickListener(
                v -> showAddDialog());

        listRoutine.setOnItemClickListener((parent, view, position, id) -> {
            int taskId = ids.get(position);
            boolean wasDone = completed.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Routine Task")
                    .setMessage(items.get(position))
                    .setPositiveButton(
                            wasDone ? "Mark Pending" : "Mark Completed",
                            (dialog, which) -> {
                                db.updateRoutineStatus(taskId, !wasDone);
                                loadRoutines();
                            })
                    .setNeutralButton("Delete", (dialog, which) -> {
                        db.delete("routines", taskId);
                        loadRoutines();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        loadRoutines();
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

        EditText title = field(form, "Task name");
        EditText date = field(form, "Date (YYYY-MM-DD)");
        EditText time = field(form, "Time (e.g. 04:00 PM)");

        new AlertDialog.Builder(this)
                .setTitle("Add Routine Task")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String task = title.getText().toString().trim();
                    String day = date.getText().toString().trim();
                    String hour = time.getText().toString().trim();

                    if (task.isEmpty() || day.isEmpty() || hour.isEmpty()) {
                        Toast.makeText(this, "Complete all fields",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ContentValues values = new ContentValues();
                    values.put("title", task);
                    values.put("date", day);
                    values.put("time", hour);
                    values.put("done", 0);

                    if (db.insert("routines", values) == -1) {
                        Toast.makeText(this, "Could not save task",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        loadRoutines();
                    }
                }).show();
    }

    private void loadRoutines() {
        ids.clear();
        items.clear();
        completed.clear();

        try (Cursor c = db.getAll("routines")) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String title = c.getString(
                        c.getColumnIndexOrThrow("title"));
                String date = c.getString(
                        c.getColumnIndexOrThrow("date"));
                String time = c.getString(
                        c.getColumnIndexOrThrow("time"));
                boolean done = c.getInt(
                        c.getColumnIndexOrThrow("done")) == 1;

                ids.add(id);
                completed.add(done);
                items.add((done ? "✓ " : "○ ") + title
                        + "\n" + date + " | " + time
                        + (done ? "\nCompleted" : "\nPending"));
            }
        }

        listRoutine.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, items));
    }
}
