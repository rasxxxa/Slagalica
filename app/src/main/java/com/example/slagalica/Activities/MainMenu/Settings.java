package com.example.slagalica.Activities.MainMenu;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.MultiPlayer.ShortPlayerInfo;
import com.example.slagalica.R;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    public static final int[] Colors = {R.string.color1,R.string.color2,R.string.color3,R.string.color4,R.string.color5,R.string.color6,R.string.color7,R.string.color8,R.string.color9,R.string.color10,R.string.color11,R.string.color12};
    private ArrayList<Button> buttons;
    public static final String gameName = "Settings";
    private ConstraintLayout constraintLayout;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextUserName;
    private Boolean exists = null;
    private ConnectionController connectionController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialize();
    }

    public void initialize()
    {
        connectionController = ConnectionController.getInstance();
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextUserName = findViewById(R.id.editTextUserName);
        constraintLayout = findViewById(R.id.layoutSettings);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey,MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey),getResources().getString(R.string.color1));
        editTextName.setText(preferences.getString(getResources().getString(R.string.nameKey),""));
        editTextLastName.setText(preferences.getString(getResources().getString(R.string.lastnameKey),""));
        editTextUserName.setText(preferences.getString(getResources().getString(R.string.usernameKey),""));

        if (editTextUserName.getText().length() > 0)
        {
            editTextUserName.setClickable(false);
            editTextUserName.setEnabled(false);
        }
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        buttons = new ArrayList<>();
        for (int i = 1;i<= Colors.length;i++)
        {
            int id = getResources().getIdentifier(getResources().getString(R.string.buttonPrefix) + i + gameName, "id", getPackageName());
            Button button = findViewById(id);
            button.setBackgroundColor(Color.parseColor(getResources().getString(Colors[i-1])));
            buttons.add(button);
        }
    }

    public void click(View view)
    {
        Button button = (Button)view;
        int colorSelected = 0;
        for (int i = 0;i<buttons.size();i++)
        {
            if (button.getId() == buttons.get(i).getId())
            {
                colorSelected = i;
                break;
            }
        }
        constraintLayout.setBackgroundColor(Color.parseColor(getResources().getString(Colors[colorSelected])));
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getResources().getString(R.string.backgroundColorKey),getResources().getString(Colors[colorSelected]));
        editor.apply();
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    private void checkUser()
    {
        exists = true;
        if (!editTextUserName.isEnabled())
        {
            return;
        }

        try
        {
            connectionController.checkForUserInListOfPlayersListener(this,editTextUserName);
        }catch (Exception e)
        {
            Toast.makeText(this,getResources().getString(R.string.connectionProblem),Toast.LENGTH_LONG).show();
        }
    }

    public void check(View view)
    {
        checkUser();

    }
    @SuppressLint("ApplySharedPref")
    public void apply(View view)
    {
        if (exists == null)
        {
            Toast.makeText(this,getResources().getString(R.string.buttonClickCheck),Toast.LENGTH_SHORT).show();
            return;
        }
        if (exists)
        {
            return;
        }
        try {
            String name = editTextName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String userName = editTextUserName.getText().toString();
            if (!(exists || lastName.length() < 3 || name.length() < 3))
            {
                try {
                    final ShortPlayerInfo shortPlayerInfo = new ShortPlayerInfo(userName,name,lastName);
                    SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey,MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getResources().getString(R.string.nameKey),name);
                    editor.putString(getResources().getString(R.string.lastnameKey),lastName);
                    editor.putString(getResources().getString(R.string.usernameKey),userName);
                    editor.commit();
                    connectionController.registerUser(this,shortPlayerInfo);
                    editor.apply();
                    Toast.makeText(this,getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                    this.recreate();
                }catch (Exception e)
                {
                    Toast.makeText(this,getResources().getString(R.string.connectionProblem),Toast.LENGTH_SHORT).show();
                }
            }else
            {
                Toast.makeText(this,getResources().getString(R.string.parameterError),Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
            Log.e("Settings error: ",e.toString());
        }
    }
}
