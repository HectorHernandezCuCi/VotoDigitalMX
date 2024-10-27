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

        signInAnonymously();

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
        // Obtener datos enviados desde RegisterPage2
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String name = getIntent().getStringExtra("name");
        String lastName = getIntent().getStringExtra("lastName");
        String gender = getIntent().getStringExtra("gender");
        String idNumber = getIntent().getStringExtra("idNumber");
        String address = getIntent().getStringExtra("address");

        if (email != null && password != null) {
            // Crear el usuario en Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Registrar datos adicionales en Firestore
                                saveUserData(user, name, lastName, gender, idNumber, address);
                            }
                        } else {
                            Log.w("RegisterPage3", "Error en la creación de usuario", task.getException());
                            Toast.makeText(this, "Error al crear usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Datos de autenticación faltantes", Toast.LENGTH_SHORT).show();
        }
    }


    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Log.w("RegisterPage3", "signInAnonymously failed", task.getException());
                        Toast.makeText(this, "Falla en la autenticación anónima", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("RegisterPage3", "Inicio de sesión anónimo exitoso");
                    }
                });
    }

    private void registerUser() {
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String name = getIntent().getStringExtra("name");
        String lastName = getIntent().getStringExtra("lastName");
        String gender = getIntent().getStringExtra("gender");
        String idNumber = getIntent().getStringExtra("idNumber");
        String address = getIntent().getStringExtra("address");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Usuario registrado y autenticado exitosamente
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Llama a saveUserData con todos los parámetros necesarios
                        saveUserData(user, name, lastName, gender, idNumber, address);
                    } else {
                        // Si la autenticación falla, muestra un mensaje de error
                        Toast.makeText(this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveUserData(FirebaseUser user, String name, String lastName, String gender, String idNumber, String address) {
        User userObject = new User(
                name,
                lastName,
                user.getEmail(),
                gender,
                idNumber,
                getIntent().getStringExtra("password"),
                address,
                uploadedImageUri.toString()
        );

        db.collection("users").document(user.getUid()).set(userObject)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registro completado y datos guardados", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginPage.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("RegisterPage3", "Error al guardar datos: " + e.getMessage());
                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}