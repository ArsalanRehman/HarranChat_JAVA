package com.example.harranchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
//Degiskenler
    private EditText edtUsername, edtPassword, edtEmail;
    private Button btnSubmit;
    private TextView txtLoginInfo;

    //İlk olarak kullanıcının signup yapmak istediğni varsayıyoruz
    private boolean isSigningUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);

        btnSubmit = findViewById(R.id.btnSubmit);

        txtLoginInfo = findViewById(R.id.txtLoginInfo);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,FriendsActivity.class));
            finish();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kullanıcının Boş input gönderdiği zaman "geçersiz input" uyarı çıkması
                if (edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()){
                    if (isSigningUp && edtUsername.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Geçersiz Bilgi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (isSigningUp){
                    handleSignUp();
                }else {
                    handleLogin();
                }
            }
        });

        //txtLoginInfo için clicklistener oluşturuyoruz
        //amaç kullanıcı giriş yapmak istediği zaman "Kullanıcı Adı" Edittext'in kaybolması
        //ve yazının(txtLoginInfo) dinamik olarak "Üye ol" diye değişmesi

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSigningUp){
                    isSigningUp = false;
                    edtUsername.setVisibility(View.GONE);
                    //Kullanıcnın Yapacağı işleme göre Buttonun yazı değişmesi
                    btnSubmit.setText("Giris Yap");
                    //Kullanıcnın Yapacağı işleme göre EditText'in değişmesi
                    txtLoginInfo.setText("Hesabim Yok : Uye Ol");
                }else {
                    isSigningUp = true;
                    edtUsername.setVisibility(View.VISIBLE);
                    btnSubmit.setText("Uye Ol");
                    txtLoginInfo.setText("Giris Yap");
                }
            }
        });
    }
    private void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(),edtEmail.getText().toString(),""));
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Kayıt Olma işlemi başarıyla gerçekleşti", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleLogin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}