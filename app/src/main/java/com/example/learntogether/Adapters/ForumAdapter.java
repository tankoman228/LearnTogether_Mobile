package com.example.learntogether.Adapters;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.example.learntogether.API.ConnectionManager;
import com.example.learntogether.FromAPI.ForumAsk;
import com.example.learntogether.R;

public class ForumAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;

    private ArrayList<ForumAsk> objects;


    public  ForumAdapter(Context context, ArrayList<ForumAsk> objList) {
        ctx = context;
        objects = objList;

        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        view = lInflater.inflate(R.layout.item_forum, parent, false);


        ForumAsk thisForumAsk = getForumAsk(position);

        ((TextView) view.findViewById(R.id.tvAuthor)).setText(thisForumAsk.AuthorTitle);
        ((TextView) view.findViewById(R.id.tvTitle)).setText(thisForumAsk.Title);
        ((TextView) view.findViewById(R.id.tvText)).setText(thisForumAsk.Text);
        ((TextView) view.findViewById(R.id.tvDateTime)).setText(thisForumAsk.WhenAdd);
        ((TextView) view.findViewById(R.id.tvCommentsNum)).setText(String.valueOf(thisForumAsk.CommentsFound));


        View finalView = view;

        ImageButton ivMenu = view.findViewById(R.id.ibMenu);

        if (thisForumAsk.ID_Author == ConnectionManager.ID_Account || ConnectionManager.AllowedModerateComments)
            ivMenu.setOnClickListener(l -> {
                showPopupMenu(finalView);
            });

        return view;
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.comment_moderator_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {

            int i = menuItem.getItemId();
            if (i == R.id.action_delete) {

                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    ForumAsk getForumAsk(int position) {
        return ((ForumAsk) getItem(position));
    }
}
