package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NotesList extends AppCompatActivity implements View.OnClickListener{


    ListView listView;
    DatabaseHelper mDatabase;
    public  static List<String> notesTitle;
    public  static List<Notesdata> AllData;


    String category_name = "";
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);


        Intent i = getIntent();
        category_name = i.getStringExtra("CAT");


      listView = findViewById(R.id.Notes_listview);
      mDatabase = new DatabaseHelper(this);

      findViewById(R.id.add_note).setOnClickListener(this);

        notesTitle = new ArrayList<>();
        AllData = new ArrayList<>();
        loadNotesTitle();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotesList.this,NotesDetail.class);

                intent.putExtra("object", AllData.get(position));
                startActivity(intent);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadNotesTitle();
    }

    private void loadNotesTitle(){

        notesTitle.clear();
        AllData.clear();
        Cursor cursor = mDatabase.getAllNotes(category_name);

        if(cursor.moveToFirst()){
            do{
                notesTitle.add(cursor.getString(2));
                AllData.add(new Notesdata(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6)));

            } while (cursor.moveToNext());
            cursor.close();

            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,notesTitle);
            listView.setAdapter(arrayAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,NotesDetail.class);
        startActivity(intent);
    }
}
