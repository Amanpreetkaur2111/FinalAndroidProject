package com.example.androidproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class NotesAdapter extends ArrayAdapter {
    Context mContext;
    int layoutRes;
    List<Notesdata> Notes;
//    SQLiteDatabase mDatabase;
    DatabaseHelper mDatabase;



    public NotesAdapter(Context mContext, int layoutRes, List<Notesdata> Notes, DatabaseHelper mDatabase) {
        super(mContext, layoutRes, Notes);
        this.mContext = mContext;
        this.layoutRes = layoutRes;
        this.Notes = Notes;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(layoutRes, null);
        TextView title = v.findViewById(R.id.notes);
        TextView DateTime = v.findViewById(R.id.dateTime);


        final Notesdata notesdata = Notes.get(position);
        title.setText(notesdata.getNotesTitle());
        DateTime.setText(notesdata.getDate());

        v.findViewById(R.id.listLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, NotesDetail.class);
                i.putExtra("OBJ",notesdata);
                mContext.startActivity(i);
            }
        });

        v.findViewById(R.id.btn_delete_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(notesdata);
            }
        });

        return v;
    }

    private void deleteNote(final Notesdata notesdata) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mDatabase.deleteNote(notesdata.getId()))
                    loadNotesTitle(notesdata.getCategory());
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void loadNotesTitle(String cat) {


        Cursor cursor = mDatabase.getAllNotes(cat);

        System.out.println(Notes.size());
        Notes.clear();
        System.out.println(Notes.size());
        if (cursor.moveToFirst()) {

            do {
                Notes.add(new Notesdata(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6),
                        cursor.getString(7)

                ));
                System.out.println(Notes.size());


            } while (cursor.moveToNext());
            cursor.close();
        }

        System.out.println(Notes.size());


        notifyDataSetChanged();
        System.out.println(Notes.size());
    }
}
