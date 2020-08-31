package com.example.bnotes.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.selection.SelectionTracker;

import com.example.bnotes.R;
import com.example.bnotes.UpdateNotes;

/**
 * Created by brijesh on 26/3/18.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ItemListViewHolder> {

    //private List<Item> itemList;
    private SelectionTracker selectionTracker;
    private Context context;
    private ArrayList note_id, note_link, note_title, note_subject;
    Activity activity;

   // public CustomListAdapter(List<Item> itemList) {
     //   this.itemList = itemList;
  //  }




    public CustomAdapter(Activity activity, Context context, ArrayList note_id, ArrayList note_title, ArrayList note_link, ArrayList note_subject) {
        this.activity = activity;
        this.context = context;
        this.note_id = note_id;
        this.note_title = note_title;
        this.note_link = note_link;
        this.note_subject = note_subject;


    }




    public SelectionTracker getSelectionTracker() {
        return selectionTracker;
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row_notes, parent, false);

        return new ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemListViewHolder holder, final int position) {


        //Item item = itemList.get(position);
   //     holder.bind(holder, selectionTracker.isSelected(note_id),position);



//        if(selectionTracker.isSelected(note_id.get(position))) {
//            holder.constraintLayout.setBackgroundResource(R.drawable.button_selected);
//        }
//        else if (!selectionTracker.isSelected(note_id.get(position))) {
//            holder.constraintLayout.setBackgroundResource(R.drawable.button_normal);
//        }

        holder.relativeLayout.setBackgroundResource
                (selectionTracker.isSelected(note_id.get(position)) ? R.drawable.shape22 : R.drawable.shape1);


        holder.note_id_txt.setText(String.valueOf(position + 1));
        holder.note_title_txt.setText(String.valueOf(note_title.get(position)));
        holder.note_link_txt.setText(String.valueOf(note_link.get(position)));
        holder.note_subject_txt.setText(String.valueOf(note_subject.get(position)));


            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!selectionTracker.hasSelection()) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                //activity.finish();
                                Intent intent = new Intent(context, UpdateNotes.class);
                                intent.putExtra("id", String.valueOf(note_id.get(position)));
                                intent.putExtra("title", String.valueOf(note_title.get(position)));
                                intent.putExtra("link", String.valueOf(note_link.get(position)));
                                intent.putExtra("subject", String.valueOf(note_subject.get(position)));

                                intent.putExtra("position",String.valueOf(position));

                                activity.startActivityForResult(intent, 1);


                            }
                        },100);


                    }
                }
            });




        setFadeAnimation(holder.itemView);
        holder.lineNumberVisibility(holder);





        //changeLayoutSize(holder, position);


    }

//    private void changeLayoutSize(@NonNull ItemListViewHolder holder, int position) {
//        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//
//        int newLayoutWidth = String.valueOf(note_link.get(position)).length()*100;
//
//        if(newLayoutWidth <= 300) {
//            params.height = 300; //or holder.getHeight()
//        }
//        if(newLayoutWidth > 300 && newLayoutWidth < 800) {
//            params.height = note_link.size();
//        }
//        if (newLayoutWidth >= 800){
//            params.height = 800;
//
//
//        }
//    }

    @Override
    public int getItemCount() {
        return note_id.size();
    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder implements ViewHolderWithDetails {
        //TextView itemId, itemName, itemPrice;
        TextView note_id_txt, note_title_txt, note_link_txt, note_subject_txt;
        //LinearLayout noteLayout;
        RelativeLayout relativeLayout;

        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemId = itemView.findViewById(R.id.itemId);
            //itemName = itemView.findViewById(R.id.itemName);
            //itemPrice = itemView.findViewById(R.id.itemPrice);

            note_id_txt = itemView.findViewById(R.id.note_id_txt);
            note_title_txt = itemView.findViewById(R.id.note_title_txt);
            note_link_txt = itemView.findViewById(R.id.note_link_txt);
            note_subject_txt = itemView.findViewById(R.id.note_subject_txt);
            //noteLayout = itemView.findViewById(R.id.noteLayout);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);


        }

        private void lineNumberVisibility(ItemListViewHolder holder) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if(prefs.getBoolean("item_numbers",false)) {
                holder.note_id_txt.setVisibility(View.GONE);
            }
            else {
                holder.note_id_txt.setVisibility(View.VISIBLE);
            }
        }

/*        public final void bind(RecyclerView.ViewHolder viewHolder, boolean isActive, final int position) {

            //itemView.setActivated(isActive);
            //itemPrice.setText(item.getItemPrice() + "$");
            //itemName.setText(item.getItemName());
            //itemId.setText(item.getItemId() + "");

            if(selectionTracker.isSelected(note_id.get(position))) {
                constraintLayout.setBackgroundResource(R.drawable.button_selected);
            }
            else if (!selectionTracker.isSelected(note_id.get(position))) {
                constraintLayout.setBackgroundResource(R.drawable.button_normal);
            }

            note_id_txt.setText(String.valueOf((position + 1)));
            note_title_txt.setText(String.valueOf(note_title.get(position)));
            note_link_txt.setText(String.valueOf(note_link.get(position)));
            note_subject_txt.setText(String.valueOf(note_subject.get(position)));



        }*/

        @Override
        public MyItemDetail getItemDetails() {
            return new MyItemDetail(getAdapterPosition(), String.valueOf(note_id.get(getAdapterPosition())));
        }
    }

    public void setFadeAnimation(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0,0,-20,0);
//      AlphaAnimation anim = new AlphaAnimation(0.9f, 1.0f);
//      anim.setDuration(500);
//      view.startAnimation(anim);

        translateAnimation.setDuration(300);
        view.startAnimation(translateAnimation);


    }
}




