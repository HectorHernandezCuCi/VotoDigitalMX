package com.mx.votodigitalmx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterPage2 extends AppCompatActivity {

    private EditText emailInput, passwordInput, addressInput;
    private Button nextButton;
    private TextView errorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);

        // Referencias a los campos de entrada
        emailInput = findViewById(R.id.email_user_input);
        passwordInput = findViewById(R.id.password_user_input);
        addressInput = findViewById(R.id.adress_user_input);
        nextButton = findViewById(R.id.next_button);
        errorMessage = findViewById(R.id.error_message);

        // Configurar el evento de clic del botón "Siguiente"
        nextButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();

            // Validar campos vacíos
            if (email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                errorMessage.setText("Por favor llena todos los campos");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            // Validar email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errorMessage.setText("Correo electrónico no válido");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            // Validar la contraseña
            if (!isPasswordValid(password)) {
                errorMessage.setText("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            // Si no hay errores, ocultar el mensaje de error y continuar
            errorMessage.setVisibility(View.GONE);

            // Pasar datos a la tercera pantalla sin autenticación
            Intent intent = new Intent(RegisterPage2.this, RegisterPage3.class);
            intent.putExtra("name", getIntent().getStringExtra("name")); // Trae el nombre de Register 1
            intent.putExtra("lastName", getIntent().getStringExtra("lastName")); // Trae el apellido de Register 1
            intent.putExtra("gender", getIntent().getStringExtra("gender")); // Trae el género de Register 1
            intent.putExtra("idNumber", getIntent().getStringExtra("idNumber")); // Número de identificación ingresado en Register 2
            intent.putExtra("address", address); // Dirección ingresada en Register 2
            intent.putExtra("email", email); // Correo electrónico
            intent.putExtra("password", password); // Contraseña
            startActivity(intent);
        });
    }

    // Validación de contraseña
    private boolean isPasswordValid(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }
}
