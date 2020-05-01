package com.example.asus1.pm.PMValue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus1.pm.AppSharePreferenceMrg;
import com.example.asus1.pm.BaseActivity;
import com.example.asus1.pm.R;

public class PMValueActivity extends BaseActivity {

    private ImageView mBack;
    private EditText mEditValue;
    private TextView mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmvalue);
        init();
    }

    private void init(){
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditValue = findViewById(R.id.edit_value);
        mSave = findViewById(R.id.tv_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mEditValue.getText().toString();
                AppSharePreferenceMrg.put(AppSharePreferenceMrg.PM_VALUE,
                        PMValueActivity.this,"value",value);
                Toast.makeText(PMValueActivity.this,
                        "保存成功",Toast.LENGTH_SHORT).show();
                mEditValue.setText(value);
            }
        });
        getValue();
    }

    private void getValue(){
        String value = (String) AppSharePreferenceMrg.get(AppSharePreferenceMrg.PM_VALUE,
                PMValueActivity.this,"value","0");
        mEditValue.setText(value);
    }
}
