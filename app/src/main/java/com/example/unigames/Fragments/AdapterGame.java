package com.example.unigames.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unigames.Game;
import com.example.unigames.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterGame extends RecyclerView.Adapter<AdapterGame.MyViewHolder> implements View.OnClickListener{

    Context mContext;
    List<Game> mData;
    //Dialog mDialog;

    //listener
    private View .OnClickListener listener;

    private OnItemClickListener miListener;



    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    public AdapterGame(Context mContext, List<Game> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.cardview_item_game,parent,false);
        v.setOnClickListener(this);
        final MyViewHolder vHolder = new MyViewHolder(v);

        //Dialog ini

        /*
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_game);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        */


        /*vHolder.item_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dialog_name = (TextView) mDialog.findViewById(R.id.dialog_Name_game);
                ImageView dialog_image = (ImageView) mDialog.findViewById(R.id.dialog_img_game);
                Button dialog_game_btn = (Button) mDialog.findViewById(R.id.dialog_btn_fragment_game);
                dialog_game_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(miListener != null) {
                            String id = mData.get(vHolder.getAdapterPosition()).getId();
                            miListener.onItemClick(id);
                        }
                    }

                });
                Button dialog_add_btn = (Button) mDialog.findViewById(R.id.dialog_btn_add_game);
                dialog_name.setText(mData.get(vHolder.getAdapterPosition()).getName());
                Picasso.get().load(mData.get(vHolder.getAdapterPosition()).getUrlBackground_image()).into(dialog_image);
                dialog_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                //mDialog.show();
            }
        });
*/
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_name.setText(mData.get(position).getName());

        Picasso.get().load(mData.get(position).getUrlBackground_image()).resize(1000,600).into(holder.img);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView item_game;
        private TextView tv_name;
        private ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_game = (CardView) itemView.findViewById(R.id.game_item_id);
            tv_name = (TextView) itemView.findViewById(R.id.game_title_id);
            img = (ImageView) itemView.findViewById(R.id.game_img_id);
        }
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onClick(v);
        }
    }

    public void setOnClickLister(View.OnClickListener listener) {
        this.listener = listener;
    }
}
