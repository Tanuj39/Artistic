package com.example.artistic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.artistic.Fragments.ChatFragment;
import com.example.artistic.Fragments.FavouriteFragment;
import com.example.artistic.Fragments.FeedFragment;
import com.example.artistic.Fragments.ProfileFragment;
import com.example.artistic.Fragments.UsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    FrameLayout frameLayout;



    FirebaseAuth auth;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView=findViewById(R.id.bottom_navigation);

        frameLayout=findViewById(R.id.frame_layout);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.following)
                {
                    UsersFragment usersFragment = new UsersFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, usersFragment);
                    fragmentTransaction.commit();
                }
                else if (id == R.id.feed)
                {
                    FeedFragment feedFragment = new FeedFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, feedFragment);
                    fragmentTransaction.commit();
                }
                else if (id==R.id.chats){
                    ChatFragment chatFragment = new ChatFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, chatFragment);
                    fragmentTransaction.commit();

                }
                else if (id == R.id.fav)
                {
                    FavouriteFragment favouriteFragment = new FavouriteFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, favouriteFragment);
                    fragmentTransaction.commit();
                }
                else if (id == R.id.profile)
                {
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, profileFragment);
                    fragmentTransaction.commit();
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUid());
                    editor.apply();
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.following);


    }
}