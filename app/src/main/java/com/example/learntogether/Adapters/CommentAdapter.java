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
import com.example.learntogether.FromAPI.Comment;
import com.example.learntogether.R;

public class CommentAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;

    private ArrayList<Comment> objects;


    public CommentAdapter(Context context, ArrayList<Comment> objList) {
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

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_comment, parent, false);
        }

        Comment thisComment = getComment(position);
        if (thisComment.Avatar != null) {
            ((ImageView) view.findViewById(R.id.ivAvatar)).setImageBitmap(thisComment.Avatar);
        }

        ((TextView) view.findViewById(R.id.tvAuthor)).setText(thisComment.Author);
        ((TextView) view.findViewById(R.id.tvText)).setText(thisComment.Text);
        ((TextView) view.findViewById(R.id.tvDateTime)).setText(thisComment.DateTime);

        Button btnAttachment = view.findViewById(R.id.btnAttachment);
        if (thisComment.Attachment == null) {
            btnAttachment.setVisibility(View.GONE);
        }
        else {
            btnAttachment.setOnClickListener(l -> {});
        }

        View finalView = view;

        ImageButton ivMenu = view.findViewById(R.id.ibMenu);

        if (thisComment.ID_Author == ConnectionManager.ID_Account || ConnectionManager.AllowedModerateComments)
            ivMenu.setOnClickListener(l -> showPopupMenu(finalView));

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

    Comment getComment(int position) {
        return ((Comment) getItem(position));
    }
}