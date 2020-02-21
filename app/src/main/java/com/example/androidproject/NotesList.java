package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class NotesList extends AppCompatActivity implements View.OnClickListener{

    SearchView searchoption;
    List<Notesdata>filter;
    ListView listView;
    DatabaseHelper mDatabase;
    public  static List<String> notesTitle;
    public  static List<Notesdata> AllData;


    String category_name = "";
    NotesAdapter notesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        Button sort_by_title = findViewById(R.id.sort_title);
        Button sort_by_date = findViewById(R.id.sort_date);

        searchoption = findViewById(R.id.SearchOption);

        searchoption.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    filter.clear();
                    for(int i =0;i<AllData.size();i++){


                        Notesdata getdata = AllData.get(i);
                        if(getdata.notesTitle.contains(newText)){

                            filter.add(getdata);

                        }

                    }
                    notesAdapter = new NotesAdapter(NotesList.this, R.layout.list_layout_notes, filter, mDatabase);
                    listView.setAdapter(notesAdapter);
                }

                if(newText.isEmpty()){

                    loadNotesTitle();

                }
                return false;
            }
        });


        Intent i = getIntent();
        category_name = i.getStringExtra("CAT");


      listView = findViewById(R.id.Notes_listview);
      mDatabase = new DatabaseHelper(this);

      findViewById(R.id.add_note).setOnClickListener(this);

        filter = new ArrayList<>();
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

        sort_by_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadsortTitle(DatabaseHelper.COLUMN_NOTESTITLE);
                notesAdapter = new NotesAdapter(NotesList.this, R.layout.list_layout_notes, AllData, mDatabase);
                listView.setAdapter(notesAdapter);


            }
        });

        sort_by_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadsortTitle(DatabaseHelper.COLUMN_DATE);
                notesAdapter = new NotesAdapter(NotesList.this, R.layout.list_layout_notes, AllData, mDatabase);
                listView.setAdapter(notesAdapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadNotesTitle();
    }

    private void loadNotesTitle(){

       // notesTitle.clear();
        AllData.clear();
        Cursor cursor = mDatabase.getAllNotes(category_name);

        if(cursor.moveToFirst()){
            do{
                //notesTitle.add(cursor.getString(2));
                AllData.add(new Notesdata(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6),
                        cursor.getString(7)));

            } while (cursor.moveToNext());
            cursor.close();

            notesAdapter = new NotesAdapter(NotesList.this, R.layout.list_layout_notes, AllData, mDatabase);
            listView.setAdapter(notesAdapter);
        }
    }



    private void loadsortTitle(String sort){
        Cursor cursor = mDatabase.getSortedNotes(DatabaseHelper.COLUMN_NOTESTITLE,category_name);
        AllData.clear();

        if(cursor.moveToFirst()){
            do{
                AllData.add(new Notesdata(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6),
                        cursor.getString(7)));
            }while ( cursor.moveToNext());
            cursor.close();

            notesAdapter = new NotesAdapter(NotesList.this, R.layout.list_layout_notes, AllData, mDatabase);
            listView.setAdapter(notesAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,NotesDetail.class);
        startActivity(intent);
    }
}
