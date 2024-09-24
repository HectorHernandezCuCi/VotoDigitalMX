package com.mx.votodigitalmx;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RegisterPage3 extends AppCompatActivity {

    private static final int REQUEST_IMG_CAPTURE = 1;
    private Button takeFrontPhotoButton;
    private ImageView frontImageView;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        // Inicializar los elementos de la vista
        takeFrontPhotoButton = findViewById(R.id.takeFrontPhotoButton);
        frontImageView = findViewById(R.id.frontImageView);

        // Inicializar Firebase Storage y Firestore
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener userId de la actividad anterior
        userId = getIntent().getStringExtra("userId");

        // Listener para el botón de tomar foto
        takeFrontPhotoButton.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMG_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMG_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                frontImageView.setImageBitmap(imageBitmap);
                uploadImageToFirebase(imageBitmap);
            }
        }
    }

    private void uploadImageToFirebase(Bitmap imageBitmap) {
        // Referencia a Firebase Storage
        StorageReference storageRef = storage.getReference().child("user_photos/" + userId + "_front.jpg");

        // Convertir el bitmap a un stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Subir imagen
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Obtener la URL de descarga y guardar en Firestore
            saveImageUrlToFirestore(uri);
        })).addOnFailureListener(e -> {
            Toast.makeText(RegisterPage3.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveImageUrlToFirestore(Uri uri) {
        // Guardar URL de imagen en Firestore
        db.collection("users").document(userId)
                .update("photoUrl", uri.toString())
                .addOnSuccessListener(aVoid -> Toast.makeText(RegisterPage3.this, "Foto subida exitosamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RegisterPage3.this, "Error al guardar URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
