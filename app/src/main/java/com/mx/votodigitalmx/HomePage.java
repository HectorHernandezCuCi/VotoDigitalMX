package com.mx.votodigitalmx;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mx.votodigitalmx.api.GNewsApi;
import com.mx.votodigitalmx.model.Article;
import com.mx.votodigitalmx.model.GNewsResponse;
import com.mx.votodigitalmx.activities.PanActivity;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private TextView welcomeTextView;
    private static final int SETTINGS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        ImageView panIcon = findViewById(R.id.pan);
        ImageView prdIcon = findViewById(R.id.prd);
        ImageView alianzaIcon = findViewById(R.id.alianza);
        ImageView verdeIcon = findViewById(R.id.verde);

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        welcomeTextView = findViewById(R.id.greetingTextView);

        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Settings.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        });

        // Configurar el botón para navegar a MainActivity
        Button btnIrAMain = findViewById(R.id.btnIrAMain);
        btnIrAMain.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, MainActivity.class);
            startActivity(intent);
        });

        panIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, PanActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        });

        prdIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, PrdActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        });

        alianzaIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, AlianzaActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        });

        verdeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, VerdeActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        });


        loadUserName(); // Llama a la función para cargar el nombre de usuario
        fetchNews(); // Cargar noticias
    }

    private void loadUserName() {
        String uid = getUserId(); // Obtener el UID
        if (uid != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name");
                        welcomeTextView.setText("Hola, " + userName);
                    } else {
                        Toast.makeText(HomePage.this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomePage.this, "Error al cargar el nombre: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(HomePage.this, "No estás autenticado. Inicia sesión.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    private void fetchNews() {
        GNewsApi apiService = GNewsApi.create();
        Call<GNewsResponse> call = apiService.getTopHeadlines("mx", "es", "9242e5a37ce718ec2e89fee4a65e8bd3");
        call.enqueue(new Callback<GNewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<GNewsResponse> call, @NonNull Response<GNewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    newsRecyclerView.setAdapter(new NewsAdapter(articles, HomePage.this));
                } else {
                    Toast.makeText(HomePage.this, "No se pudieron obtener noticias.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GNewsResponse> call, @NonNull Throwable t) {
                Toast.makeText(HomePage.this, "Error al obtener noticias: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String updatedUserName = data.getStringExtra("updatedUserName");
            if (updatedUserName != null) {
                welcomeTextView.setText("Hola, " + updatedUserName);
            }
        }
    }
}
