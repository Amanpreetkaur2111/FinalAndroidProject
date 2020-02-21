package com.example.androidproject;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class NotesDetail extends AppCompatActivity implements View.OnClickListener{


    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURED_CODE = 1001;


    DatabaseHelper mDatabase;


    ImageView mImageView;
    Uri image_uri;
    String notetitle;

    public int position;

    EditText ET_category, ET_NoteTitle, ET_description;
    ImageButton IB_location,IB_Capture,btnPlay,btnStop,btnRecord,btnStopRecord;
    Notesdata n;




    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    final int REQUEST_PERMISSION_CODE = 100;
     private static String RECORD_FILE = "/audio.3gp";


//      Location where note has taken
        Location Nlocation;
        private  GoogleMap mMap;
        private final int REQUEST_CODE = 1;

        private FusedLocationProviderClient mFusedLocationClient;
        LocationCallback locationCallback;
        LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);


        audioManager =(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);

        btnPlay = findViewById(R.id.btn_play);
        btnRecord = findViewById(R.id.btn_record);
        btnStop = findViewById(R.id.btn_stop);
        btnStopRecord = findViewById(R.id.btn_stop_record);


        ET_category = findViewById(R.id.E_category);
        ET_NoteTitle = findViewById(R.id.E_noteTitle);
        ET_description = findViewById(R.id.E_description);
        IB_location = findViewById(R.id.btn_location);

        findViewById(R.id.btn_save).setOnClickListener(this);
        mDatabase = new DatabaseHelper(this);

        String noteTitle = ET_NoteTitle.getText().toString().trim();
        notetitle = noteTitle;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        buildLocationRequest();
        buildLocationCallBack();


        // Image

        mImageView = findViewById(R.id.image_view);
        IB_Capture = findViewById(R.id.btn_capture);
        IB_Capture.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                 if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 == PackageManager.PERMISSION_DENIED){


                     String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

                     requestPermissions(permission,PERMISSION_CODE);
                 }

                  else {
                      openCamera();
                 }

                }else {

                    openCamera();
                }
            }
        });


        Intent i = getIntent();
        n = (Notesdata) i.getSerializableExtra("OBJ");



        if (n != null){
            ET_category.setText(n.category);
            ET_NoteTitle.setText(n.notesTitle);
            ET_description.setText(n.description);
        }




        IB_location.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              Intent intent = new Intent(NotesDetail.this,map_activity.class);
              intent.putExtra("loc",n);
              startActivity(intent);
          }
          });

        if (!checkPermissionDevice())
            requestPermission();


        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionDevice()) {

                   // RECORD_FILE = "/audio" + ET_NoteTitle+".3gp";

                    pathSave = getExternalCacheDir().getAbsolutePath()
                            + "/" + notetitle + ".3gp";

                    Log.d("path", "onClick: " + pathSave);

                    setUpMediaRecorder();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException ise) {
                        // make something ...
                        ise.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnStopRecord.setEnabled(true);
                    btnRecord.setVisibility(View.GONE);

                    Toast.makeText(NotesDetail.this, "Recording...", Toast.LENGTH_SHORT).show();
                } else
                    requestPermission();
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                mediaRecorder.release();
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(true);
                btnRecord.setEnabled(true);
                btnRecord.setVisibility(View.VISIBLE);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(false);
                btnStop.setEnabled(true);
                btnRecord.setEnabled(false);
                btnPlay.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnStop.setVisibility(View.GONE);
                        btnPlay.setVisibility(View.VISIBLE);
                        btnRecord.setEnabled(true);
                    }
                });

                mediaPlayer.start();
                Toast.makeText(NotesDetail.this, "Playing...", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnRecord.setEnabled(true);
                btnPlay.setVisibility(View.VISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setUpMediaRecorder();
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        if(! checkPermissions())
            requestPermissions();
        else
        {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }



    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);



        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURED_CODE);
    }



// IMAGE REQUEST PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
            else
            {
                Toast.makeText(this, "nope", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == PERMISSION_CODE ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
               // mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }



        if (requestCode == REQUEST_PERMISSION_CODE) {
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
               Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
           else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

    }


    // IMAGE


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){

            mImageView.setImageURI(image_uri);
        }
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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd HH:MM:SS");
        String date = df.format(calendar.getTime());

        if (category.isEmpty()) {
            ET_category.setError("Category field is mandatory");
            ET_category.requestFocus();
            return;
        }

        if (noteTitle.isEmpty()) {
            ET_NoteTitle.setError("NoteTitle field cannot be empty");
            ET_NoteTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            ET_description.setError("Description field cannot be empty");
            ET_description.requestFocus();
            return;
        }

        if (n == null){

            // add
            if(mDatabase.addNotes(category,noteTitle,description,date,Nlocation.getLatitude(),Nlocation.getLongitude(),pathSave))
                Toast.makeText(this,"Added",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,"Not Added",Toast.LENGTH_SHORT).show();

        }else{

            if(mDatabase.updateNote(n.id,category, noteTitle, description,Nlocation.getLatitude(),Nlocation.getLongitude(),pathSave))
                Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,"Not updated",Toast.LENGTH_SHORT).show();
        }


    }


    private  boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location: locationResult.getLocations()) {
                    Nlocation = location;
                }
            }
        };
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void getLastLocation() {
        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Nlocation = task.getResult();

                }
            }
        });
    }

//    recording methods
    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }



}
