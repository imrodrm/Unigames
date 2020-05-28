package com.example.unigames.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unigames.Game;
import com.example.unigames.R;
import com.example.unigames.UnigamesApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String id;

    UnigamesApi unigamesApi = new UnigamesApi();


    private ImageView image;
    private TextView tvTitulo;
    private TextView tvDescripcionGame;
    private TextView tvReleaseDate;
    private TextView tvRating;
    private TextView tvHours;
    private TextView tvDevelopers;
    private TextView tvpublishersOfGame;
    private TextView tvESRBRating;
    private LinearLayout layoutPlatforms;
    private RecyclerView recyclerView;
    private List<Game> relacionados;
    private AdapterGame adapterGame;
    private ImageView youtubeIcon;

    private String codigo;


    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private ImageView btnAdd;
    private ImageView btnDelete;


    public GameFragment() {
        // Required empty public constructor
    }

    public GameFragment(String id) {
        this.codigo = id;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_game, container, false);
        mAuth = FirebaseAuth.getInstance();
        final Game g = unigamesApi.getInfoFromAGame(codigo);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(g.getName());
        tvTitulo = (TextView) fragmentView.findViewById(R.id.textViewTitulo);
        tvTitulo.setText(g.getName());
        tvDescripcionGame = (TextView) fragmentView.findViewById(R.id.textViewGameDescription);
        tvDescripcionGame.setText(Html.fromHtml(g.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        image = (ImageView) fragmentView.findViewById(R.id.gamePhoto);
        Picasso.get().load(g.getUrlBackground_image()).into(image);
        youtubeIcon = (ImageView) fragmentView.findViewById(R.id.youtubeIcon);
        youtubeIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view){
                    unigamesApi.getTrailerFromAGame(g);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + g.getTrailer()));
                    startActivity(intent);
                    intent.putExtra("VIDEO_ID", g.getTrailer());
            }
        });

        tvReleaseDate = (TextView) fragmentView.findViewById(R.id.releaseDateGame);
        tvRating = (TextView) fragmentView.findViewById(R.id.ratingGame);
        try {
            if(g.getReleased()==null){
                tvReleaseDate.setText("Unknown release date");
                if(g.getRating()!=null){
                    tvRating.setText(tvRating.getText() + " " + g.getRating());
                } else{
                    tvRating.setText("unkown rating");
                }
            }
            else {
                Calendar car = Calendar.getInstance();
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = originalFormat.parse(g.getReleased());
                car.setTime(date);
                String getSuffix = this.getDayOfMonthSuffix(date.getDay());
                tvReleaseDate.setText(new SimpleDateFormat("MMMM dd'" + getSuffix + ",' yyyy", Locale.ENGLISH).format(date));
                if (car.getTimeInMillis() > System.currentTimeMillis()) {
                    car.add(Calendar.DATE, -5);
                    if (car.getTimeInMillis() < System.currentTimeMillis()) {
                        tvReleaseDate.setTextColor(Color.RED);
                    }
                } else {
                    tvReleaseDate.setTextColor(Color.LTGRAY);
                }
                car.add(Calendar.DATE, -5);
                if (car.getTimeInMillis() > System.currentTimeMillis()) {
                    tvRating.setText(tvRating.getText() + " no valid ratings yet");
                } else {
                    tvRating.setText(tvRating.getText() + " " + g.getRating());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.layoutPlatforms = fragmentView.findViewById(R.id.LinearLayoutPlatforms);
        try {
            String s = "";
            for (int i = 0; i < g.getPlatforms().size(); i++) {
                if(i<g.getPlatforms().size()-1){
                    if (g.getPlatforms().get(i).startsWith("Xbox")) {
                        s += "<font color='#5dc21e'>" + g.getPlatforms().get(i) + "</font>, ";
                    } else if (g.getPlatforms().get(i).startsWith("Nintendo") || g.getPlatforms().get(i).startsWith("Wii")) {
                        s += "<font color='#e70009'>" + g.getPlatforms().get(i) + "</font>, ";
                    } else if (g.getPlatforms().get(i).startsWith("PlayStation")) {
                        s += "<font color='#003791'>" + g.getPlatforms().get(i) + "</font>, ";
                    } else if (g.getPlatforms().get(i).startsWith("PC")) {
                        s += "<font color='#FACA04'>" + g.getPlatforms().get(i) + "</font>, ";
                    } else if (g.getPlatforms().get(i).startsWith("Linux")) {
                        s += "<font color='#E95420'>" + g.getPlatforms().get(i) + "</font>, ";
                    } else if (g.getPlatforms().get(i).startsWith("macOS")) {
                        s += "<font color='#999999'>" + g.getPlatforms().get(i) + "</font>, ";
                    } else if (g.getPlatforms().get(i).startsWith("macOS")) {
                        s += "<font color='#999999'>" + g.getPlatforms().get(i) + "</font>, ";
                    } else {
                        s += g.getPlatforms().get(i) + ", ";
                    }
                } else{
                    if (g.getPlatforms().get(i).startsWith("Xbox")) {
                        s += "<font color='#5dc21e'>" + g.getPlatforms().get(i) + "</font>";
                    } else if (g.getPlatforms().get(i).startsWith("Nintendo") || g.getPlatforms().get(i).startsWith("Wii")) {
                        s += "<font color='#e70009'>" + g.getPlatforms().get(i) + "</font>";
                    } else if (g.getPlatforms().get(i).startsWith("PlayStation")) {
                        s += "<font color='#003791'>" + g.getPlatforms().get(i) + "</font>";
                    } else if (g.getPlatforms().get(i).startsWith("PC")) {
                        s += "<font color='#FACA04'>" + g.getPlatforms().get(i) + "</font>";
                    } else if (g.getPlatforms().get(i).startsWith("Linux")) {
                        s += "<font color='#E95420'>" + g.getPlatforms().get(i) + "</font>";
                    } else if (g.getPlatforms().get(i).startsWith("macOS")) {
                        s += "<font color='#999999'>" + g.getPlatforms().get(i) + "</font>";
                    } else {
                        s += g.getPlatforms().get(i);
                    }
                }
            }
            TextView tv = new TextView(fragmentView.getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setText(Html.fromHtml(s));
            this.layoutPlatforms.addView(tv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvHours = (TextView) fragmentView.findViewById(R.id.hoursOfGame);
        if (g.getPlaytime() != 0) {
            tvHours.setText(tvHours.getText().toString() + g.getPlaytime() + " hours");
        } else {
            tvHours.setText(tvHours.getText() + " unknown");
        }
        try {
            tvESRBRating = fragmentView.findViewById(R.id.ESRBRating);
            if (g.getESRBRating() != null) {
                tvESRBRating.setText("ESRB: " + g.getESRBRating());
                if (g.getESRBRating().startsWith("Adults")) {
                    tvESRBRating.setTextColor(Color.BLACK);
                }else if (g.getESRBRating().startsWith("Mature")) {
                    tvESRBRating.setTextColor(Color.RED);
                } else if (g.getESRBRating().startsWith("Teen")) {
                    tvESRBRating.setTextColor(Color.MAGENTA);
                } else if (g.getESRBRating().equalsIgnoreCase("Everyone 10+")) {
                    tvESRBRating.setTextColor(Color.BLUE);
                } else if (g.getESRBRating().startsWith("Everyone")) {
                    tvESRBRating.setTextColor(Color.GREEN);
                }
            } else {
                tvESRBRating.setText("ESRB: unknown");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvDevelopers = (TextView) fragmentView.findViewById(R.id.developersOfGame);
        tvpublishersOfGame = (TextView) fragmentView.findViewById(R.id.publishersOfGame);
        try {
            tvDevelopers.setText(tvDevelopers.getText() + " " + g.getDeveloper());
            tvpublishersOfGame.setText(tvpublishersOfGame.getText() + " " + g.getPublisher());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnAdd = (ImageView) fragmentView.findViewById(R.id.btnAddGame);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> map = new HashMap<>();
                map.put("game", codigo);

                String id = mAuth.getCurrentUser().getUid();

                mDatabase.child("Users").child(id).child("games").child(codigo).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task2) {
                        if (task2.isSuccessful()) {
                            Toast.makeText(getContext(), "Game has been correctly added", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            //finish();
                        } else {
                            Toast.makeText(getContext(), "Game couldn't be added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        btnDelete = (ImageView) fragmentView.findViewById(R.id.btnDeleteGame);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                String id = mAuth.getCurrentUser().getUid();
                    mDatabase.child("Users").child(id).child("games").child(codigo).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(getContext(), "Game has been removed from favourites", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                //finish();
                            } else {
                                Toast.makeText(getContext(), "The game couldn't be removed from favourites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });
        relacionados = new ArrayList<>();
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_relacionados);
        List<Game> aux = unigamesApi.getVisuallySimmilarGames(g.getId());
        List<Game> saga = unigamesApi.getSeriesGames(g.getId());
        if(saga!=null){
            aux.addAll(0, saga);
        }
        if(aux.size() != 0) {
            int o = 3;
            if(aux.size() < o) {
                o = aux.size();
            }
            for(int i=0; i<o; i++){
                relacionados.add(aux.get(i));
            }
            mostrarData(recyclerView, relacionados);
        }
        return fragmentView;
    }

    String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public void mostrarData(final RecyclerView rv, final List<Game> g) {

        rv.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapterGame = new AdapterGame(getContext(),g);
        rv.setAdapter(adapterGame);


        adapterGame.setOnClickLister(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = g.get(rv.getChildAdapterPosition(v)).getId();
                Fragment fragment = new GameFragment(id);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}