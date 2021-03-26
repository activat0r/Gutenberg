package com.activator.gutenberg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class BooksActivity extends AppCompatActivity implements BookAdapter.OnItemClickListener, BookAdapter.OnLongClickListener{
    private RecyclerView booksRecyclerView;
    private ArrayList<Book> booklist;
    private BookAdapter bookAdapter;
    private String category;
    private TextView title;
    private ImageView back;
    private EditText searchBar;
    private String nextPageUrl;
    private Context context;
    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        booksRecyclerView = findViewById(R.id.books_recyclerView_bookshelf);
        nextPageUrl = "";
        back = findViewById(R.id.books_image_back);
        title = findViewById(R.id.books_textview_category);
        searchBar = findViewById(R.id.books_editTextView_search);

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
                if(actionId == EditorInfo.IME_ACTION_GO || actionId==EditorInfo.IME_ACTION_SEARCH){
                    loading=true;
                    formUrl(category,v.getText().toString());
                    searchBar.clearFocus();
                    return true;
                }
                return false;
            }
        });

        GridLayoutManager layoutManager = (GridLayoutManager) booksRecyclerView.getLayoutManager();
        loading = false;
        booksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItems = layoutManager.getItemCount();
                int lastViewablePosition = layoutManager.findLastVisibleItemPosition();

                if((totalItems - lastViewablePosition-1 < layoutManager.getSpanCount()*2) && !nextPageUrl.equals("") && !loading){
                    loading=true;
                    getBooks(nextPageUrl);
                }
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
        context = this;
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
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage("We are facing issues establishing connection to the server. Please try again later.")
                            .setNeutralButton("Okay",null)
                            .setIcon(ContextCompat.getDrawable(context,R.drawable.ic_error))
                            .show();
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
            if(resp.getInt("count")==0){
                new AlertDialog.Builder(context)
                        .setTitle("No books found")
                        .setMessage("We were unable to find any books for you. Please try later.")
                        .setNeutralButton("Okay",null)
                        .setIcon(ContextCompat.getDrawable(context,R.drawable.ic_error))
                        .show();
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
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("Something went wrong. Please try again later.")
                    .setNeutralButton("Okay",null)
                    .setIcon(ContextCompat.getDrawable(context,R.drawable.ic_error))
                    .show();
        }

        Objects.requireNonNull(booksRecyclerView.getAdapter()).notifyDataSetChanged();
        Log.d("books", "generateBookList: "+booksRecyclerView.getAdapter().getItemCount());
        loading = false;
    }


    @Override
    public void onItemClick(int position) {
        Log.d("books","On Click");

        if(booklist.get(position).getBookUrl().equals("")){
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("No viewable version available")
                    .setNeutralButton("Okay",null)
                    .setIcon(ContextCompat.getDrawable(context,R.drawable.ic_error))
                    .show();
        }
        else{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(booklist.get(position).getBookUrl()));
            startActivity(browserIntent);
        }

    }

    @Override
    public void onItemLongClick(int position) {
        Toast.makeText(this,booklist.get(position).getBookName(),Toast.LENGTH_SHORT).show();
    }
}