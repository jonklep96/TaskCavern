package com.taskcavern.taskcavern;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SecondaryListActivity extends AppCompatActivity {

    private String arrayKey;

    private ArrayList<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private Button btn_add;

    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_list);
        arrayKey = getIntent().getStringExtra("key");
        file = arrayKey + "PrimaryList.txt";

        ((TextView) findViewById(R.id.tv_secondary_title)).setText(arrayKey);

        ListView listView = (ListView) findViewById(R.id.lv_secondary_list);
        adapter = new ArrayAdapter<>(this, R.layout.secondary_list_item, R.id.tv_secondary_item, list);
        listView.setAdapter(adapter);

        btn_add = (Button) findViewById(R.id.btn_add_secondary_item);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.et_add_secondary_item);
                if (!editText.getText().toString().equals("")) {
                    String text = editText.getText().toString();
                    list.add(text);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
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
