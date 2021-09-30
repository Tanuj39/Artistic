package com.example.artistic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artistic.Adapters.PhotosAdapter;
import com.example.artistic.Models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class OthersProfileActivity extends AppCompatActivity {


    Button btn_follow,btn_following,message;
    ImageView profile;
    ImageView bg;
    TextView username,fullname,following_count,followers_count,pos_count;
    TextView followers,following;

    Toolbar toolbar;

    RecyclerView recyclerView;
    List<Posts> postsList;
    PhotosAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    String id;
    DatabaseReference reference;
    StorageReference storageReference,bgRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);


        init();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference= FirebaseStorage.getInstance().getReference().child("Profiles");
        bgRef= FirebaseStorage.getInstance().getReference().child("Backgrounds");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        id=getIntent().getStringExtra("uid");

        if (id.equals(user.getUid()))
        {
            btn_follow.setVisibility(View.VISIBLE);
            btn_follow.setText("Settings");
        }else
        {
            checkFollow();
        }

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));

        postsList=new ArrayList<>();
        adapter=new PhotosAdapter(this,postsList);

        getImages();
        recyclerView.setAdapter(adapter);


        getUserData();
        getFollowCount();

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OthersProfileActivity.this,ShowListActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("title",followers.getText().toString());
                startActivity(intent);
                Toast.makeText(OthersProfileActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(OthersProfileActivity.this,ShowListActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("title",following.getText().toString());
                startActivity(intent);
            }
        });


        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_follow.getText().toString().equals("Follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid())
                            .child("following").child(id).setValue(true);

                    addNotifications();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(id)
                            .child("followers").child(user.getUid()).setValue(true);

                }
            }
        });
        btn_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_following.getText().toString().equals("Following"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid())
                            .child("following").child(id).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(id)
                            .child("followers").child(user.getUid()).removeValue();
                }
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OthersProfileActivity.this, ChatDetailActivity.class);
                startActivity(intent);
            }
        });


    }
    private void init()
    {
        btn_follow=findViewById(R.id.btn_follow);
        btn_following=findViewById(R.id.btn_following);
        profile=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        message=findViewById(R.id.message);
        bg=findViewById(R.id.background);
        following_count=findViewById(R.id.following_count);
        followers_count=findViewById(R.id.followers_count);
        pos_count=findViewById(R.id.posts);
        followers=findViewById(R.id.followers);
        following=findViewById(R.id.following);



    }

    private void getUserData()
    {

        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String n= Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String f= Objects.requireNonNull(snapshot.child("fullname").getValue()).toString();
                String p= Objects.requireNonNull(snapshot.child("profileUrl").getValue()).toString();
                String b= Objects.requireNonNull(snapshot.child("background").getValue()).toString();


                username.setText(n);
                fullname.setText(f);
                Picasso.get().load(p).placeholder(R.drawable.profile_image).into(profile);
                Picasso.get().load(b).into(bg);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OthersProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getFollowCount()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(id).child("followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers_count.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OthersProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(id).child("following");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following_count.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OthersProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference postCount=FirebaseDatabase.getInstance().getReference().child("Posts");
        postCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String p=dataSnapshot.child("publisher").getValue().toString();
                    if (p.equals(id))
                    {
                        i++;
                    }
                }
                pos_count.setText("Posts "+"("+i+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(OthersProfileActivity.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void checkFollow()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(user.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists())
                {
                    btn_follow.setVisibility(View.GONE);
                    btn_following.setVisibility(View.VISIBLE);
                }else
                {
                    btn_following.setVisibility(View.GONE);
                    btn_follow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getImages()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Posts posts=dataSnapshot.getValue(Posts.class);
                    if (posts.getPublisher().equals(id))
                    {
                        postsList.add(posts);
                    }
                }
                Collections.reverse(postsList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(OthersProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void addNotifications()
    {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Notifications").child(id);

        HashMap<String,Object> map=new HashMap<>();

        map.put("userid",user.getUid());
        map.put("comment","started following you");
        map.put("postid","");
        map.put("ispost",false);

        reference.push().setValue(map);
    }






}