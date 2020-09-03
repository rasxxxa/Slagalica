package com.example.slagalica.MultiPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.R;

import java.util.List;
import java.util.Objects;

public class MessageListAdapter extends ArrayAdapter<Message> {

    private Context mContext;
    private int resourceId;


    public MessageListAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
        mContext = context;
        resourceId = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int id = Objects.requireNonNull(getItem(position)).getSenderId();
        String name = Objects.requireNonNull(getItem(position)).getSenderName();
        String lastname = Objects.requireNonNull(getItem(position)).getSenderLastName();
        String message = Objects.requireNonNull(getItem(position)).getMessage();
        String fullName = name + " " + lastname;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(resourceId,parent,false);
        TextView textViewFullName = convertView.findViewById(R.id.senderInfo);
        TextView textViewMessage = convertView.findViewById(R.id.senderMessage);
        textViewFullName.setText(fullName);
        textViewMessage.setText(message);
        if (id == MainActivity.typeOfPlayer)
        {
            LinearLayout layoutParrent = (LinearLayout) textViewFullName.getParent();
            layoutParrent.setGravity(Gravity.START);
            layoutParrent.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            layoutParrent.setTextDirection(View.TEXT_DIRECTION_LTR);
        }else
        {
            LinearLayout layoutParrent = (LinearLayout) textViewFullName.getParent();
            layoutParrent.setGravity(Gravity.END);
            layoutParrent.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            layoutParrent.setTextDirection(View.TEXT_DIRECTION_RTL);
        }

        return  convertView;
    }
}
