package com.example.bnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateNotes extends AppCompatActivity {

    EditText inputTitle,inputLink,inputSubject;
    Button updateButton, deleteButton;

    String id,title,link,subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_notes);

        inputTitle =  findViewById(R.id.inputTitle2);
        inputLink = findViewById(R.id.inputLink2);
        inputSubject = findViewById(R.id.inputSubject2);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        //First we call this
        getAndSetIntentData();

        //set ActionBar Title after getAndSetIntentData
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setTitle(title);
        }

        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //And only then call this
                DataBase myDB = new DataBase(UpdateNotes.this);
                title = inputTitle.getText().toString().trim();
                link = inputLink.getText().toString().trim();
                subject = inputSubject.getText().toString().trim();
                myDB.updateData(id, title, link, subject);
                finish();
                Intent intent = new Intent(UpdateNotes.this, MainActivity.class);
                intent.putExtra("position",getIntent().getStringExtra("position"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                confirmDialog();

            }
        });



    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("title") &&
                getIntent().hasExtra("link") && getIntent().hasExtra("subject")){


            //Getting data from intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            link = getIntent().getStringExtra("link");
            subject = getIntent().getStringExtra("subject");

            //setting Intent Data
            inputTitle.setText(title);
            inputLink.setText(link);
            inputSubject.setText(subject);
            Log.d("stev", title+" / "+subject);

        }else {
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
        }

    }


    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete?");
        builder.setMessage("Are you sure you want to delete "+title+"?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DataBase myDB = new DataBase(UpdateNotes.this);
                myDB.deleteOneRow(id);
                finish();
                startActivity(new Intent(UpdateNotes.this,MainActivity.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


    @Override
    public void onBackPressed() {

//        if(inputLink.getText().length() == 0 && inputSubject.getText().length() == 0 && inputTitle.getText().length() == 0){
//
//            finish();
//            Toast.makeText(this,"Empty note",Toast.LENGTH_SHORT).show();
//        }
//        else {
//            startActivity(new Intent(UpdateNotes.this, MainActivity.class));
//        }

        finish();

        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem selectedItemCount = menu.findItem(R.id.action_item_count);
        MenuItem clearSelection = menu.findItem(R.id.action_clear);
        MenuItem gridSelection = menu.findItem(R.id.action_grid);
        MenuItem deleteSelection = menu.findItem(R.id.action_delete);
        MenuItem settings = menu.findItem(R.id.action_settings);

        selectedItemCount.setVisible(false);
        clearSelection.setVisible(false);
        gridSelection.setVisible(false);
        deleteSelection.setVisible(false);
        settings.setVisible(false);




        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_share){

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getIntent().getStringExtra("title"));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getIntent().getStringExtra("subject")+ "\n"+getIntent().getStringExtra("link"));
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }


        return super.onOptionsItemSelected(item);
    }
}
