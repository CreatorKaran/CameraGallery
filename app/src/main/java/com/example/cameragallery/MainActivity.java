package com.example.cameragallery;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int APP_PERMISSIONS = 1;
    private static final int CAPTURE_IMAGE = 10;
    private Button btCamera, btGallery;
    private ImageView imageView;
    private Context context;
    private Drawable img;
    private String imageName;
    BitmapDrawable drawable;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        btCamera=findViewById(R.id.btCamera);
        btGallery=findViewById(R.id.btGallery);

        imageView=findViewById(R.id.imageView);

        checkPermission();


        btGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GalleryActivity.class);
                startActivity(intent);

            }
        });


        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btCamera.getText().toString()){
                    case "Camera":
                        img = getApplication().getResources().getDrawable(R.drawable.ic_save_black_24dp);
                        btCamera.setText("Save");
                        btCamera.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);

                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,CAPTURE_IMAGE);
                        break;
                    case "Save":

                        drawable = (BitmapDrawable) imageView.getDrawable();
                        bitmap=drawable.getBitmap();

                        FileOutputStream outputStream = null;

                        File sdCard= Environment.getExternalStorageDirectory();
                        File directory = new File(sdCard.getAbsolutePath() + "/CameraGallery");
                        directory.mkdir();

                        imageName= String.format("CAM_%d.jpg",System.currentTimeMillis());
                        File outFile= new File(directory,imageName);

                        try {
                            outputStream =new FileOutputStream(outFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                            outputStream.flush();
                            outputStream.close();

                            Intent intent1=  new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent1.setData(Uri.fromFile(outFile));
                            sendBroadcast(intent1);

                            img = getApplication().getResources().getDrawable(R.drawable.ic_camera_black_24dp);
                            btCamera.setText("Camera");
                            btCamera.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);

                            Toast.makeText(context, "Image Saved Successfully", Toast.LENGTH_SHORT).show();


                        } catch (FileNotFoundException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE ) {
            if(resultCode == RESULT_OK){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);

            }else if(resultCode == RESULT_CANCELED){
                    img = getApplication().getResources().getDrawable(R.drawable.ic_camera_black_24dp);
                    btCamera.setText("Camera");
                    btCamera.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
            }

        }
    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)+
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Camera, Read and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                APP_PERMISSIONS
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        APP_PERMISSIONS
                );
            }
        } else {
            // Do something, when permissions are already granted
            Toast.makeText(this,"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case APP_PERMISSIONS:{
                // When request is cancelled, the results array are empty
                if(
                        (grantResults.length >0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ){
                    // Permissions are granted
                    Toast.makeText(this,"Permissions granted.",Toast.LENGTH_SHORT).show();
                }else {
                    // Permissions are denied
                    Toast.makeText(this,"Permissions denied.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
