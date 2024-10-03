package com.mx.votodigitalmx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterPage extends AppCompatActivity {
    private EditText nameInput, lastNameInput, idNumberInput;
    private Button nextButton, maleButton, femaleButton;
    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Referencias a los campos de entrada
        nameInput = findViewById(R.id.name_user_input);
        lastNameInput = findViewById(R.id.lastname_user_input);
        idNumberInput = findViewById(R.id.editTextNumber);
        nextButton = findViewById(R.id.next_button);
        maleButton = findViewById(R.id.gender_male);
        femaleButton = findViewById(R.id.gender_female);

        // Configurar botones de género
        maleButton.setOnClickListener(view -> {
            selectedGender = "Male";
            maleButton.setBackgroundColor(getResources().getColor(R.color.colorButtonPressed));
            femaleButton.setBackgroundColor(getResources().getColor(R.color.colorButtonNormal));
        });

        femaleButton.setOnClickListener(view -> {
            selectedGender = "Female";
            maleButton.setBackgroundColor(getResources().getColor(R.color.colorButtonNormal));
            femaleButton.setBackgroundColor(getResources().getColor(R.color.colorButtonPressed));
        });

        // Botón para ir a la siguiente pantalla
        nextButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String idNumber = idNumberInput.getText().toString().trim();

            if (name.isEmpty() || lastName.isEmpty() || idNumber.isEmpty()) {
                Toast.makeText(RegisterPage.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviar datos a la siguiente pantalla
            Intent intent = new Intent(RegisterPage.this, RegisterPage2.class);
            intent.putExtra("name", name);
            intent.putExtra("lastName", lastName);
            intent.putExtra("idNumber", idNumber);
            intent.putExtra("gender", selectedGender);
            startActivity(intent);
        });
    }
}
