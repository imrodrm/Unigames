package com.example.unigames.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.unigames.MainActivity;
import com.example.unigames.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String email = "";
    private String password = "";
    private String user = "";
    //private String password2 = "";

    //botones
    private Button mButtonChange;

    //Textos
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private TextView mTextViewPassword;

    //Foto
    private ImageView navUserPhot;
    Uri pickedImgUri;
    static int PReqCode = 1;
    static int REQUESCODE = 1;

    //Fecha
    private TextView mTextViewDate;


    //Firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Datos usuario
        mTextViewName = (TextView) fragmentView.findViewById(R.id.textViewNameChange);
        mTextViewEmail = (TextView) fragmentView.findViewById(R.id.textViewEmailChange);
        mTextViewPassword = (TextView) fragmentView.findViewById(R.id.textViewPasswordChange);
        navUserPhot = (ImageView) fragmentView.findViewById(R.id.ChangeUserPhoto);
        //Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);
        Picasso.get().load(currentUser.getPhotoUrl()).resize(700,700).into(navUserPhot);
        pickedImgUri = currentUser.getPhotoUrl();
        navUserPhot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >=22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });



        //Fecha
        mTextViewDate = (TextView) fragmentView.findViewById(R.id.editTextDate);

        mButtonChange = (Button) fragmentView.findViewById(R.id.btnCambiarDatos);

        getUserInfo();

        mButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mTextViewEmail.getText().toString();
                password = mTextViewPassword.getText().toString();
                user = mTextViewName.getText().toString();


                //Toast.makeText(getActivity(),email,Toast.LENGTH_SHORT).show();



                updateUserInfo(user,email,password);

                //Toast.makeText(getActivity(),currentUser.getEmail(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return fragmentView;
    }

    private void getUserInfo() {

        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    String name = currentUser.getDisplayName();
                    String email = currentUser.getEmail();
                    String password = dataSnapshot.child("password").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();

                    /*String name = dataSnapshot.child("name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String password = dataSnapshot.child("password").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();*/


                    mTextViewName.setText(name);
                    mTextViewEmail.setText(email);
                    mTextViewPassword.setText(password);
                    mTextViewDate.setText(date);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateUserInfo(final String name,final String email,final String password) {
        //primero subimos la foto a firebase storage y conseguimos la url
        // first we need to upload user photo to firebase storage and get url

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());


        if(!email.isEmpty() || !password.isEmpty()) {

            if(pickedImgUri != currentUser.getPhotoUrl()) {
                if(!name.equals(currentUser.getDisplayName())) {

                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // image uploaded succesfully
                            // now we can get our image url

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    // uri contain user image url
                                    UserProfileChangeRequest profileUpdates;

                                    profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .setPhotoUri(uri)
                                            .build();
                                    currentUser.updateProfile(profileUpdates);
                                    //currentUser.updatePassword(password);


                                    //currentUser.updateEmail(mTextViewEmail.getText().toString());
                                    //Toast.makeText(getActivity(),email,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {

                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // image uploaded succesfully
                            // now we can get our image url

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    // uri contain user image url
                                    UserProfileChangeRequest profileUpdates;

                                    profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    currentUser.updateProfile(profileUpdates);
                                    //currentUser.updateEmail(mTextViewEmail.getText().toString());
                                    //Toast.makeText(getActivity(),email,Toast.LENGTH_SHORT).show();

                                    //currentUser.updatePassword(password);


                                }
                            });
                        }
                    });
                }

            } else {
                if(!name.equals(currentUser.getDisplayName())) {
                    UserProfileChangeRequest profileUpdates;
                    profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    currentUser.updateProfile(profileUpdates);
                    //currentUser.updateEmail(mTextViewEmail.getText().toString());
                    //Toast.makeText(getActivity(),email,Toast.LENGTH_SHORT).show();



                    //currentUser.updatePassword(password);


                } else {
                    Toast.makeText(getActivity(),"You didn't change anything",Toast.LENGTH_SHORT).show();


                    //currentUser.updatePassword(password);

                }
            }
        } else {
            Toast.makeText(getActivity(),"Please, fill all the data",Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            navUserPhot.setImageURI(pickedImgUri);
            Picasso.get().load(pickedImgUri).resize(700,700).into(navUserPhot);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(),"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            openGallery();

    }

}