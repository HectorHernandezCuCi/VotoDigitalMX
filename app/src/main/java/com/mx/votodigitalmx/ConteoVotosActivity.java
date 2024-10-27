package com.mx.votodigitalmx;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConteoVotosActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo_votos);

        db = FirebaseFirestore.getInstance();

        contarVotos();
    }

    private void contarVotos() {
        db.collection("votos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int votosCandidato1 = 0;
                    int votosCandidato2 = 0;

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String candidato = document.getString("candidato");
                        if ("candidato1".equals(candidato)) {
                            votosCandidato1++;
                        } else if ("candidato2".equals(candidato)) {
                            votosCandidato2++;
                        }
                    }

                    // Muestra el conteo de votos en un TextView
                    TextView conteoVotosText = findViewById(R.id.conteoVotosText);
                    conteoVotosText.setText("Candidato 1: " + votosCandidato1 + "\nCandidato 2: " + votosCandidato2);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al contar votos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
