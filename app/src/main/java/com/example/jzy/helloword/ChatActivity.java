package com.example.jzy.helloword;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

/**
 * Created by jzy on 8/5/17.
 */

public class ChatActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chart_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

    }

    private void initValidationEt() {
        final MaterialEditText validationEt = (MaterialEditText) findViewById(R.id.validationEt);
        validationEt.addValidator(new RegexpValidator("Only Integer Valid!", "\\d+"));
        final Button validateBt = (Button) findViewById(R.id.validateBt);
        validateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate
                validationEt.validate();
            }
        });
    }
}
