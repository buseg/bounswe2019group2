package tk.traiders.components.article;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tk.traiders.LoginActivity;
import tk.traiders.MainActivity;
import tk.traiders.R;
import tk.traiders.components.annotation.AnnotationFragment;
import tk.traiders.components.annotation.ShowAnnotationsFragment;
import tk.traiders.components.article.adapters.CommentAdapter;
import tk.traiders.marshallers.AnnotationMarshaller;
import tk.traiders.marshallers.ArticleMarshaller;
import tk.traiders.marshallers.CommentMarshaller;
import tk.traiders.marshallers.LikeMarshaller;
import tk.traiders.models.Annotation;
import tk.traiders.models.Article;
import tk.traiders.models.Comment;

public class ViewArticleActivity extends AppCompatActivity {

    private static final String URL_COMMENTS = "https://api.traiders.tk/comments/article/";
    private static final String URL_LIKES = "https://api.traiders.tk/likes/";
    private static final String URL_ANNOTATIONS = "https://annotation.traiders.tk/annotations/";


    private Article article = null;

    private TextView textView_title;
    private ImageView imageView_image;
    private TextView textView_content;
    private TextView textView_publishedAt;
    private TextView textView_likeCount;
    private TextView textView_writtenBy;
    private TextView textView_noComment;
    private RecyclerView recylerView_commentList;
    private RequestQueue requestQueue;
    private EditText editText_comment;
    private ImageView imageView_comment;

    private List<Pair<Integer, Integer>> pairs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);

        requestQueue = Volley.newRequestQueue(this);

        textView_title = findViewById(R.id.activityViewArticle_textView_title);
        imageView_image = findViewById(R.id.activityViewArticle_imageView_image);
        textView_content = findViewById(R.id.activityViewArticle_textView_content);
        textView_publishedAt = findViewById(R.id.textView_article_publishedAt);
        textView_writtenBy = findViewById(R.id.textView_article_writtenBy);
        textView_likeCount = findViewById(R.id.textView_article_likeCount);
        textView_noComment = findViewById(R.id.textView_article_noComment);
        editText_comment = findViewById(R.id.editText_comment_add);
        imageView_comment = findViewById(R.id.imageView_comment_add);

        textView_content.setTextIsSelectable(true);
        textView_content.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.removeItem(android.R.id.selectAll);
                menu.removeItem(android.R.id.cut);
                menu.removeItem(android.R.id.shareText);
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                menu.add(0, 99, 0, "Annotate");

                int min = 0;
                int max = textView_content.getText().length();
                if (textView_content.isFocused()) {
                    final int selStart = textView_content.getSelectionStart();
                    final int selEnd = textView_content.getSelectionEnd();

                    min = Math.max(0, Math.min(selStart, selEnd));
                    max = Math.max(0, Math.max(selStart, selEnd));
                }

                int numberOfAnnotations = 0;

                for(Pair<Integer, Integer> pair: pairs){
                    if(pair.first <= min && max <= pair.second){
                        numberOfAnnotations++;
                    }
                }

                if(numberOfAnnotations > 0) {
                    menu.add(0, 100, 0, "Show Annotations");
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Called when an action mode is about to be exited and
                // destroyed
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case 99:
                        int min = 0;
                        int max = textView_content.getText().length();
                        if (textView_content.isFocused()) {
                            final int selStart = textView_content.getSelectionStart();
                            final int selEnd = textView_content.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        // Perform your definition lookup with the selected text
                        final String selectedText = textView_content.getText().subSequence(min, max).toString();


                        FragmentManager fm = getSupportFragmentManager();
                        AnnotationFragment annotationFragment = AnnotationFragment.newInstance(selectedText, article.getUrl(), MainActivity.getUserURL(ViewArticleActivity.this), min, max );
                        annotationFragment.show(fm, "fragment_annotation");
                        // Finish and close the ActionMode
                        mode.finish();
                        return true;
                    case 100:
                        int min1 = 0;
                        int max1 = textView_content.getText().length();
                        if (textView_content.isFocused()) {
                            final int selStart = textView_content.getSelectionStart();
                            final int selEnd = textView_content.getSelectionEnd();

                            min1 = Math.max(0, Math.min(selStart, selEnd));
                            max1 = Math.max(0, Math.max(selStart, selEnd));
                        }
                        // Perform your definition lookup with the selected text

                        ShowAnnotationsFragment showAnnotationsFragment = ShowAnnotationsFragment.newInstance(article.getUrl(),min1, max1);
                        showAnnotationsFragment.show(getSupportFragmentManager(), "fragment_show_annotation");
                        // Finish and close the ActionMode
                        mode.finish();

                        break;

                    default:
                        break;
                }
                return false;
            }

        });

        imageView_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.isUserLoggedIn(ViewArticleActivity.this)) {
                    ViewArticleActivity.this.startActivity(new Intent(ViewArticleActivity.this, LoginActivity.class));
                    Toast.makeText(ViewArticleActivity.this, "Please log in to continue", Toast.LENGTH_SHORT).show();
                    return;
                }

                String comment = editText_comment.getText().toString();

                if(comment.length() == 0) {
                    editText_comment.setError("Enter your comment!");
                    return;
                }

                StringRequest postRequest = new StringRequest(Request.Method.POST, URL_COMMENTS,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                editText_comment.setText("");
                                editText_comment.clearFocus();
                                InputMethodManager imm = (InputMethodManager)getSystemService(ViewArticleActivity.this.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editText_comment.getWindowToken(), 0);
                                fetchComments();
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Toast.makeText(ViewArticleActivity.this, "An error occured posting comment!", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }
                ) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = MainActivity.getAuthorizationHeader(ViewArticleActivity.this);
                        return headers != null ? headers : super.getHeaders();
                    }

                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();

                        params.put("content", comment);
                        params.put("article", article.getUrl());

                        return params;
                    }
                };

                requestQueue.add(postRequest);

            }
        });

        Intent intent = getIntent();

        if(intent != null) {
            if(intent.hasExtra("article")) {
                article = (Article) intent.getSerializableExtra("article");
            }
        }

        if(article != null){
            textView_title.setText(article.getTitle());
            if(!article.getImage().isEmpty())
                Picasso.get().load(article.getImage()).into(imageView_image);
            else
                imageView_image.setVisibility(View.GONE);
            textView_content.setText(article.getContent());
            textView_writtenBy.setText(article.getAuthor().getUsername());
            textView_publishedAt.setText(article.getCreatedAt());
            textView_likeCount.setText(article.getLikeCount());
        }

        recylerView_commentList = findViewById(R.id.recylerView_commentList);

        recylerView_commentList.setLayoutManager(new LinearLayoutManager(this));
        recylerView_commentList.setHasFixedSize(true);
        //recylerView_commentList.setAdapter(new CommentAdapter(this, new ArrayList<>()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void fetchAnnotations(){

        StringRequest request = new StringRequest(Request.Method.GET, URL_ANNOTATIONS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                List<Annotation> annotationList = AnnotationMarshaller.unmarshallList(response);
                List<Annotation> filteredAnnotationList = new ArrayList<>();
                for(Annotation annotation: annotationList){
                    if(annotation != null){
                        if(annotation.getTarget().getSource().equals(ViewArticleActivity.this.article.getUrl())){
                            filteredAnnotationList.add(annotation);
                        }
                    }
                }


                SpannableString contentAsSpannableString = new SpannableString(article.getContent());

                for(Annotation annotation: filteredAnnotationList) {
                    if(!annotation.getBody().getType().equals("TextualBody")){
                        continue;
                    }
                    String value = annotation.getTarget().getSelector().getValue();
                    int indexOfEqualSign = value.indexOf("=");
                    int indexOfComma = value.indexOf(",");
                    int startIndex = Integer.valueOf(value.substring(indexOfEqualSign + 1, indexOfComma));
                    int endIndex = Integer.valueOf(value.substring(indexOfComma + 1));
                    pairs.add(new Pair<>(startIndex, endIndex));

                    /*

                    int colorCode;

                    switch (new Random().nextInt(6)){
                        case 0:
                            colorCode = 0xFFFF0000;
                            break;
                        case 1:
                            colorCode = 0xFF00FF00;
                            break;
                        case 2:
                            colorCode = 0xFF0000FF;
                            break;
                        case 3:
                            colorCode = 0xFFFFFF00;
                            break;
                        case 4:
                            colorCode = 0xFF00FFFF;
                            break;
                        default:
                            colorCode = 0xFFFFFFFF;
                            break;
                    }


                     */

                    contentAsSpannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, 0);
                }

                textView_content.setText(contentAsSpannableString);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewArticleActivity.this, "An error occured fetching annotations", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = MainActivity.getAuthorizationHeader(ViewArticleActivity.this);
                return headers != null ? headers : super.getHeaders();
            }
        };

        requestQueue.add(request);


    }

    private void fetchComments(){

        Uri.Builder builder = Uri.parse(URL_COMMENTS).buildUpon();
        builder.appendQueryParameter("article", article.getId());

        String UrlWithArticleId = builder.build().toString();

        StringRequest request = new StringRequest(Request.Method.GET, UrlWithArticleId, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                List<Comment> commentList = CommentMarshaller.unmarshallList(response);

                if(commentList.size() == 0) {
                    textView_noComment.setVisibility(View.VISIBLE);
                } else {
                    textView_noComment.setVisibility(View.GONE);
                }

                recylerView_commentList.setAdapter(new CommentAdapter(ViewArticleActivity.this, commentList));
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewArticleActivity.this, "An error occured fetching comments", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = MainActivity.getAuthorizationHeader(ViewArticleActivity.this);
                return headers != null ? headers : super.getHeaders();
            }
        };

        requestQueue.add(request);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_article_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        StringRequest getRequest = new StringRequest(Request.Method.GET, article.getUrl(),
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        article = ArticleMarshaller.unmarshall(response);
                        invalidateOptionsMenu();
                        fetchComments();
                        fetchAnnotations();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(ViewArticleActivity.this, "An error occured fetching article!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = MainActivity.getAuthorizationHeader(ViewArticleActivity.this);
                return headers != null ? headers : super.getHeaders();
            }
        };

        requestQueue.add(getRequest);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(article == null || article.getLike() == null || !article.getLike().getUser().equals(MainActivity.getUserURL(this))) {
            menu.findItem(R.id.viewArticle_menu_whiteLike).setVisible(true);
            menu.findItem(R.id.viewArticle_menu_blackLike).setVisible(false);
        } else {
            menu.findItem(R.id.viewArticle_menu_whiteLike).setVisible(false);
            menu.findItem(R.id.viewArticle_menu_blackLike).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.viewArticle_menu_whiteLike:

                if(MainActivity.isUserLoggedIn(this)) {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, URL_LIKES,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    article.setLike(LikeMarshaller.unmarshall(response));
                                    invalidateOptionsMenu();
                                    Toast.makeText(ViewArticleActivity.this, "liked", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Toast.makeText(ViewArticleActivity.this, "An error occured liking article!", Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                            }
                    ) {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = MainActivity.getAuthorizationHeader(ViewArticleActivity.this);
                            return headers != null ? headers : super.getHeaders();
                        }

                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("article", article.getUrl());
                            return params;
                        }
                    };

                    requestQueue.add(postRequest);
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                    Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.viewArticle_menu_blackLike:
                StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, article.getLike().getUrl(),
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                article.setLike(null);
                                invalidateOptionsMenu();
                                Toast.makeText(ViewArticleActivity.this, "unliked", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Toast.makeText(ViewArticleActivity.this, "An error occured unliking article!", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }
                ) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = MainActivity.getAuthorizationHeader(ViewArticleActivity.this);
                        return headers != null ? headers : super.getHeaders();
                    }
                };

                requestQueue.add(deleteRequest);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
