package com.example.slagalica.MultiPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.ResourceHelper;
import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.MultiPlayerActivity;
import com.example.slagalica.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerListAdapter extends ArrayAdapter<Player> {
    private Context mContext;
    private int resourceId;
    private List<Player> finalPlayers;
    private List<Player> allPlayers;
    private Filter filter;

    public PlayerListAdapter(@NonNull Context context, int resource, @NonNull List<Player> objects) {
        super(context, resource, objects);
        mContext = context;
        resourceId = resource;
        allPlayers = objects;
        finalPlayers = new ArrayList<>(objects);
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Player> filteredPlayers = new ArrayList<>();
                if (constraint == null || constraint.length() == 0)
                {
                    filteredPlayers.addAll(finalPlayers);
                }
                else
                {
                    for (Player player: finalPlayers) {
                        String filtederSequence = constraint.toString().trim().toLowerCase();
                        String name = player.getName();
                        String lastName = player.getLastname();
                        String username = player.getUsername().toLowerCase();
                        String concatenated = name.trim().toLowerCase() + lastName.trim().toLowerCase();
                        if (username.contains(filtederSequence) || concatenated.contains(filtederSequence))
                        {
                            filteredPlayers.add(player);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredPlayers;

                return  results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
               allPlayers.clear();
               allPlayers.addAll((List)results.values);
               notifyDataSetInvalidated();
            }
        };
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name = Objects.requireNonNull(getItem(position)).getName();
        String lastName = Objects.requireNonNull(getItem(position)).getLastname();
        final String username = Objects.requireNonNull(getItem(position)).getUsername();

        final Player player = new Player();
        player.setName(name);
        player.setLastname(lastName);
        player.setUsername(username);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(resourceId,parent,false);

        TextView textViewName = convertView.findViewById(R.id.nameMultiplayer);
        TextView textViewLastName = convertView.findViewById(R.id.lastNameMultiplayer);
        TextView textViewUserName = convertView.findViewById(R.id.usernameMultiplayer);
        final Button button = convertView.findViewById(R.id.buttonChallenge);

        textViewName.setText(player.getName());
        textViewLastName.setText(player.getLastname());
        textViewUserName.setText(player.getUsername());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (MainActivity.player.getGameId().equals("") || MainActivity.player.getGameId().equals(" "))
                {
                    String keyForGame = ResourceHelper.getInstance(null).createRandomKey(MainActivity.player.getUsername());
                    MainActivity.player.setGameId(keyForGame);
                    MainActivity.player.setIdInGame(username);
                }
            ConnectionController.getInstance().challengeSomeone(mContext,username, MainActivity.player);
                ((MultiPlayerActivity)mContext).setDialogAwait(DialogBuilder.createWaitingAlert(username,mContext).create());
                ((MultiPlayerActivity)mContext).getDialogAwait().show();
               ConnectionController.getInstance().setListenerChallengeResponse(ConnectionController.getInstance().waitingForAnswerListener(mContext,username));
            }
        });


        return convertView;
    }



    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }
}
