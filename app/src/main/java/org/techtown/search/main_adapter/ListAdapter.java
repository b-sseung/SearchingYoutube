package org.techtown.search.main_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.search.R;

import java.text.DateFormat;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements OnListClickListener {

    ArrayList<TubeList> items = new ArrayList<TubeList>();
    OnListClickListener listener;

    static ViewGroup parent;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        this.parent = parent;
        View itemView = inflater.inflate(R.layout.activity_list_item, parent, false);

        return new ViewHolder(itemView, this);
    }

    public void setOnItemClickListener(OnListClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null){
            listener.onItemClick(holder, view, position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TubeList item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(TubeList item){
        items.add(item);
    }

    public void setItems(ArrayList<TubeList> items){
        this.items = items;
    }

    public TubeList getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, TubeList item){
        items.set(position, item);
    }

    public void clear() {
        setItems(new ArrayList<TubeList>());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView channal;
        TextView date;
        ImageView image;

        public ViewHolder(@NonNull View itemView, final OnListClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.titleText);
            channal = itemView.findViewById(R.id.channalText);
            date = itemView.findViewById(R.id.DateText);
            image = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });

        }

        public void setItem(TubeList item){
            title.setText(item.getTitle());
            channal.setText(item.getChannal());
            date.setText(item.getDate());
            Glide.with(ListAdapter.parent.getContext()).load(item.getResId()).into(image);
        }

    }


}
