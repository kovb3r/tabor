package com.example.tabor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CampAdapter extends RecyclerView.Adapter<CampAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<Camp> mCampsData;
    private ArrayList<Camp> mCampsDataAll;
    private int lastPosition = -1;

    CampAdapter(Context context, ArrayList<Camp> campsData){
        this.mCampsData = campsData;
        this.mContext = context;
        this.mCampsDataAll = campsData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CampAdapter.ViewHolder holder, int position) {
        Camp currentCamp = mCampsData.get(position);
        
        holder.bindTo(currentCamp);

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mCampsData.size();
    }

    @Override
    public Filter getFilter() {
        return bookingFilter;
    }

    private Filter bookingFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Camp> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mCampsDataAll.size();
                results.values = mCampsDataAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Camp camp : mCampsDataAll){
                    if(camp.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(camp);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mCampsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mCampImage;
        private RatingBar mRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPriceText = itemView.findViewById(R.id.price);;
            mCampImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);

        }

        public void bindTo(Camp currentCamp) {
            mTitleText.setText(currentCamp.getName());
            mInfoText.setText(currentCamp.getDescription());
            mPriceText.setText(currentCamp.getPrice());
            mRatingBar.setRating(currentCamp.getRatedInfo());

            Glide.with(mContext).load(currentCamp.getImageUrl()).into(mCampImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(v -> {
                ((CampListActivity) mContext).updateAlertIcon(currentCamp);
            });
            itemView.findViewById(R.id.delete).setOnClickListener(v -> {
                ((CampListActivity) mContext).deleteCamp(currentCamp);
            });
        }
    }
}
