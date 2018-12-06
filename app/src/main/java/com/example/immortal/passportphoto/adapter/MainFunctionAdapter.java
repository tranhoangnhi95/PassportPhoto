package com.example.immortal.passportphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.model.MainFunction;
import com.example.immortal.passportphoto.myinterface.MainFunctiontItemClickListner;

import java.util.ArrayList;

public class MainFunctionAdapter extends RecyclerView.Adapter<MainFunctionAdapter.ItemHolder> {
    public Context context;
    public int resource;
    public ArrayList<MainFunction> objects;
    public SeekBar sbBrightness, sbContrast, sbSaturation, sbTemperature;
    public TextView txtValue;
    public MainFunctionAdapter(Context context, int resource, ArrayList<MainFunction> objects, SeekBar sbBrightness,
                               SeekBar sbContrast, SeekBar sbSaturation, SeekBar sbTemperature, TextView txtValue) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.sbBrightness = sbBrightness;
        this.sbContrast = sbContrast;
        this.sbSaturation = sbSaturation;
        this.sbTemperature = sbTemperature;
        this.txtValue = txtValue;
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

        holder.imgImage.setImageBitmap(mainFunction.getImage());

        holder.setMainFunctiontItemClickListner(new MainFunctiontItemClickListner() {
            @Override
            public void itemClick(View view, int position, boolean isLongClick) {
//                Toast.makeText(context, objects.get(position).getName().toString(), Toast.LENGTH_SHORT).show();
                switch (objects.get(position).getName().toString()) {
                    case "Sáng tối":
                        sbBrightness.setVisibility(View.VISIBLE);
                        sbContrast.setVisibility(View.INVISIBLE);
                        sbSaturation.setVisibility(View.INVISIBLE);
                        sbTemperature.setVisibility(View.INVISIBLE);
                        txtValue.setText(String.valueOf(sbBrightness.getProgress() - 100));
                        txtValue.setVisibility(View.VISIBLE);

                        break;
                    case "Tương phản":
                        sbBrightness.setVisibility(View.INVISIBLE);
                        sbContrast.setVisibility(View.VISIBLE);
                        sbSaturation.setVisibility(View.INVISIBLE);
                        sbTemperature.setVisibility(View.INVISIBLE);
                        txtValue.setText(String.valueOf(sbContrast.getProgress() - 100));
                        txtValue.setVisibility(View.VISIBLE);
                        break;
                    case "Bão hòa":
                        sbBrightness.setVisibility(View.INVISIBLE);
                        sbContrast.setVisibility(View.INVISIBLE);
                        sbSaturation.setVisibility(View.VISIBLE);
                        sbTemperature.setVisibility(View.INVISIBLE);
                        txtValue.setText(String.valueOf(sbSaturation.getProgress() - 100));
                        txtValue.setVisibility(View.VISIBLE);
                        break;
                    case "Nhiệt độ":
                        sbBrightness.setVisibility(View.INVISIBLE);
                        sbContrast.setVisibility(View.INVISIBLE);
                        sbSaturation.setVisibility(View.INVISIBLE);
                        sbTemperature.setVisibility(View.VISIBLE);
                        txtValue.setText(String.valueOf(sbTemperature.getProgress() - 100));
                        txtValue.setVisibility(View.VISIBLE);
                        break;
                    case "Tự động":
                        sbBrightness.setProgress(110);
                        sbContrast.setProgress(110);
                        sbSaturation.setProgress(110);
                        sbTemperature.setProgress(110);
                        sbBrightness.setVisibility(View.INVISIBLE);
                        txtValue.setText(String.valueOf(0));
                        sbContrast.setVisibility(View.INVISIBLE);
                        sbSaturation.setVisibility(View.INVISIBLE);
                        sbTemperature.setVisibility(View.INVISIBLE);
                        txtValue.setVisibility(View.INVISIBLE);
                        break;

                    default:
                        sbBrightness.setProgress(100);
                        sbContrast.setProgress(100);
                        sbSaturation.setProgress(100);
                        sbTemperature.setProgress(100);
                        sbBrightness.setVisibility(View.INVISIBLE);
                        txtValue.setText(String.valueOf(0));
                        sbContrast.setVisibility(View.INVISIBLE);
                        sbSaturation.setVisibility(View.INVISIBLE);
                        sbTemperature.setVisibility(View.INVISIBLE);
                        txtValue.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgImage;
        public TextView txtName;
        public MainFunctiontItemClickListner mainFunctiontItemClickListner;

        public ItemHolder(View itemView) {
            super(itemView);
            this.imgImage = itemView.findViewById(R.id.img_MFImage);
            this.txtName = itemView.findViewById(R.id.txt_MFName);

            itemView.setOnClickListener(this);
        }

        public void setMainFunctiontItemClickListner(MainFunctiontItemClickListner mainFunctiontItemClickListner) {
            this.mainFunctiontItemClickListner = mainFunctiontItemClickListner;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                default:
                    mainFunctiontItemClickListner.itemClick(view, getAdapterPosition(), false);

            }
        }
    }

}
