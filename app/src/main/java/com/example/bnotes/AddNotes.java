package com.example.bnotes;

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
import androidx.appcompat.app.AppCompatActivity;

public class AddNotes extends AppCompatActivity {

    EditText inputTitle,inputLink,inputSubject;
    Button inputButton;
    MainActivity mainActivity = new MainActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        inputTitle = findViewById(R.id.inputTitle);
        inputLink = findViewById(R.id.inputLink);
        inputSubject = findViewById(R.id.inputSubject);


        getIntentFromOtherApps();


        inputButton = findViewById(R.id.inputButton);
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBase myDB = new DataBase(AddNotes.this);
                myDB.addBook(inputTitle.getText().toString().trim(),
                        inputLink.getText().toString().trim(),
                        inputSubject.getText().toString().trim());

                finish();
                Intent intent = new Intent(AddNotes.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();
                myDB.close();


            }
        });

    }


    @Override
    public void onBackPressed() {


        //(inputLink.getText().length() == 0 && inputSubject.getText().length() == 0 && inputTitle.getText().length() == 0){

            finish();
            Toast.makeText(this,"Note discarded",Toast.LENGTH_SHORT).show();



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
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, inputTitle.getText().toString().trim());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,inputSubject.getText().toString().trim()+ "\n" +inputLink.getText().toString().trim());
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }


        return super.onOptionsItemSelected(item);
    }



    void getIntentFromOtherApps() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
    }





    void handleSendText(Intent intent) {
        String sharedText2 = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        String sharedText1 = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText1 != null || sharedText2 != null) {
            // Update UI to reflect text being shared


            Log.i("Intent ------>", sharedText2 + sharedText1);
            inputTitle.setText(sharedText2);
            inputLink.setText(sharedText1);



        }
    }





}
