package com.brangerbriz.testchatapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.google.gson.Gson;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONObject;



import java.util.ArrayList;
import java.util.Date;


public class MainActivity  extends AppCompatActivity implements View.OnClickListener{


    EditText messageInput;
    Button sendButton;
    String MESSAGES_ENDPOINT="Your endpoint";

    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get our input field by its ID
        messageInput = (EditText) findViewById(R.id.message_input);


        // get our button by its ID
        sendButton = (Button) findViewById(R.id.send_button);

        // set its click listener
        sendButton.setOnClickListener(this);

        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        final ListView messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        PusherOptions options = new PusherOptions();
        options.setCluster("ap1");

        // initialize Pusher
        Pusher pusher = new Pusher("APP_KEY_given on pusher",options);

        // connect to the Pusher API
        pusher.connect( new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.e("pusher: State"," changed to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.e("pusher:problem" ," connecting! msg:" + message);
            }
        }, ConnectionState.ALL);


        // subscribe to our "messages" channel
        Channel channel = pusher.subscribe("test_channel");

        // listen for the "new_message" event
        channel.bind("my_event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                Log.e("DATA:  ", data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Message message = gson.fromJson(data, Message.class);
                        messageAdapter.add(message);

                        // have the ListView scroll down to the new message
                        messagesView.setSelection(messageAdapter.getCount() - 1);
                    }

                });
            }
        });


    }



    private void postMessage()  {

        String text = messageInput.getText().toString();

        // return if the text is blank
        if (text.equals("")) {
            return;
        }


        RequestParams params = new RequestParams();

        // set our JSON object
        params.put("message", text);
        params.put("user", "burned text");
        //params.put("time", new Date().getTime());
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(MESSAGES_ENDPOINT, params, new com.loopj.android.http.JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageInput.setText("");
                    }
                });
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(
                        getApplicationContext(),
                        "Something went wrong :(",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        postMessage();
    }
}
