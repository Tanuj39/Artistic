package com.example.artistic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;


public class SignUpActivity extends AppCompatActivity {

    TextView gotologin;
    EditText fullname,username,email,password,password1;
    Button signup;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    String url="https://firebasestorage.googleapis.com/v0/b/signin-function.appspot.com/o/download.jpg?alt=media&token=143d5e9d-c652-465d-b513-6ee75537ad11";

    String profile="https://firebasestorage.googleapis.com/v0/b/signin-function.appspot.com/o/user.svg?alt=media&token=561ed6be-db1a-4068-8023-15904ed28796";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");


        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f=fullname.getText().toString();
                String n=username.getText().toString();
                String e=email.getText().toString();
                String p=password.getText().toString();
                String p1=password1.getText().toString();

                if (f.isEmpty()||e.isEmpty()||p.isEmpty()||p1.isEmpty()||n.isEmpty()){
                    Toast.makeText(SignUpActivity.this,"Required Field",Toast.LENGTH_SHORT).show();
                }
                else if(p.length()<6){
                    Toast.makeText(SignUpActivity.this,"Password length must be greater than 6 characters ",Toast.LENGTH_SHORT).show();
                }else if (!p.equals(p1)){
                    Toast.makeText(SignUpActivity.this,"Password doesn't match",Toast.LENGTH_SHORT).show();
                }else{
                    createAccount(f,n,e,p,p1);
                }
            }
        });



    }

    private void createAccount(String f,String n, String e, String p, String p1) {
        progressDialog=new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating new Account");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull  Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"Please verify Email",Toast.LENGTH_SHORT).show();
                                saveData(f,n,e,p,p1);
                            }
                            else{
                                Toast.makeText(SignUpActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"Failed"+task.getException(),Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void saveData(String f, String n,String e, String p, String p1) {


        HashMap<String,Object> map=new HashMap<>();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();


        map.put("fullname",f);
        map.put("username",n);
        map.put("email",e);
        map.put("password",p);
        map.put("password1",p1);
        map.put("profileUrl",profile);
        map.put("background",url);
        map.put("Uid",user.getUid());

        reference.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    Toast.makeText(SignUpActivity.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });


    }

    private void init(){
        fullname = findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        email = findViewById(R.id.email);
        password= findViewById(R.id.password);
        password1 = findViewById(R.id.password1);
        gotologin = findViewById(R.id.gotologin);
        signup = findViewById(R.id.signup);
    }
}
