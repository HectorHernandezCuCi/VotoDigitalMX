package com.mx.votodigitalmx;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mx.votodigitalmx.model.Article;


public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Article[] articles = (Article[]) getIntent().getParcelableArrayExtra("newsArticles");
        TextView newsTextView = findViewById(R.id.newsTextView);

        if (articles != null && articles.length > 0) {
            StringBuilder news = new StringBuilder();
            for (Article article : articles) {
                news.append(article.getTitle()).append("\n");
            }
            newsTextView.setText(news.toString());
        } else {
            newsTextView.setText("No hay noticias disponibles.");
        }
    }
}
