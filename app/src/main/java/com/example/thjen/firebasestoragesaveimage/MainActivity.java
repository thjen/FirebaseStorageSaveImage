package com.example.thjen.firebasestoragesaveimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button bt;
    EditText et;
    ListView lv;
    ArrayList<HinhAnh> list;

    int REQUEST_CODE_IMAGE = 1;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference dataRef;
    adapter ada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageV);
        bt = (Button) findViewById(R.id.bt);
        et = (EditText) findViewById(R.id.et);
        lv = (ListView) findViewById(R.id.lv);

        dataRef = FirebaseDatabase.getInstance().getReference();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://imagestorage-51440.appspot.com");

        list = new ArrayList<>();
        ada = new adapter(this, list, R.layout.row);
        lv.setAdapter(ada);

        LoadDataBase();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        final Random random = new Random();

        bt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                final int r = random.nextInt(1000000000);

                StorageReference mountainsRef = storageRef.child("image" + r + ".png");

                image.setDrawingCacheEnabled(true);
                image.buildDrawingCache();
                Bitmap bitmap = image.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                final byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                        Log.d("a", downloadUrl + "");
                        HinhAnh anh = new HinhAnh(et.getText().toString(), String.valueOf(downloadUrl));
                        dataRef.child("Hinh anh").push().setValue(anh, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if ( databaseError == null ) {
                                    Toast.makeText(MainActivity.this, "Successfully data", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });

            }
        });

    }

    private void LoadDataBase() {

        dataRef.child("Hinh anh").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HinhAnh anh = dataSnapshot.getValue(HinhAnh.class);
                list.add(new HinhAnh(anh.getTen(), anh.getLink()));
                ada.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ( requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null ) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
