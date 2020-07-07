package com.codepath.teleroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.teleroid.R;
import com.codepath.teleroid.databinding.ItemPostBinding;
import com.codepath.teleroid.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ItemPostBinding binding;

        public ViewHolder(@NonNull ItemPostBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Post post){
            binding.postCaption.setText(post.getCaption());
            binding.profileHandle.setText(post.getUser().getUsername());

            ParseFile image = post.getImage();
            if(image != null){
                Glide.with(context)
                        .load(post.getImage().getUrl())
                        .into(binding.postPhoto);
            }
        }
    }
}
