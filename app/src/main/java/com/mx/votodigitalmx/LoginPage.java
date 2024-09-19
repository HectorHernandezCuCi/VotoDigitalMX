package com.mx.votodigitalmx;

import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias a los campos de entrada y el botón
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.loginButton);

        // Manejamos el clic en el botón de iniciar sesión
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputs();
            }
        });

        // Referencia al TextView que dispara la acción
        TextView registerTextView = findViewById(R.id.create_account);

        // Manejamos el clic en el TextView para ir a la página de registro
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent); // Inicia la actividad de registro
            }
        });

        // Mostrar el Toast de bienvenida al iniciar la actividad
        Toast.makeText(this, "¡Bienvenido a la aplicación!", Toast.LENGTH_SHORT).show();
    }

    private void validateInputs() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isValid = true;

        if (email.isEmpty()) {
            emailEditText.setError("El correo electrónico es obligatorio");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("La contraseña es obligatoria");
            isValid = false;
        }

        if (isValid) {
            // Si todos los campos están llenos, puedes continuar con el proceso de inicio de sesión
            Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
            // Aquí puedes agregar la lógica para el inicio de sesión, por ejemplo, llamar a Firebase Authentication
        }
    }
}
