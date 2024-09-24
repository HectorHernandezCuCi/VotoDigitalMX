package com.mx.votodigitalmx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterPage2 extends AppCompatActivity {
    private EditText emailInput, passwordInput, addressInput;
    private Button nextButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Referencias a los campos de entrada
        emailInput = findViewById(R.id.email_user_input);
        passwordInput = findViewById(R.id.password_user_input);
        addressInput = findViewById(R.id.adress_user_input);
        nextButton = findViewById(R.id.next_button);

        // Recibe los datos de la primera actividad
        String name = getIntent().getStringExtra("name");
        String lastName = getIntent().getStringExtra("lastName");
        String idNumber = getIntent().getStringExtra("idNumber");
        String gender = getIntent().getStringExtra("gender");

        nextButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                Toast.makeText(RegisterPage2.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crea el objeto User
            User user = new User(name, lastName, email, gender, idNumber, password, address);

            // Envía el objeto a Firestore
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(RegisterPage2.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        // Redirige a RegisterPage3
                        Intent intent = new Intent(RegisterPage2.this, RegisterPage3.class);
                        intent.putExtra("userId", documentReference.getId());
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegisterPage2.this, "Error en el registro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
