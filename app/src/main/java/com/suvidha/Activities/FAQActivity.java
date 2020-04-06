package com.suvidha.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.suvidha.R;

public class FAQActivity extends AppCompatActivity {

    TextView tvFAQ;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);
//        toolbar = findViewById(R.id.default_toolbar);
        tvFAQ = findViewById(R.id.faqtext);
//        manageToolbar();
    }

//    void manageToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(title);
//    }
}
