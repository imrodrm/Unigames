package com.example.unigames.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unigames.Game;
import com.example.unigames.R;
import com.example.unigames.UnigamesApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //Slider
    //HashMap<String, Integer> image_list;

    UnigamesApi unigamesApi = new UnigamesApi();

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    AdapterGame adapterGame;
    //referencias para comunicar fragments
    //Activity actividad;
    //iComunicaFragments interfaceComunicaFragments;

    //variable del fragment
    //GameFragment gameFragment = null;


    //private RecyclerView myRecyclerView_favoritos;
    //private List<Game> games_favoritos;

    private TextView textViewPopulares;
    private RecyclerView myRecyclerView_populares;
    private List<Game> games_populares;
    private TextView textViewPc;
    private RecyclerView myRecyclerView_pc;
    private List<Game> games_pc;
    private TextView textViewXbox;
    private RecyclerView myRecyclerView_xboxone;
    private List<Game> games_xboxone;
    private TextView textViewPs4;
    private RecyclerView myRecyclerView_ps4;
    private List<Game> games_ps4;
    private TextView textViewNintendo;
    private RecyclerView myRecyclerView_nintendoswitch;
    private List<Game> games_nintendoswitch;
    private TextView textViewAndroid;
    private RecyclerView myRecyclerView_android;
    private List<Game> games_android;
    private TextView textViewIOS;
    private RecyclerView myRecyclerView_ios;
    private List<Game> games_ios;

    //buscar
    private EditText mTextBuscar;
    private Button btnBuscar;


    private Button btnPopulares;
    private Button btnPc;
    private Button btnXbox;
    private Button btnPs4;
    private Button btnSwitch;
    private Button btnAndroid;
    private Button btnIos;


    //Textos
    //private TextView mTextViewGame;
    //Foto
    //private ImageView navGamePhot;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        textViewPopulares = (TextView) fragmentView.findViewById(R.id.textViewPopulares);
        textViewPopulares.setText(this.getEmojiByUnicode(0x1F525) + " Trending " + this.getEmojiByUnicode(0x1F525));
        textViewPc = (TextView) fragmentView.findViewById(R.id.textViewPc);
        textViewPc.setText(this.getEmojiByUnicode(0x1F5A5) + " PC Games " + this.getEmojiByUnicode(0x1F5A5));
        textViewXbox = (TextView) fragmentView.findViewById(R.id.textViewXboxOne);
        textViewXbox.setText(this.getEmojiByUnicode(0x1F3AE) + " Xbox One Games " + this.getEmojiByUnicode(0x1F3AE));
        textViewPs4 = (TextView) fragmentView.findViewById(R.id.textViewPs4);
        textViewPs4.setText(this.getEmojiByUnicode(0x1F535) + " PS4 Games " + this.getEmojiByUnicode(0x1F535));
        textViewNintendo = (TextView) fragmentView.findViewById(R.id.textViewSwitch);
        textViewNintendo.setText(this.getEmojiByUnicode(0x1F579) + " Nintendo Switch Games " + this.getEmojiByUnicode(0x1F579));
        textViewAndroid = (TextView) fragmentView.findViewById(R.id.textViewAndroid);
        textViewAndroid.setText(this.getEmojiByUnicode(0x1F4F1) + " Android games " + this.getEmojiByUnicode(0x1F4F1));
        textViewIOS = (TextView) fragmentView.findViewById(R.id.textViewIos);
        textViewIOS.setText(this.getEmojiByUnicode(0x1F4F1) + " IOS Games " + this.getEmojiByUnicode(0x1F4F1));
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
       // myRecyclerView_favoritos = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_favoritos);
        myRecyclerView_populares = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_populares);
        myRecyclerView_pc = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_pc);
        myRecyclerView_xboxone = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_Xbox);
        myRecyclerView_ps4 = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_Ps4);
        myRecyclerView_nintendoswitch = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_Switch);
        myRecyclerView_android = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_Android);
        myRecyclerView_ios = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_Ios);

        mTextBuscar = (EditText) fragmentView.findViewById(R.id.textViewGameSearch);
        btnBuscar = (Button) fragmentView.findViewById(R.id.btnGameSearch);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                boolean encontrado = false;
                String nombreJuego =  "";//mTextBuscar.getText().toString();
                nombreJuego = mTextBuscar.getText().toString();
                List<Game> games =  unigamesApi.searchGames(nombreJuego);
                Game game = null;
                if(nombreJuego.trim().isEmpty() || nombreJuego == null || games.size() == 0 ){ //si el nombre del juego está vacío
                    Toast.makeText(getContext(),"Busqueda no valida",Toast.LENGTH_SHORT).show();
                }else{
                        Fragment fragment = new ListFragment(games);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    //XCOM: Enemy Unknown si pones este juego peta

                }
            }
        });

        //populares
        games_populares = new ArrayList<>();
        List<Game> aux = unigamesApi.getPopularGames();
        if(aux == null) {
            Fragment fragment = new PageFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            for (int i = 0; i < 6; i++) {
                games_populares.add(aux.get(i));
            }
            mostrarData(myRecyclerView_populares, games_populares);
            btnPopulares = (Button) fragmentView.findViewById(R.id.btnpopulares);
            btnPopulares.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ListFragment("populares");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        //pc
        games_pc = new ArrayList<>();
        aux = unigamesApi.platforms("4");
        if(aux == null) {
            Fragment fragment = new PageFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            for (int i = 0; i < 6; i++) {
                games_pc.add(aux.get(i));
            }
            mostrarData(myRecyclerView_pc, games_pc);
            btnPc = (Button) fragmentView.findViewById(R.id.btnpc);
            btnPc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ListFragment("4");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        //xbox one
        games_xboxone = new ArrayList<>();
        aux = unigamesApi.platforms("1");
        if(aux == null) {
            Fragment fragment = new PageFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            {
                for (int i = 0; i < 6; i++) {
                    games_xboxone.add(aux.get(i));
                }
                mostrarData(myRecyclerView_xboxone, games_xboxone);
                btnXbox = (Button) fragmentView.findViewById(R.id.btnxbox);
                btnXbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = new ListFragment("1");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }
        }
        //ps4
        games_ps4 = new ArrayList<>();
        aux = unigamesApi.platforms("18");
        if(aux == null) {
            Fragment fragment = new PageFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            for (int i = 0; i < 6; i++) {
                games_ps4.add(aux.get(i));
            }
            mostrarData(myRecyclerView_ps4, games_ps4);
            btnPs4 = (Button) fragmentView.findViewById(R.id.btnps4);
            btnPs4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ListFragment("18");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        //nintendo switch
        games_nintendoswitch = new ArrayList<>();
        aux = unigamesApi.platformsUna("7");
        if(aux == null) {
            Fragment fragment = new PageFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            for (int i = 0; i < 6; i++) {
                games_nintendoswitch.add(aux.get(i));
            }
            mostrarData(myRecyclerView_nintendoswitch, games_nintendoswitch);
            btnSwitch = (Button) fragmentView.findViewById(R.id.btnswitch);
            btnSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ListFragment("7");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        //android
        games_android = new ArrayList<>();
        aux = unigamesApi.platforms("21");
        if(aux == null) {
            Fragment fragment = new PageFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            for (int i = 0; i < 6; i++) {
                games_android.add(aux.get(i));
            }
            mostrarData(myRecyclerView_android, games_android);
            btnAndroid = (Button) fragmentView.findViewById(R.id.btnandroid);
            btnAndroid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ListFragment("21");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        //ios
        games_ios = new ArrayList<>();
        aux = unigamesApi.platforms("3");
        if(aux == null) {
            Fragment fragment = new PageFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            for (int i = 0; i < 6; i++) {
                games_ios.add(aux.get(i));
            }
            mostrarData(myRecyclerView_ios, games_ios);
            btnIos = (Button) fragmentView.findViewById(R.id.btnios);
            btnIos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ListFragment("3");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

        return fragmentView;
    }



    public void onItemClick(String id) {
        Fragment fragment = new GameFragment(id);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void mostrarData(final RecyclerView rv, final List<Game> g) {

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)/*GridLayoutManager(getContext(),3)*/);
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

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }


}
