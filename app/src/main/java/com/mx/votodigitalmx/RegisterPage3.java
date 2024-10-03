package com.mx.votodigitalmx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RegisterPage3 extends AppCompatActivity {

    private static final int REQUEST_IMG_CAPTURE = 1;
    private Button takeFrontPhotoButton, finishButton;
    private ImageView frontImageView;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private Uri uploadedImageUri;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        // Inicializar elementos
        takeFrontPhotoButton = findViewById(R.id.takeFrontPhotoButton);
        finishButton = findViewById(R.id.finishButton);
        frontImageView = findViewById(R.id.frontImageView);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Verificar y solicitar permisos
        checkCameraPermission();

        // Configurar eventos de botones
        configureButtons();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            configureButtons();
        }
    }

    private void configureButtons() {
        takeFrontPhotoButton.setOnClickListener(v -> captureFrontPhoto());
        finishButton.setOnClickListener(v -> sendForm());
    }

    private void captureFrontPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMG_CAPTURE);
        } else {
            Toast.makeText(this, "No se pudo acceder a la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMG_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    frontImageView.setImageBitmap(imageBitmap);
                    uploadImageToFirebase(imageBitmap);
                } else {
                    Toast.makeText(this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No se recibieron datos de la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Bitmap imageBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("images/" + userId + "/profile.jpg");

        UploadTask uploadTask = userImageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                uploadedImageUri = uri;
                Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                finishButton.setEnabled(true);
            });
        }).addOnFailureListener(e -> {
            Log.e("RegisterPage3", "Error al subir la imagen: " + e.getMessage());
            Toast.makeText(this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void sendForm() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            signInAnonymously();
        } else {
            saveUserData(currentUser);
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Log.w("RegisterPage3", "signInAnonymously failed", task.getException());
                        Toast.makeText(this, "Falla en la autenticación anónima", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        saveUserData(user);
                    }
                });
    }

    private void saveUserData(FirebaseUser user) {
        User userObject = new User(
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("lastName"),
                user.getEmail(),
                getIntent().getStringExtra("gender"),
                getIntent().getStringExtra("idNumber"),
                getIntent().getStringExtra("password"),
                getIntent().getStringExtra("address"),
                uploadedImageUri.toString()
        );

        db.collection("users").add(userObject)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Registro completado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginPage.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("RegisterPage3", "Error en el registro: " + e.getMessage());
                    Toast.makeText(this, "Error en el registro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
