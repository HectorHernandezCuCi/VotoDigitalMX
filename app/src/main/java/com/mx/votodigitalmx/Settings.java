package com.mx.votodigitalmx;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.HashMap;

import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText genderInput;
    private EditText emailInput;
    private EditText addressInput;
    private EditText idNumberInput;
    private String userId; // UID del usuario
    private Button logoutButton;
    private Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inicializar los EditTexts
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        genderInput = findViewById(R.id.genderInput);
        emailInput = findViewById(R.id.emailInput);
        addressInput = findViewById(R.id.addressInput);
        idNumberInput = findViewById(R.id.idNumberInput);

        // Inicializar UID TextView
        TextView uidValue = findViewById(R.id.uidValue);

        // Obtener el ID del usuario autenticado
        userId = getUserId();
        loadUserData();

        // Mostrar el UID en el TextView
        uidValue.setText(userId);

        // Configurar el botón de logout
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Settings.this, LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Configurar el botón de guardar cambios
        saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(v -> saveUserName());
    }

    private void saveUserName() {
        String uid = getUserId();
        if (uid != null) {
            String newUserName = firstNameInput.getText().toString();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", newUserName);
            updatedData.put("lastName", lastNameInput.getText().toString());
            updatedData.put("gender", genderInput.getText().toString());
            updatedData.put("email", emailInput.getText().toString());
            updatedData.put("address", addressInput.getText().toString());
            updatedData.put("idNumber", idNumberInput.getText().toString());

            userRef.update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Settings.this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                        updateHomePageName(newUserName);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(Settings.this, "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void updateHomePageName(String newUserName) {
        Intent intent = new Intent();
        intent.putExtra("updatedUserName", newUserName);
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    private void loadUserData() {
        String uid = getUserId();
        if (uid != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        firstNameInput.setText(document.getString("name"));
                        lastNameInput.setText(document.getString("lastName"));
                        genderInput.setText(document.getString("gender"));
                        emailInput.setText(document.getString("email"));
                        addressInput.setText(document.getString("address"));
                        idNumberInput.setText(document.getString("idNumber"));
                    } else {
                        Toast.makeText(Settings.this, "El usuario no se encontró.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Exception e = task.getException();
                    Toast.makeText(Settings.this, "Error al cargar datos: " + (e != null ? e.getMessage() : "Error desconocido."), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Settings.this, "No estás autenticado. Inicia sesión.", Toast.LENGTH_SHORT).show();
        }
    }
}
