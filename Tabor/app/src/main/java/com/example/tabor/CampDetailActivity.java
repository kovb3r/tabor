package com.example.tabor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CampDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp_detail);

        TextView nameTextView = findViewById(R.id.campName);
        TextView descriptionTextView = findViewById(R.id.campDescription);
        TextView priceTextView = findViewById(R.id.campPrice);
        RatingBar ratingBar = findViewById(R.id.campRating);
        ImageView imageView = findViewById(R.id.campImage);

        // Adatok lekérése az intentből
        String name = getIntent().getStringExtra("campName");
        String description = getIntent().getStringExtra("campDescription");
        String price = getIntent().getStringExtra("campPrice");
        float rating = getIntent().getFloatExtra("campRating", 0);
        int imageResId = getIntent().getIntExtra("campImageResId", R.drawable.ic_placeholder);

        nameTextView.setText(name);
        descriptionTextView.setText(description);
        priceTextView.setText(price);
        ratingBar.setRating(rating);
        imageView.setImageResource(imageResId);
    }
}
