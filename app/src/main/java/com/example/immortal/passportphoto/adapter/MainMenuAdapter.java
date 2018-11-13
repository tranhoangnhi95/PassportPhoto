package com.example.immortal.passportphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.model.MainMenu;

import java.util.ArrayList;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ItemHolder> {
    public Context context;
    public int resource;
    public ArrayList<MainMenu> objects;

    public MainMenuAdapter(Context context, int resource, ArrayList<MainMenu> objects) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_mainmenu_item, null);
        ItemHolder itemHolder = new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        MainMenu mainMenu = objects.get(position);
        holder.txtName.setMaxLines(1);
        holder.txtName.setEllipsize(TextUtils.TruncateAt.END);
        holder.txtName.setText(mainMenu.getName());

        holder.imgImage.setImageResource(mainMenu.getImage());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        public ImageView imgImage;
        public TextView txtName;

        public ItemHolder(View itemView) {
            super(itemView);
            this.imgImage = itemView.findViewById(R.id.img_MMImage);
            this.txtName = itemView.findViewById(R.id.txt_MMName);
        }
    }
}
