package com.activator.gutenberg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView categoryListview;
    ArrayList<CategoryModel> categoryList;
    CategoryAdapter categoryAdapter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        categoryListview = findViewById(R.id.genreList);
        categoryList = new ArrayList<>();

        categoryList.add(new CategoryModel(ContextCompat.getDrawable(context,R.drawable.ic_fiction),"Fiction"));
        categoryList.add(new CategoryModel(ContextCompat.getDrawable(context,R.drawable.ic_drama),"Drama"));
        categoryList.add(new CategoryModel(ContextCompat.getDrawable(context,R.drawable.ic_humour),"Humour"));
        categoryList.add(new CategoryModel(ContextCompat.getDrawable(context,R.drawable.ic_politics),"Politics"));
        categoryList.add(new CategoryModel(ContextCompat.getDrawable(context,R.drawable.ic_philosophy),"Philosophy"));
        categoryList.add(new CategoryModel(ContextCompat.getDrawable(context,R.drawable.ic_history),"History"));
        categoryList.add(new CategoryModel(ContextCompat.getDrawable(context,R.drawable.ic_adventure),"Adventure"));

        categoryAdapter = new CategoryAdapter(context,categoryList);
        categoryListview.setAdapter(categoryAdapter);

        categoryListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), BooksActivity.class);
                intent.putExtra("Category_Name",categoryList.get(position).categoryName);
                startActivity(intent);
            }
        });
    }
}