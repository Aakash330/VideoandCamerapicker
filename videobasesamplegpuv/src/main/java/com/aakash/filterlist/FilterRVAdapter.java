package com.aakash.filterlist;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aakash.FilterRvListner;
import com.aakash.model.FilterRvModel;
import com.aakash.videobasesamplegpuv.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class FilterRVAdapter extends RecyclerView.Adapter<FilterRVAdapter.FilterRvHolder> {

    private Context mContext;
    private List<FilterRvModel> filterRvModelList;
    private FilterRvListner filterRvListner;

    public FilterRVAdapter(Context mContext, List<FilterRvModel> filterRvModelList, FilterRvListner filterRvListner) {
        this.mContext = mContext;
        this.filterRvModelList = filterRvModelList;
        this.filterRvListner=filterRvListner;
    }

    @NonNull
    @Override
    public FilterRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.filterlist_item_view,parent,false);
        return new FilterRvHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterRvHolder holder, int position) {

   FilterRvModel  data=filterRvModelList.get(position);

         Glide.with(mContext).
          load(Uri.parse(data.getImageUrl()))
                 .error(R.drawable.filterimage)
                .into(holder.imageView);

   holder.imageView.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           filterRvListner.onFilterClick(position);
       }
   });



    }

    @Override
    public int getItemCount() {
        return filterRvModelList.size();
    }


    public class FilterRvHolder extends RecyclerView.ViewHolder {
      private TextView filter_name;
      private ImageView imageView;
        public FilterRvHolder(@NonNull View itemView) {
            super(itemView);
            filter_name=itemView.findViewById(R.id.filter_name);
            imageView=itemView.findViewById(R.id.filter_image);

        }
    }
}
