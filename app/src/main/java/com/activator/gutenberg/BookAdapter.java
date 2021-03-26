package com.activator.gutenberg;

import android.content.Context;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{


    private List<Book> listitems;
    private Context context;
    private OnItemClickListener mlistener;
    private OnLongClickListener longClickListener;

    public BookAdapter( Context context,List<Book> listitems) {
        this.listitems = listitems;
        this.context = context;
        setHasStableIds(false);
    }

public interface  OnLongClickListener{
        void  onItemLongClick(int position);
}

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public void setLongClickListener(OnLongClickListener listener){
        longClickListener = listener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book listItem = listitems.get(position);
        holder.listItem_textview_bookName.setText(listItem.getBookName());
        holder.listItem_textview_bookAuthor.setText(listItem.getBookAuthor());
        Picasso.get().load(listItem.getBookImage()).into(holder.listItem_textview_bookImage);
        holder.listItem_textview_bookImage.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView listItem_textview_bookAuthor;
        public TextView listItem_textview_bookName;
        public ImageView listItem_textview_bookImage;
        public ViewHolder(View itemView) {
            super(itemView);
            listItem_textview_bookName = (TextView) itemView.findViewById(R.id.book_name) ;
            listItem_textview_bookAuthor = (TextView) itemView.findViewById(R.id.book_author);
            listItem_textview_bookImage = (ImageView) itemView.findViewById(R.id.book_image);

            itemView.setOnLongClickListener(v -> {
                if (longClickListener!= null){
                    int position = getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION){
                        longClickListener.onItemLongClick(position);
                    }
                }
                return true;
            });

            itemView.setOnClickListener(v -> {
                if (mlistener!= null){
                    int position = getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION){
                        mlistener.onItemClick(position);
                    }
                }
            });


        }
    }

}

