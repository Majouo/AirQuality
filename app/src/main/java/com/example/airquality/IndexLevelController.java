package com.example.airquality;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IndexLevelController extends AppCompatActivity {

    TextView indexName;
    TextView indexDesc;

    TextView distance;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_level);
        indexName = findViewById(R.id.indexName);
        indexDesc = findViewById(R.id.indexDesc);
        distance = findViewById(R.id.distance);
        progressBar = findViewById(R.id.progressBar);


    }


}
