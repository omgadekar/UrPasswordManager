package com.example.password_generator_project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.password_generator_project.MainActivity;
import com.example.password_generator_project.R;
import com.example.password_generator_project.data.MyDBHandler;
import com.example.password_generator_project.model.pass_db;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<pass_db> passlist;
    public RecyclerViewAdapter(Context context, List<pass_db> passlist) {
        this.context = context;
        this.passlist = passlist;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
            pass_db passdb = passlist.get(position);
            holder.password_text.setText(passdb.getPass());
            holder.website_name.setText(passdb.getWebsitename());
    }

    @Override
    public int getItemCount() {
        return passlist.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView password_text;
            public TextView website_name;
            public ImageView delete_icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            password_text = itemView.findViewById(R.id.prev_pass_name);
            website_name = itemView.findViewById(R.id.prev_web_name);
            delete_icon = itemView.findViewById(R.id.imageView);
            delete_icon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            MyDBHandler db = new MyDBHandler(itemView.getContext());
            int clickedPosition = getAdapterPosition();
            pass_db passdb = passlist.get(clickedPosition);

            if (view.getId() == R.id.imageView) { // Check if the delete icon was clicked
                db.deletePassword(passdb.getWebsitename(), passdb.getPass());
                passlist.remove(clickedPosition);
                notifyItemRemoved(clickedPosition);
            }
           //db.close();
        }
    }
}
