package com.mx.votodigitalmx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterPage extends AppCompatActivity {

    private String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button genderMaleButton = findViewById(R.id.gender_male);
        Button genderFemaleButton = findViewById(R.id.gender_female);
        Button nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterPage.this, RegisterPage2.class);
                startActivity(intent);
            }
        });

        // Establecer los listeners para los botones
        genderMaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGenderSelection(genderMaleButton, genderFemaleButton, "Male");
            }
        });

        genderFemaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGenderSelection(genderFemaleButton, genderMaleButton, "Female");
            }
        });
    }

    private void updateGenderSelection(Button selectedButton, Button unselectedButton, String gender) {
        selectedButton.setSelected(true); // Marca el botón como seleccionado
        unselectedButton.setSelected(false); // Marca el botón como no seleccionado

        // Actualiza el valor seleccionado
        selectedGender = gender;
    }
}
