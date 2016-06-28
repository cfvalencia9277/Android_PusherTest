package com.brangerbriz.testchatapp;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Fabian on 18/05/2016.
 */
public class MessageAdapter extends BaseAdapter {

    Context messageContext;
    ArrayList<Message> messageList;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        messageList = messages;
        messageContext = context;
    }




    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageViewHolder holder;

        if (convertView == null){
            LayoutInflater messageInflater = (LayoutInflater) messageContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            // create a view out of our `message.xml` file
            convertView = messageInflater.inflate(R.layout.message, null);

            // create a MessageViewHolder
            holder = new MessageViewHolder();

            // set the holder's properties to elements in `message.xml`
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.senderView = (TextView) convertView.findViewById(R.id.message_sender);
            holder.bodyView = (TextView) convertView.findViewById(R.id.message_body);

            // assign the holder to the view we will return
            convertView.setTag(holder);
        } else {

            // otherwise fetch an already-created view holder
            holder = (MessageViewHolder) convertView.getTag();
        }

        // get the message from its position in the ArrayList
        com.brangerbriz.testchatapp.Message message = (com.brangerbriz.testchatapp.Message) getItem(position);

        // set the elements' contents
        holder.bodyView.setText(message.message);
        holder.senderView.setText(message.user);

        // fetch the user's Twitter avatar from their username
        // and place it into the thumbnailImageView.
        Picasso.with(messageContext).
                load("https://twitter.com/" + "none"+ "/profile_image?size=original").
                placeholder(R.mipmap.ic_launcher).
                into(holder.thumbnailImageView);

        return convertView;
    }

    public void add(Message message){
        messageList.add(message);
        notifyDataSetChanged();
    }

    private static class MessageViewHolder {
        public ImageView thumbnailImageView;
        public TextView senderView;
        public TextView bodyView;
    }
}
