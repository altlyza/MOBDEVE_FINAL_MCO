package com.mobdeve.xx22.pascualreynadojaime.mco.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.xx22.pascualreynadojaime.mco.myapp.databinding.ActivityExploreBinding;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private ActivityExploreBinding binding;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind the layout using ViewBinding
        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DatabaseHandler
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // Load posts from the database
        postList = dbHandler.getAllPosts();

        // If no posts are in the database, add dummy data
        if (postList.isEmpty()) {
            addDummyPosts(dbHandler);
            postList = dbHandler.getAllPosts(); // Reload posts after adding dummy data
        }

        // Set up the adapter
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        // Set up FAB to show a dialog for selecting post type
        binding.fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExploreActivity.this);
            builder.setTitle("Create Post")
                    .setItems(new CharSequence[]{"Event Post", "Normal Post"}, (dialog, which) -> {
                        Intent intent = new Intent(ExploreActivity.this, CreatePostActivity.class);
                        if (which == 0) {
                            intent.putExtra("POST_TYPE", "Event");
                            Toast.makeText(ExploreActivity.this, "Creating Event Post", Toast.LENGTH_SHORT).show();
                        } else if (which == 1) {
                            intent.putExtra("POST_TYPE", "Normal");
                            Toast.makeText(ExploreActivity.this, "Creating Normal Post", Toast.LENGTH_SHORT).show();
                        }
                        startActivity(intent);
                    });
            builder.show();
        });

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_explore);

        // Handle navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_explore) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                String email = getIntent().getStringExtra("USER_EMAIL");
                Intent intent = new Intent(ExploreActivity.this, ProfileActivity.class);
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    /**
     * Add dummy posts to the database.
     */
    private void addDummyPosts(DatabaseHandler dbHandler) {
        dbHandler.addPost("Apotheka", "Best bar in Poblacion, Makati.", R.drawable.ic_apotheka, R.drawable.post_apotheka);
        dbHandler.addPost("Clubhouse", "Come and enjoy the best drinks in town!", R.drawable.ic_clubhouse, R.drawable.post_clubhouse);
        dbHandler.addPost("Lan Kwai", "A perfect place to relax with friends.", R.drawable.ic_lankwai, R.drawable.post_lankwai);
        dbHandler.addPost("La Toca", "Experience the ultimate nightlife here!", R.drawable.ic_latoca, R.drawable.post_latoca);
    }

}
