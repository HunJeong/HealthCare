package com.example.jeonghun.heathcare.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jeonghun.heathcare.DB.Dao;
import com.example.jeonghun.heathcare.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText weightEditText;
    private EditText heightEditText;
    private EditText bmiEditText;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        ageEditText = (EditText)findViewById(R.id.ageEditText);
        weightEditText = (EditText)findViewById(R.id.weightEditText);
        heightEditText = (EditText)findViewById(R.id.heightEditText);
        bmiEditText = (EditText)findViewById(R.id.bmiEditText);

        initEditText();

        submitButton = (Button)findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameEditText.getText().toString().equals("")
                        && !ageEditText.getText().toString().equals("")
                        && !heightEditText.getText().toString().equals("")
                        && !weightEditText.getText().toString().equals("")
                        && !bmiEditText.getText().toString().equals("")) {
                    boolean t = false;
                    try {
                        JSONObject object = new JSONObject();
                        object.put("name", nameEditText.getText().toString());
                        object.put("age", Integer.valueOf(ageEditText.getText().toString()));
                        object.put("height", Double.valueOf(heightEditText.getText().toString()));
                        object.put("weight", Double.valueOf(weightEditText.getText().toString()));
                        object.put("bmi", Double.valueOf(bmiEditText.getText().toString()));

                        Dao.insertData(object);
                        t = true;
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (t) {
                        Toast.makeText(getApplicationContext(), "정보를 수정하였습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initEditText(){
        JSONObject object = Dao.getData();
        try{
            nameEditText.setText(object.getString("name"));
            ageEditText.setText(object.getString("age"));
            heightEditText.setText(object.getString("height"));
            weightEditText.setText(object.getString("weight"));
            bmiEditText.setText(object.getString("bmi"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
