package com.example.bnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.bnotes.adapter.CustomAdapter;
import com.example.bnotes.adapter.MyItemKeyProvider;
import com.example.bnotes.adapter.MyItemLookup;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    MenuItem selectedItemCount, clearSelection, gridSelection, deleteSelection, share;
    SelectionTracker selectionTracker;
    private RecyclerView recyclerView;
    private CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    StaggeredGridLayoutManager gridLayoutManager;
    private ActionMode actionMode;
    ArrayList<String> note_id, note_title, note_link, note_subject;
    DataBase myDB;
    ImageView no_data_image;
    TextView note_id_txt;
    FloatingActionButton add_button;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    BottomNavigationView bottomNavigationView;
    BottomNavigationItemView select,deselectAll;
    Boolean StateSelectAll = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.getMenu().removeItem(R.id.invisible);
        select = findViewById(R.id.bottom_action_select);
        //deselectAll.setVisibility(View.GONE);


        //itemList = getRandomList();
        no_data_image = (ImageView) findViewById(R.id.no_data_image);
        recyclerView = findViewById(R.id.recyclerViewNotes);
        note_id_txt = findViewById(R.id.note_id_txt);
        add_button = (FloatingActionButton) findViewById(R.id.add_links);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddNotes.class);
                startActivity(intent);
                //finish();


            }
        });


        recyclerView.setHasFixedSize(true);


        myDB = new DataBase(MainActivity.this);
        note_id = new ArrayList<>();
        note_title = new ArrayList<>();
        note_link = new ArrayList<>();
        note_subject = new ArrayList<>();

        storeDataInArrays();


        mAdapter = new CustomAdapter(MainActivity.this, this, note_id, note_title, note_link, note_subject);


        //mLayoutManager = new LinearLayoutManager(this);
        //gridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);


        setSharedPref();

        layoutChange(sharedPreferences.getBoolean("relayout", false));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //mAdapter = new CustomListAdapter(itemList);
        recyclerView.setAdapter(mAdapter);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        setScrollPosition();


        selectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(1, note_id),
                new MyItemLookup(recyclerView),
                StorageStrategy.createLongStorage()
        )
//                .withOnItemActivatedListener(new OnItemActivatedListener<Long>() {
//                    @Override
//                    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull MotionEvent e) {
//                        Log.d(TAG, "Selected ItemId: " + item.toString());
//                        return true;
//                    }
//                })
                .withOnDragInitiatedListener(new OnDragInitiatedListener() {
                    @Override
                    public boolean onDragInitiated(@NonNull MotionEvent e) {
                        Log.d(TAG, "onDragInitiated");
                        return true;
                    }

                })
                .build();
        mAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onItemStateChanged(@NonNull Object key, boolean selected) {


                super.onItemStateChanged(key, selected);
            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = startSupportActionMode(new ActionModeController(MainActivity.this, selectionTracker));
                    setMenuItemTitle(selectionTracker.getSelection().size());


                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                } else {
                    setMenuItemTitle(selectionTracker.getSelection().size());
                }
                Iterator<String> itemIterable = selectionTracker.getSelection().iterator();
                while (itemIterable.hasNext()) {
                    Log.i(TAG, itemIterable.next().toString());

                }
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });

        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        selectionTracker.onSaveInstanceState(outState);
    }

    public void setMenuItemTitle(int selectedItemSize) {


        if (selectedItemSize > 0) {

            selectedItemCount.setVisible(true);
            selectedItemCount.setTitle(selectedItemSize + " Selected");
            clearSelection.setVisible(true);
            gridSelection.setVisible(false);
            deleteSelection.setVisible(true);

            bottomNavigationView.setVisibility(View.VISIBLE);


            if(mAdapter.getItemCount() == selectedItemSize){

                //bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.getMenu().getItem(0).setTitle("Deselect All").setIcon(R.drawable.ic_baseline_check_box_24);
                StateSelectAll = true;

            }
            else {

                //bottomNavigationView.setVisibility(View.GONE);
                bottomNavigationView.getMenu().getItem(0).setTitle("Select All").setIcon(R.drawable.ic_baseline_check_box_outline_blank_24);
                StateSelectAll = false;
            }



            if (selectedItemSize == 1) {
                share.setVisible(true);
            } else {
                share.setVisible(false);
            }


        } else {
            selectedItemCount.setVisible(false);
            clearSelection.setVisible(false);
            gridSelection.setVisible(true);
            deleteSelection.setVisible(false);
            share.setVisible(false);

            bottomNavigationView.setVisibility(View.GONE);


        }






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        selectedItemCount = menu.findItem(R.id.action_item_count);
        clearSelection = menu.findItem(R.id.action_clear);
        gridSelection = menu.findItem(R.id.action_grid);
        deleteSelection = menu.findItem(R.id.action_delete);
        share = menu.findItem(R.id.action_share);


        selectedItemCount.setVisible(false);
        clearSelection.setVisible(false);
        deleteSelection.setVisible(false);
        share.setVisible(false);


        if (sharedPreferences.getBoolean("relayout", false)) {
            // findItem id function return the id of the menu
            gridSelection.setIcon(R.drawable.baseline_view_quilt_24)
                    .setChecked(true);
//            //editor.putBoolean("relayout", false);
        } else {
            gridSelection.setIcon(R.drawable.baseline_view_stream_24)
                    .setChecked(false);
        }


        //setSharedPref();


        //getSharedPref();
        //setSharedPref(grid);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:

                finish();
                startActivity(new Intent(new Intent(this, SettingsActivity.class)));

                break;

            case R.id.action_share:

                shareNote();

                break;


            case R.id.action_delete:

                alertDialogDelete();
                //deleteSelectedItems();
                //finish();
                //startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.action_clear:
                selectionTracker.clearSelection();
                break;

            case R.id.action_grid:

                //setSharedPref();

//                if (gridSelection.isChecked()) {
//                    editor.putBoolean("checked", true);
//                    gridSelection.setIcon(R.drawable.ic_add);
//                    item.setChecked(true);
//                } else{
//                    gridSelection.setChecked(false);
//                    editor.putBoolean("checked", false);
//                    gridSelection.setIcon(R.drawable.ic_baseline_horizontal_split_24);
//
//                }
//                editor.apply();

                layoutChange(!item.isChecked());

                if (item.isChecked()) {
                    // make it false because user unchecked item.
                    gridSelection.setIcon(R.drawable.baseline_view_stream_24);
                    item.setChecked(false);


                    //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));


                    // checked item now unchecked
                    // so make it false into sharedPreferences
                    editor.putBoolean("relayout", false);
                } else {
                    item.setChecked(true);
                    gridSelection.setIcon(R.drawable.baseline_view_quilt_24);


                    //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));


                    editor.putBoolean("relayout", true);
                }
                editor.commit();


                //setSharedPref(!grid);
//                grid = false;
                //gridSelection.setIcon(R.drawable.ic_baseline_horizontal_split_24);
                //layoutChange(grid);
//                recyclerView.setLayoutManager(mLayoutManager);

                break;
        }
        return true;
    }


    private void storeDataInArrays() {

        DataBase myDB = new DataBase(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        Cursor cursor = null;

        if (!sharedPreferences.getBoolean("reverse_order", false)) {
            cursor = myDB.readAllData();
        } else {
            cursor = myDB.testGetData();
        }
        //Cursor cursor = myDB.testGetData();
        if (cursor.getCount() == 0) {
            no_data_image.setVisibility(View.VISIBLE);
            //no_data.setVisibility(View.VISIBLE);


        } else {
            while (cursor.moveToNext()) {
                note_id.add(cursor.getString(0));
                note_title.add(cursor.getString(1));
                note_link.add(cursor.getString(2));
                note_subject.add(cursor.getString(3));
            }

            no_data_image.setVisibility(View.GONE);
            //no_data.setVisibility(View.GONE);
        }

        cursor.close();


    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            //selectionTracker.clearSelection();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Delete this Note?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    new DataBase(MainActivity.this);
                    myDB.deleteOneRow(String.valueOf(note_id.get(viewHolder.getAdapterPosition())));

                    mAdapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();


                    finish();
                    Intent i2 = new Intent(MainActivity.this, MainActivity.class);
                    overridePendingTransition(0, 0);
                    startActivity(i2);
                    overridePendingTransition(0, 0);
                    myDB.close();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

//                    finish();
//                    Intent i1 = new Intent(MainActivity.this, MainActivity.class);
//                    overridePendingTransition(0, 0);
//                    startActivity(i1);
//                    overridePendingTransition(0, 0);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();


                }
            });

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
//                    finish();
//                    Intent i2 = new Intent(MainActivity.this, MainActivity.class);
//                    overridePendingTransition(0, 0);
//                    startActivity(i2);
//                    overridePendingTransition(0, 0);
                    recyclerView.scheduleLayoutAnimation();
                    mAdapter.notifyDataSetChanged();


                }
            });


            builder.create().show();


        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {


            if (selectionTracker.hasSelection()) return 0;

            return super.getSwipeDirs(recyclerView, viewHolder);
        }
    };


    private void deleteSelectedItems() {
//            for (int i = 0; i < note_id.size(); i++) {
//                if (selectionTracker.isSelected(i)) {
//                    Log.d("testingTAG", String.valueOf(note_id.get(i)));
//                    myDB.deleteOneRow(String.valueOf(note_id.get(i)));
//                    //mAdapter.notifyItemRemoved(i);
//                   // mAdapter.notifyItemRangeChanged(i, selectionTracker.getSelection().size());
//                    i--;
//                }
//            }
//        }

        DataBase dataBase = new DataBase(MainActivity.this);
        if (selectionTracker.getSelection().size() == 0) {

            dataBase.deleteAllData();


        }

        if (selectionTracker.getSelection() != null) {
            Iterator<String> itemIterable = selectionTracker.getSelection().iterator();
            while (itemIterable.hasNext()) {
                //Log.i(TAG, itemIterable.next().toString());

                dataBase.deleteOneRow(itemIterable.next());

            }

        }

    }


    private void layoutChange(boolean gridOn) {

        recyclerView.scheduleLayoutAnimation();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        if (gridOn) {


            //final LayoutAnimationController controller =
            //         AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);

            //recyclerView.setLayoutAnimation(controller);


            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        } else {

            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Integer.parseInt(prefs.getString("columns", "2")), StaggeredGridLayoutManager.VERTICAL));

        }
        setScrollPosition();
        if (prefs.getBoolean("scroll_to_bottom", false)) {
            recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }


    void alertDialogDelete() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Notes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                finish();
                deleteSelectedItems();
                startActivity(new Intent(MainActivity.this, MainActivity.class));


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        builder.create().show();

    }


    private void setSharedPref() {

//        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("gridValue",gridset);
//        editor.commit();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        // by default phone item if you want to make checked then make it true
        //editor.putBoolean("relayout", false);

//        if(sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", true)) {
//
//            gridSelection.setIcon(R.drawable.ic_baseline_horizontal_split_24).setChecked(true);
//        }
//        else {
//            gridSelection.setIcon(R.drawable.ic_add).setChecked(false);
//        }


        editor.apply();


    }

    private void shareNote() {

        DataBase mydb = new DataBase(this);
        Cursor curs = mydb.getOneRow(selectionTracker.getSelection().iterator().next().toString());


        if (selectionTracker.getSelection().size() == 1) {

            while (curs.moveToNext()) {
                Log.d("to share", String.valueOf(curs.getString(1)));


//        String s = curs.getString(1);
//        String d = curs.getString(2);
//        String f = curs.getString(3);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, curs.getString(1));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, curs.getString(3) + "\n" + curs.getString(2));
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        }
        curs.close();
        mydb.close();


    }

    @Override
    public void onBackPressed() {

        if (selectionTracker.hasSelection()) {
            selectionTracker.clearSelection();
        } else {
            finish();
        }

        //super.onBackPressed();
    }

    public void setScrollPosition() {

        int scrollPosition;
        //Set adapter position after updating an item


        if (getIntent().hasExtra("position")) {
            recyclerView.scrollToPosition(Integer.parseInt(getIntent().getStringExtra("position")));
            getIntent().removeExtra("position");
        }
    }


    public void bottomMenuItemsChange(){


        select = (BottomNavigationItemView)findViewById(R.id.bottom_action_select);


        if(selectionTracker.getSelection().size() > 0){


        }





    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {
                case R.id.bottom_action_select:

                    //selectionTracker.setItemsSelected(note_id,true);
                    //bottomNavigationView.getMenu().removeItem(R.id.bottom_action_select_all);
                    //bottomNavigationView.getMenu().getItem(R.id.bottom_action_deselect_all);
                    //bottomNavigationView.findViewById(R.id.bottom_action_deselect_all).setVisibility(View.VISIBLE);
                    //deselectAll.setVisibility(View.VISIBLE);
                    //selectAll.setVisibility(View.GONE);

                    if (StateSelectAll){

                        item.setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_baseline_check_box_outline_blank_24));
                        //bottomNavigationView.getMenu().getItem(R.id.bottom_action_select).setChecked(false);
                        StateSelectAll = false;

                        bottomNavigationView.setSelectedItemId(R.id.invisible);
                        selectionTracker.clearSelection();
                        bottomNavigationView.getMenu().getItem(0).setTitle("Select all");


                        //item.setEnabled(false);


                    } else {

                        StateSelectAll = true;

                        //bottomNavigationView.getMenu().getItem(R.id.bottom_action_select).setChecked(true);
                        bottomNavigationView.setSelectedItemId(R.id.invisible);
                        //select.setIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_baseline_check_box_outline_blank_24));
                        item.setIcon(R.drawable.ic_baseline_check_box_24);
                        selectionTracker.setItemsSelected(note_id,true);
                        bottomNavigationView.getMenu().getItem(0).setTitle("Deselect all");


                    }


                    return true;


                case R.id.bottom_action_share:

                    if(selectionTracker.getSelection().size() == 1) {
                        shareNote();
                    }
                    else Toast.makeText(getApplicationContext(),"Select one Item",Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.bottom_action_delete:

                    alertDialogDelete();

                    return true;


            }
            return true;
        }
    };


}