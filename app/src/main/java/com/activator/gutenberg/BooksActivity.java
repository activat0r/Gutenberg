package com.activator.gutenberg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Logger;

public class BooksActivity extends AppCompatActivity implements BookAdapter.OnItemClickListener, BookAdapter.OnLongClickListener{
    private RecyclerView booksRecyclerView;
    private ArrayList<Book> booklist;
    private BookAdapter bookAdapter;
    private String category;
    private TextView title;
    private ImageView back;
    private EditText searchBar;
    private String nextPageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        booksRecyclerView = findViewById(R.id.books_recyclerView);
        nextPageUrl = "";
        back = findViewById(R.id.books_back);
        title = findViewById(R.id.books_category);
        searchBar = findViewById(R.id.books_search);

        booklist = new ArrayList<>();
        booksRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        bookAdapter = new BookAdapter(this,booklist);
        booksRecyclerView.setAdapter(bookAdapter);
        category = getIntent().getStringExtra("Category_Name");

        bookAdapter.setOnItemClickListener(this);
        bookAdapter.setLongClickListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    formUrl(category,v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        title.setText(category);
        booklist.clear();
        bookAdapter.notifyDataSetChanged();
        formUrl(category);

    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void formUrl(String category) {
        String url = getString(R.string.baseUrl)+"?mime_type=image/jpeg&topic="+category;
        getBooks(url);
        booklist.clear();
        bookAdapter.notifyDataSetChanged();
    }

    private void formUrl(String category,String criteria) {
        String url = getString(R.string.baseUrl)+"?mime_type=image/jpeg&topic="+category+"&search="+criteria.replace(" ","%20");
        booklist.clear();
        bookAdapter.notifyDataSetChanged();
        getBooks(url);
    }

    private void getBooks(String url){
        Log.d("books","getBooks");

        com.android.volley.RequestQueue mRequestQueue = Volley.newRequestQueue(this);
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("books",response);
                    if (response!= null) {
                        generateBookList(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //todo error dialog
                    Log.d("books",error.getMessage());

                }
            })

            {
                @Override
                public Priority getPriority() {
                    return Priority.IMMEDIATE;
                }
            };
            int socketTimeout = 10000;//10 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            stringRequest.setShouldCache(false);
            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void generateBookList(String response) {
        try{

            String name="";
            String author="";
            String imageUrl="";
            JSONObject resp = new JSONObject(response);
            JSONArray booksArray = resp.getJSONArray("results");
            
            if(resp.has("next") && !resp.getString("next").equals("")){
                nextPageUrl = resp.getString("next");
            }

            for(int counter =0;counter<booksArray.length();counter++) {
                name = booksArray.getJSONObject(counter).getString("title");

                if (booksArray.getJSONObject(counter).has("authors")) {
                    if (booksArray.getJSONObject(counter).getJSONArray("authors").length() > 0)
                        author = booksArray.getJSONObject(counter).getJSONArray("authors").getJSONObject(0).getString("name");
                }
                if (booksArray.getJSONObject(counter).getJSONObject("formats").has("image/jpeg")) {
                    imageUrl = booksArray.getJSONObject(counter).getJSONObject("formats").getString("image/jpeg");
                }

                JSONObject formats = booksArray.getJSONObject(counter).getJSONObject("formats");
                Iterator<String> keys = formats.keys();
                String bookUrl="";

                while (keys.hasNext()) {
                    String nextItem = keys.next();
                    if (nextItem.contains("html"))
                        bookUrl = formats.getString(nextItem);
                    else if (nextItem.contains("pdf"))
                        bookUrl = formats.getString(nextItem);
                    else if (nextItem.contains("txt"))
                        bookUrl = formats.getString(nextItem);
                }

                booklist.add(new Book(name, author, imageUrl, bookUrl));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            //todo dialog
        }

        Objects.requireNonNull(booksRecyclerView.getAdapter()).notifyDataSetChanged();
        Log.d("books", "generateBookList: "+booksRecyclerView.getAdapter().getItemCount());
    }


    @Override
    public void onItemClick(int position) {
        Log.d("books","On Click");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(booklist.get(position).getBookUrl()));
        startActivity(browserIntent);
    }

    @Override
    public void onItemLongClick(int position) {
        Toast.makeText(this,booklist.get(position).getBookName(),Toast.LENGTH_SHORT).show();
    }
}