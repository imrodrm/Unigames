package com.example.unigames.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
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
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
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

    private RecyclerView myRecyclerView_populares;
    private List<Game> games_platform = new ArrayList<>();

    //buscar
    private EditText mTextBuscar;
    private Button btnBuscar;
    private String platform;


    public ListFragment() {
        // Required empty public constructor
    }

    public ListFragment(String id) {
        // Required empty public constructor
        this.platform = id;
    }

    public ListFragment(List<Game> id) {
        // Required empty public constructor
        games_platform = id;
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
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
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
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
       // myRecyclerView_favoritos = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_favoritos);
        myRecyclerView_populares = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_platform);

        mTextBuscar = (EditText) fragmentView.findViewById(R.id.textViewGameSearch2);
        if(games_platform.size() == 0) {
            if(platform.equals("populares")) {
                games_platform = unigamesApi.getPopularGames();
            } else {
                if(this.platform.equals("7")){
                    games_platform = unigamesApi.platformsUna(platform);
                }
                else{
                    games_platform = unigamesApi.platforms(platform);
                }

            }
        }


        btnBuscar = (Button) fragmentView.findViewById(R.id.btnGameSearch2);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                boolean encontrado = false;
                String nombreJuego =  "";//mTextBuscar.getText().toString();
                nombreJuego = mTextBuscar.getText().toString();
                List<Game> games =  unigamesApi.searchGames(nombreJuego);
                Game game = null;
                if(nombreJuego.trim().isEmpty() || nombreJuego == null ){ //si el nombre del juego está vacío
                    Toast.makeText(getContext(),"Busqueda no valida",Toast.LENGTH_SHORT).show();
                }else{
                        Fragment fragment = new ListFragment(games);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                }
            }
        });


        mostrarData(myRecyclerView_populares,games_platform);

        return fragmentView;
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
