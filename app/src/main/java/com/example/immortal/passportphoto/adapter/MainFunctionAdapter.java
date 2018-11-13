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
import com.example.immortal.passportphoto.model.MainFunction;
import com.example.immortal.passportphoto.model.MainMenu;

import java.util.ArrayList;

public class MainFunctionAdapter extends RecyclerView.Adapter<MainFunctionAdapter.ItemHolder> {
    public Context context;
    public int resource;
    public ArrayList<MainFunction> objects;

    public MainFunctionAdapter(Context context, int resource, ArrayList<MainFunction> objects) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_mainfunction_item, null);
        MainFunctionAdapter.ItemHolder itemHolder = new MainFunctionAdapter.ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        MainFunction mainFunction = objects.get(position);
        holder.txtName.setMaxLines(1);
        holder.txtName.setEllipsize(TextUtils.TruncateAt.END);
        holder.txtName.setText(mainFunction.getName());

        holder.imgImage.setImageResource(mainFunction.getImage());
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
            this.imgImage = itemView.findViewById(R.id.img_MFImage);
            this.txtName = itemView.findViewById(R.id.txt_MFName);
        }
    }
}
