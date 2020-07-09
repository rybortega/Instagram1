package com.codepath.teleroid.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.codepath.teleroid.PostDetailsActivity;
import com.codepath.teleroid.R;
import com.codepath.teleroid.adapters.PreviewPostsAdapter;
import com.codepath.teleroid.databinding.FragmentProfileBinding;
import com.codepath.teleroid.models.Post;
import com.codepath.teleroid.utilities.BitmapManipulations;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName(); //logging purposes

    protected FragmentProfileBinding binding;

    protected List<Post> posts;
    private PreviewPostsAdapter previewPostsAdapter;
    private int lastVisitedPost;
    private  File profilePictureFile;

    private ParseUser profileOwnerUser;

    public ProfileFragment(ParseUser profileOwnerUser){
        this.profileOwnerUser = profileOwnerUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();

        //Bind data from User that owns the profile
        bindProfileOwner(profileOwnerUser);

        //ADAPTER

        //Post preview listener --> Details
        PreviewPostsAdapter.OnClickListener onClickListener = new PreviewPostsAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent postDetailsIntent = new Intent(getContext(),
                        PostDetailsActivity.class);

                /* Storing position so we can refresh recycle view
                and smooth scroll back to where we were */
                lastVisitedPost = position;

                //Passing data to the intent
                postDetailsIntent.putExtra("post_object", Parcels.wrap(posts.get(position)));
                startActivity(postDetailsIntent);
            }
        };

        previewPostsAdapter = new PreviewPostsAdapter(getContext(), posts, onClickListener);
        binding.postsRecycler.setAdapter(previewPostsAdapter);
        binding.postsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        queryPosts();

        //REFRESHER

        //Swipe Refresh loading indicator colors
        binding.swipeRefreshContainer.setColorSchemeResources(
                R.color.colorSecondary,
                R.color.colorPrimaryDark,
                R.color.colorPrimary,
                R.color.colorAccent);

        //Swipe Refresh listener
        binding.swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refresh: Fetching fresh data!");

                //Clears and repopulates
                queryPosts();

                //Turn off the reload signal
                binding.swipeRefreshContainer.setRefreshing(false);
            }
        });

        if(profileOwnerUser == ParseUser.getCurrentUser()){
            binding.profilePicture.setClickable(true);
            binding.profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchCamera();
                }
            });
        }
    }

    /**
     * Binds data queried from Parse pertaining to the user to the views in the fragment.
     * @param user
     */
    private void bindProfileOwner(ParseUser user) {
        //Profile Pic
        ParseFile profilePicture = user.getParseFile("profilePicture");
        try{
            Glide.with(getContext())
                    .load(profilePicture.getUrl())
                    .circleCrop()
                    .placeholder(R.drawable.default_profile_picture)
                    .into(binding.profilePicture);
        }
        catch (Exception e){
            Log.e(TAG, "No profile picture found");
            Glide.with(getContext())
                    .load(R.drawable.default_profile_picture)
                    .circleCrop()
                    .into(binding.profilePicture);
        }

        //Display Name
        String displayName = user.getString("name");
        if(displayName == null || displayName.trim().equals("")){
            displayName = user.getUsername();
        }
        binding.displayName.setText(displayName);

        //Bio
        String userBio = user.getString("biography");
        if(userBio == null){
            userBio = "";
        }
            binding.displayBio.setText(userBio);
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, profileOwnerUser);
        query.setLimit(20); //Limits number of results.
        query.addDescendingOrder(Post.KEY_TIME);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> newPosts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't query posts: " + e);
                    return;
                }
                Log.i(TAG, "Posts retrieved successfully!");
                posts.clear();
                posts.addAll(newPosts);
                previewPostsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        String profilePictureFileName = "photo.jpg";
        profilePictureFile = getPhotoFileUri(profilePictureFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", profilePictureFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CreateFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        else {
            Log.e(TAG, "No available camera app");
        }
    }

    /**
     * Helper method to get the identifier of the captured image.
     * @Return the File for a photo stored on disk given the fileName
     */
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        String pathName = mediaStorageDir.getPath() + File.separator + fileName;
        File file = new File(pathName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreateFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //TODO: Resize picture before setting it.
                setNewProfilePicture(profileOwnerUser, new ParseFile(profilePictureFile));

            } else { // Result was a failure
                Log.i(TAG, "Picture wasn't taken!");
            }
        }
    }

    private void setNewProfilePicture(final ParseUser user, ParseFile newProfilePicture) {
        user.put("profilePicture", newProfilePicture);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in saving: " + e);
                    Toast.makeText(getContext(), "Posting Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Profile picture updated!");

                // Load the new image (now in our DB) as the profile picture
                ParseFile nowSetProfilePicture = user.getParseFile("profilePicture");
                Glide.with(getContext())
                        .load(nowSetProfilePicture.getUrl())
                        .circleCrop()
                        .placeholder(R.drawable.default_profile_picture)
                        .into(binding.profilePicture);
            }
        });
    }
}
