package com.suvidha.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.suvidha.R;

public class FAQActivity extends AppCompatActivity {

    TextView tvFAQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);
        tvFAQ = findViewById(R.id.faqtext);
    }
}
