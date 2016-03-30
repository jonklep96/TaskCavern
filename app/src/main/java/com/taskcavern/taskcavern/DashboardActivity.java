package com.taskcavern.taskcavern;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

public class DashboardActivity extends AppCompatActivity {

    private GridLayout mGrid;

    private String mFile = "Dashboard.txt";

    private View menuView;
    private final int MENU_DELETE = Menu.FIRST;
    private final int MENU_MOVE = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mGrid = (GridLayout) findViewById(R.id.dashboard_grid);
        createDashboardItem(getResources().getString(R.string.btn_create_dashboard_item));

        readList();
    }

    public void createDashboardItem(String text) {
        int count = mGrid.getChildCount();

        final TextView textView = new TextView(this);
        textView.setText(text);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        textView.setWidth(width / 2);
        textView.setHeight(width / 2);

        int p = getResources().getDimensionPixelSize(R.dimen.dashboard_item_padding);
        textView.setPaddingRelative(p, p, p, p);

        textView.setGravity(Gravity.CENTER);

        textView.setBackgroundColor(getRandomColor(50, 50, 50));
        textView.setTextColor(Color.rgb(0, 0, 0));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PrimaryListActivity.class);
                i.putExtra("key", textView.getText().toString());
                startActivity(i);
            }
        });

        if(count == 0) {
            textView.setBackground(getResources().getDrawable(R.drawable.add_button, getTheme()));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(DashboardActivity.this);
                    dialog.setContentView(R.layout.dialog_dashboard_item);
                    dialog.setTitle("Add a Category");
                    dialog.setCancelable(true);
                    Button button = (Button) dialog.findViewById(R.id.btn_dialog_dashboard_item);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editText = (EditText) dialog.findViewById(R.id.et_dialog_dashboard_item);
                            String text = editText.getText().toString();
                            if (!text.equals("")) {
                                createDashboardItem(text);
                                dialog.cancel();
                            } else
                                Toast.makeText(getApplicationContext(), "Please Enter a Category", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                }
            });
        }

        if(count > 0) {
            //registerForContextMenu(textView);
            textView.setOnLongClickListener(longClickListener);
            textView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    int action = event.getAction();

                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            Log.e("drag start", "drag started");
                            return true;
                        case DragEvent.ACTION_DROP:
                            View localState = (View) event.getLocalState();
                            v.setVisibility(View.VISIBLE);
                            localState.setVisibility(View.VISIBLE);
                            localState.setX(event.getX());
                            localState.setY(event.getY());
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            break;
                    }

                    return true;
                }
            });
        }

        mGrid.addView(textView, count - 1);
    }

    public int getRandomColor(int maxR, int maxG, int maxB) {
        if(maxR > 255 || maxR < 0 || maxG > 255 || maxG < 0 || maxB > 255 || maxB < 0)
            return Color.BLACK;
        else {
            Random random = new Random();
            int r = (int) (random.nextDouble() * maxR) + (255 - maxR);
            int g = (int) (random.nextDouble() * maxG) + (255 - maxG);
            int b = (int) (random.nextDouble() * maxB) + (255 - maxB);

            return Color.rgb(r, g, b);
        }
    }

    private void saveList() {
        try {
            OutputStreamWriter output = new OutputStreamWriter(openFileOutput(mFile, MODE_PRIVATE));
            for(int i = 0; i < mGrid.getChildCount() - 1; i++)
                output.write(((TextView) mGrid.getChildAt(i)).getText().toString() + "\n");
            output.close();
        } catch (IOException e) {
            Log.e("Save Error", "Could not save list");
        }
    }

    private void readList() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(mFile)));
            String line;

            while((line = reader.readLine()) != null) { createDashboardItem(line); }
            reader.close();
        } catch (IOException e) {
            Log.e("Input Error", "Error in reading list input");
        }
    }

    private void deleteList() {
        deleteFile(mFile);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menuView = v;

        menu.setHeaderTitle(getResources().getString(R.string.context_menu_title));
        menu.add(0, MENU_DELETE, Menu.NONE, getResources().getStringArray(R.array.dashboard_context_menu)[0]);
        menu.add(0, MENU_MOVE, Menu.NONE, getResources().getStringArray(R.array.dashboard_context_menu)[1]);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        int itemId = item.getItemId();
        switch (itemId) {
            case MENU_DELETE: deleteMenuView(menuView); break;
            case MENU_MOVE: moveMenuView(menuView); break;
        }
        return false;
    }

    private void deleteMenuView(View v) {
        mGrid.removeView(v);
    }

    private void moveMenuView(View v) {}

    @Override
    public void onPause() {
        saveList();
        super.onPause();
    }

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipData data = ClipData.newPlainText("", "");
            MyDragShadowBuilder shadow = new MyDragShadowBuilder(v);

            v.startDrag(data, shadow, v, 0);
            return false;
        }
    };

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        public static View dragObject;

        public MyDragShadowBuilder(View v) {
            super(v);
            dragObject = v;
            //v.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {

            int width = getView().getWidth();
            int height = getView().getHeight();

            size.set(width, height);

            // Centers the View by the touch point
            touch.set(width/2, height/2);

            super.onProvideShadowMetrics(size, touch);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            dragObject.draw(canvas);
        }
    }


}
