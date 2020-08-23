package com.example.slagalica.Activities.Chat;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.MultiPlayer.Message;
import com.example.slagalica.MultiPlayer.Player;
import com.example.slagalica.R;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private EditText editTextMessage;
    private ChildEventListener listener;
    private ListView mainListView;
    private ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
    }

    public void initialize()
    {
        connectionController = ConnectionController.getInstance();
        mainListView = findViewById(R.id.mainListView);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey,MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey),getResources().getString(R.string.color1));
        constraintLayout = findViewById(R.id.layoutChatActivity);
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        editTextMessage = findViewById(R.id.editTextMessage);
        listener = connectionController.chatListener(this,mainListView);
    }

    public void sendMessage(View view)
    {
        String message = editTextMessage.getText().toString();
        if (message.length() < 1)
        {
            return;
        }
        ArrayList<Message> messages = SinglePlayerActivity.game.getMessages();
        Message messageForDatabase = new Message();
        final Player player = MainActivity.player;
        messageForDatabase.setSenderId(MainActivity.typeOfPlayer);
        messageForDatabase.setSenderName(player.name);
        messageForDatabase.setSenderLastName(player.lastname);
        messageForDatabase.setMessage(message);
        messages.add(messageForDatabase);
        SinglePlayerActivity.game.setMessages(messages);
        connectionController.sendMessange(this, SinglePlayerActivity.game.getMessages());
    }
}