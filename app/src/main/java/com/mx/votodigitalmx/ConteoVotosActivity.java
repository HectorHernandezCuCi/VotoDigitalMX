package com.mx.votodigitalmx;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class ConteoVotosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView conteoVotosText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo_votos);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        conteoVotosText = findViewById(R.id.conteoVotosText);

        // Contar los votos
        contarVotos();
    }

    private void contarVotos() {
        CollectionReference votosRef = db.collection("votos");

        // Usar un listener para recibir actualizaciones en tiempo real
        votosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(ConteoVotosActivity.this, "Error al cargar votos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshots != null) {
                    int conteoPan = 0;
                    int conteoPrd = 0;
                    int conteoAlianza = 0;
                    int conteoVerde = 0;

                    for (QueryDocumentSnapshot document : snapshots) {
                        String candidato = document.getString("candidato");
                        if (candidato != null) {
                            switch (candidato) {
                                case "PAN":
                                    conteoPan++;
                                    break;
                                case "PRD":
                                    conteoPrd++;
                                    break;
                                case "Alianza":
                                    conteoAlianza++;
                                    break;
                                case "Verde":
                                    conteoVerde++;
                                    break;
                            }
                        }
                    }

                    // Mostrar los resultados en el TextView
                    String resultados = "Resultados:\n" +
                            "PAN: " + conteoPan + "\n" +
                            "PRD: " + conteoPrd + "\n" +
                            "Alianza: " + conteoAlianza + "\n" +
                            "Verde: " + conteoVerde;
                    conteoVotosText.setText(resultados);
                }
            }
        });
    }
}
