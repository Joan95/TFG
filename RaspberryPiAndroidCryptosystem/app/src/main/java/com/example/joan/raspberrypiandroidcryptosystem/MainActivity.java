package com.example.joan.raspberrypiandroidcryptosystem;

import android.content.Intent;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity
{
    /*Static call to ShowToasts, just to get de Singleton. */
    private ShowToasts myToasts = ShowToasts.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listMain = findViewById(R.id.listMain);
        Button buttonBluetooth = findViewById(R.id.buttonBluetooth);

        String[] comments = new String[]
                {
                    "1st - Achieve the connection between device and Raspberry Pi board",
                    "2nd - Raspberry will execute a service",
                    "3rd - Choose a file to be encrypt",
                    "4th - Also you can choose a file to be decrypt, but three step will be required",
                    "5th - See %CPU %MEM monitoring",
                    "That's all!"
                };

        ArrayAdapter adapterList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,comments);
        listMain.setAdapter(adapterList);

        listMain.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                myToasts.show(MainActivity.this,"Just click on the button, this list is not interactive.");
            }
        });

        buttonBluetooth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, ShowDevices.class);
                startActivity(intent);
            }
        });
    }

    protected void onDestroy()
    {
        android.os.Process.killProcess(Process.myPid());
        super.onDestroy();
    }
}
