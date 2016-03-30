package com.taskcavern.taskcavern;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class PrimaryListActivity extends AppCompatActivity {

    private String arrayKey;

    private ArrayList<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private Button btn_add;

    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_list);
        arrayKey = getIntent().getStringExtra("key");
        file = arrayKey + "SecondaryList.txt";

        ((TextView) findViewById(R.id.tv_primary_title)).setText(arrayKey);

        ListView listView = (ListView) findViewById(R.id.lv_primary_list);
        adapter = new ArrayAdapter<>(this, R.layout.primary_list_item, list);
        listView.setAdapter(adapter);

        btn_add = (Button) findViewById(R.id.btn_add_primary_item);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PrimaryListActivity.this);
                dialog.setContentView(R.layout.dialog_dashboard_item);
                dialog.setTitle("Add a List");
                dialog.setCancelable(true);
                Button button = (Button) dialog.findViewById(R.id.btn_dialog_dashboard_item);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) dialog.findViewById(R.id.et_dialog_dashboard_item);
                        String text = editText.getText().toString();
                        if (!text.equals("")) {
                            list.add(text);
                            dialog.cancel();
                            adapter.notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Please Enter a Name", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), SecondaryListActivity.class);
                i.putExtra("key", list.get(position));
                startActivity(i);
            }
        });

        readList();
    }

    private void saveList() {
        try {
            OutputStreamWriter output = new OutputStreamWriter(openFileOutput(file, MODE_PRIVATE));
            for(String e : list)
                output.write(e + "\n");
            output.close();
        } catch (IOException e) {
            Log.e("Save Error", "Could not save list");
        }
    }

    private void readList() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(file)));
            String line;

            while((line = reader.readLine()) != null) { list.add(line); }
            reader.close();
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            Log.e("Input Error", "Error in reading list input");
        }
    }

    private void deleteList() {
        deleteFile(file);
    }

    @Override
    public void onPause() {
        saveList();
        super.onPause();
    }
}
