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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper mDatabase;
    public static List<Notesdata> CategoriesList;
    Button AddNote;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    public static int categoryPosition;
//    ArrayList<String> categName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      AddNote = findViewById(R.id.add_btn);
      listView = findViewById(R.id.list_view);


      findViewById(R.id.add_btn).setOnClickListener(this);

      mDatabase = new DatabaseHelper(this);
      CategoriesList = new ArrayList<>();
      loadEmployees();



      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              categoryPosition = position;
              Intent intent = new Intent(MainActivity.this, NotesList.class);
              intent.putExtra("id",position);
              startActivity(intent);
          }
      });


    }

    private void loadEmployees(){

        Cursor cursor = mDatabase.getAllCategories();

        if(cursor.moveToFirst()){
           do{
               CategoriesList.add(new Notesdata(
                       cursor.getInt(0)
               ));
           } while (cursor.moveToNext());
           cursor.close();

//           arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,CategoriesList,mDatabase);
           listView.setAdapter(arrayAdapter);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_btn:
                Intent intent = new Intent(MainActivity.this,NotesDetail.class);
                startActivity(intent);
        }
    }


}
