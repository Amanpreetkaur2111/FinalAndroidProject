package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotesDetail extends AppCompatActivity implements View.OnClickListener{


    DatabaseHelper mDatabase;

    EditText ET_category, ET_NoteTitle, ET_description;
    Button BT_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);

        ET_category = findViewById(R.id.E_category);
        ET_NoteTitle = findViewById(R.id.E_noteTitle);
        ET_description = findViewById(R.id.E_description);

       findViewById(R.id.btn_save).setOnClickListener(this);


       mDatabase = new DatabaseHelper(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
              addNotes();
        }

    }

    private void addNotes(){
        String category = ET_category.getText().toString().trim();
        String noteTitle = ET_NoteTitle.getText().toString().trim();
        String description = ET_description.getText().toString().trim();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String date = df.format(calendar.getTime());

        if(mDatabase.addNotes(category,noteTitle,description,date))
            Toast.makeText(this,"Added",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Not Added",Toast.LENGTH_SHORT).show();
    }


}
