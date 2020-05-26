package com.example.unigames.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unigames.Game;
import com.example.unigames.R;
import com.example.unigames.UnigamesApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView textViewLista;
    //api
    UnigamesApi unigamesApi = new UnigamesApi();

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    AdapterGame adapterGame;

    private RecyclerView myRecyclerView_favoritos;
    private List<Game> games_favoritos;


    public SettingsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        textViewLista = (TextView) fragmentView.findViewById(R.id.textViewFavourites);
        textViewLista.setText(this.getEmojiByUnicode(0x2764) + " YOUR FAVOURITE GAMES " + this.getEmojiByUnicode(0x2764));
        myRecyclerView_favoritos = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_id_favoritos);


        games_favoritos = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getGameInfo();



        mostrarData(myRecyclerView_favoritos,games_favoritos);

        return fragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

    public void mostrarData(final RecyclerView rv, final List<Game> g) {

        rv.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapterGame = new AdapterGame(getContext(),g);
        rv.setAdapter(adapterGame);

        adapterGame.setOnClickLister(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = g.get(rv.getChildAdapterPosition(v)).getId();
                //Toast.makeText(getActivity(),id,Toast.LENGTH_SHORT).show();
                Fragment fragment = new GameFragment(id);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void getGameInfo() {

        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).child("games").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                games_favoritos.removeAll(games_favoritos);
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        String idgame = ds.getKey();
                        //Toast.makeText(getContext(),idgame,Toast.LENGTH_SHORT).show();
                        games_favoritos.add(unigamesApi.getInfoFromAGame(idgame));
                    }
                adapterGame.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
