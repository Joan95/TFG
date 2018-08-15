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
                        "",
                        "1st - Achieve the connection between device and Raspberry Pi board by scanning and binding via Bluetooth",
                        "2nd - Be sure to have your Bluetooth on",
                        "3nd - Be sure of being running the correct service in the Board",
                        "4rd - Choose an option to be executed for HSM or Raspberry",
                        "5th - Wait for the results",
                        "That's all!",
                        "",
                        "Press the button below to start."
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
