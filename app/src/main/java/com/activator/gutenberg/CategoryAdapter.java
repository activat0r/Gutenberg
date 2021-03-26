package com.activator.gutenberg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryModel>{
private ArrayList<CategoryModel> listitems;
private LayoutInflater mInflater;

Context context;
    CategoryAdapter(Context context, ArrayList<CategoryModel> listitems){
        super(context, R.layout.item_category);
        this.context = context;
        this.listitems = listitems;
        this.mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return listitems.size();
    }

    @Override
    public CategoryModel getItem(int position) {
        return listitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        if (convertView == null) {
            view = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_category,parent,false);
            view.catName = convertView.findViewById(R.id.cat_name);
            view.catIcon = convertView.findViewById(R.id.cat_icon);
            view.catEndDrawable = convertView.findViewById(R.id.cat_arrow);
            convertView.setTag(view);

        }else{
            view = (ViewHolder) convertView.getTag();
        }

        view.catName.setText(listitems.get(position).getCategoryName());
        view.catIcon.setImageDrawable(listitems.get(position).getCategoryIcon());
        view.catEndDrawable.setImageDrawable(context.getDrawable(R.drawable.ic_next));

        return convertView;
    }
    static class ViewHolder {
        ImageView catIcon;
        TextView catName;
        ImageView catEndDrawable;
    }
}
