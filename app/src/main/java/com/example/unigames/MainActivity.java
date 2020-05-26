package com.example.unigames;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private EditText mEditTextName;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;

    //Fecha
    private TextView mTextViewDate;
    public final Calendar c = Calendar.getInstance();
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //botones
    private Button mButtonRegister;
    private Button mButtonSendLogin;


    //private String fotoPerfil;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    Uri pickedImgUri;
    static int PReqCode = 1;
    static int REQUESCODE = 1;

    //Codigo que va a usar la galeria para abrir la galeria
    //   private static final int GALLERY_INTENT = 1;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Variables de los datos a registrar
        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);

        //Fecha
        mTextViewDate = (TextView) findViewById(R.id.editTextDateLogin);

        mTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.editTextDateLogin:
                        obtenerFecha();
                        break;
                }
            }
        });




        mButtonSendLogin = (Button) findViewById(R.id.btnSendToLogin);
        mProgressBar = (ProgressBar) findViewById(R.id.regProgressBar);

        mProgressBar.setVisibility(View.INVISIBLE);

        mButtonRegister = (Button) findViewById(R.id.btnRegister);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonRegister.setVisibility(View.INVISIBLE);
                //mButtonSendLogin.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);

                final String name = mEditTextName.getText().toString();
                final String email = mEditTextEmail.getText().toString();
                final String password = mEditTextPassword.getText().toString();
                final String date = mTextViewDate.getText().toString();
                // fotoPerfil =


                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !date.isEmpty()) {
                    if(password.length() >= 6) {
                        registerUser(email,name,password,date);

                    } else {
                        Toast.makeText(MainActivity.this,"Password must contain at least 6 characters",Toast.LENGTH_SHORT).show();
                        //mButtonSendLogin.setVisibility(View.VISIBLE);
                        mButtonRegister.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                } else {
                    Toast.makeText(MainActivity.this,"Please, fill all the gaps",Toast.LENGTH_SHORT).show();
                    //mButtonSendLogin.setVisibility(View.VISIBLE);
                    mButtonRegister.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        //Foto
        mImageView = (ImageView) findViewById(R.id.regUserPhoto);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >=22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        mButtonSendLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                //finish(); no se pone para que pueda navegar hacia login y volver a registro
            }
        });
    }



    private void registerUser(final String email, final String name, final String password, final String date) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {



                    Map<String, Object> map = new HashMap<>();
                    map.put("password", password);
                    map.put("date",date);


                    String id = mAuth.getCurrentUser().getUid();

                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()) {
                                Toast.makeText(MainActivity.this,"Account created succesfully",Toast.LENGTH_SHORT).show();
                                updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());
                            } else {
                                Toast.makeText(MainActivity.this,"Data added to the database",Toast.LENGTH_SHORT).show();
                                mButtonSendLogin.setVisibility(View.VISIBLE);
                                mButtonRegister.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this,"Ups! We have a problem. Try again later",Toast.LENGTH_SHORT).show();
                    mButtonRegister.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        if(pickedImgUri == null) {
            pickedImgUri = Uri.parse("android.resource://com.example.unigames/drawable/"+ R.drawable.foto_defecto);
        }

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // image uploaded succesfully
                // now we can get our image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // uri contain user image url
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            Toast.makeText(MainActivity.this,"The data has been properly associated with the user",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, Home.class));
                                            finish();
                                        }
                                    }
                                });
                    }
                });
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
        }
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? "0" + String.valueOf(month+1):String.valueOf(month+1);
                //Muestro la fecha con el formato deseado
                mTextViewDate.setText(diaFormateado + "/" + mesFormateado + "/" + year);
            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            mImageView.setImageURI(pickedImgUri);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
            openGallery();
    }
}