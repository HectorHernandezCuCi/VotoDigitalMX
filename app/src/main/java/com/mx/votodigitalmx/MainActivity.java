package com.mx.votodigitalmx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String candidatoSeleccionado;
    private Button btnConfirmarVoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configuración de botones de votación y confirmación
        Button btnVotarCandidato1 = findViewById(R.id.btnVotarCandidato1);
        Button btnVotarCandidato2 = findViewById(R.id.btnVotarCandidato2);
        btnConfirmarVoto = findViewById(R.id.btnConfirmarVoto);

        btnVotarCandidato1.setOnClickListener(v -> seleccionarCandidato("candidato1"));
        btnVotarCandidato2.setOnClickListener(v -> seleccionarCandidato("candidato2"));
        btnConfirmarVoto.setOnClickListener(v -> confirmarVoto());
    }

    private void seleccionarCandidato(String candidato) {
        candidatoSeleccionado = candidato;
        btnConfirmarVoto.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Candidato seleccionado: " + candidato, Toast.LENGTH_SHORT).show();
    }

    private void confirmarVoto() {
        if (candidatoSeleccionado != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                Map<String, Object> voto = new HashMap<>();
                voto.put("userId", userId);
                voto.put("candidato", candidatoSeleccionado);
                voto.put("fecha", System.currentTimeMillis());

                db.collection("votos").document(userId).set(voto)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Voto registrado con éxito", Toast.LENGTH_SHORT).show();
                            // Redirigir a la actividad de conteo de votos
                            startActivity(new Intent(MainActivity.this, ConteoVotosActivity.class));
                            finish(); // Finalizar la actividad para que no pueda regresar y cambiar su voto
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al registrar el voto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Inicia sesión para votar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
