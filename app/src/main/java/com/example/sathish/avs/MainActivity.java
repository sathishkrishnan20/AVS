package com.example.sathish.avs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView addMember, viewMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addMember= (TextView)findViewById(R.id.addMember);
        viewMember= (TextView)findViewById(R.id.viewMember);

        addMember.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(), AddMembers.class);
                startActivity(intent);
            }

        });

        viewMember.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(), ViewMembers.class);
                startActivity(intent);
            }

        });


    }
}
