package com.example.budspaces.Adapters;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.PopupActions;
import com.example.budspaces.Listeners.OnPopupActionSelectedListener;
import com.example.budspaces.R;
import com.example.budspaces.Utils.Tuple3;

import java.util.List;

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.PopupVH> {
    private List<Tuple3<String, Drawable, PopupActions>> data;
    private OnPopupActionSelectedListener listener;
    private int count;

    public PopupAdapter(List<Tuple3<String, Drawable, PopupActions>> data, OnPopupActionSelectedListener listener) {
        this.data = data;
        this.listener = listener;
        this.count = data.size();
    }

    @NonNull
    @Override
    public PopupVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_popup, parent, false);

        return new PopupVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopupVH holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void add(Tuple3<String, Drawable, PopupActions> tuple3) {
        data.add(tuple3);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List<Tuple3<String, Drawable, PopupActions>> data) {
        for (Tuple3<String, Drawable, PopupActions> tuple3 : data)
            add(tuple3);
    }

    public void remove(String name) {
        for (Tuple3<String, Drawable, PopupActions> tuple3 : data) {
            if (tuple3.getFirst().equals(name)) {
                int position = data.indexOf(tuple3);
                if (position > -1) {
                    data.remove(position);
                    notifyItemRemoved(position);
                }
                break;
            }
        }
    }

    public void removeAll() {
        data.clear();
        notifyDataSetChanged();
    }

    public void showHideLast(boolean show) {
        if (show)
            count = data.size();
        else
            count = data.size() - 1;

        this.notifyDataSetChanged();
    }

    public static class PopupVH extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public PopupVH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTxt);
            imageView = itemView.findViewById(R.id.itemImg);
        }

        public void bind(Tuple3<String, Drawable, PopupActions> tuple, OnPopupActionSelectedListener listener) {
            textView.setText(tuple.getFirst());
            imageView.setImageDrawable(tuple.getSecond());
//            Drawable img = tuple.getSecond();
//            img.setBounds(0, 0, 60, 60);
//            textView.setCompoundDrawables(img, null, null, null);

            itemView.setOnClickListener(v -> {
                listener.actionSelected(tuple.getThird());
            });
        }
    }
}