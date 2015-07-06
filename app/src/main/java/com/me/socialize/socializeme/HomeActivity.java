package com.me.socialize.socializeme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class HomeActivity extends ActionBarActivity implements View.OnClickListener {
    //View-ovi
    EditText editTextPost;
    Button buttonPost;
    EditText editTextSearchPerson;
    Button buttonSearchPerson;
    String userEmail;
    RelativeLayout relativeLayoutContent;
    ScrollView scrollViewContent;


    ArrayList<Post>Posts;
    ArrayList<Post>LoadedPosts;
    int LastPostNumber;
    boolean isLoadingPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FindViews();
        ShowGuideDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            RestartActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    void ShowGuideDialog()
    {
        SharedPreferences sharedPreferencesSettings = getSharedPreferences("SocializeMeSettings",0);
        boolean dialogSeen = sharedPreferencesSettings.getBoolean("DIALOG1",false);
        if(dialogSeen == false)
        {
            final Dialog dialogGuide = new Dialog(HomeActivity.this, R.style.custom_dialog);
            dialogGuide.setContentView(R.layout.custom_dialog);
            dialogGuide.setTitle("Guide");

            TextView textViewGuideText = (TextView) dialogGuide.findViewById(R.id.textViewGuideText);
            textViewGuideText.setText("Use search bar to find people you are interested in. Once you are following someone, their posts will show up here. You can also click on persons name below to visit their profile. To load more posts just scroll down!");
            Button buttonDialogButtonOk = (Button) dialogGuide.findViewById(R.id.buttonDialogOk);
            buttonDialogButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogGuide.dismiss();//ugasi dialog na "OK"
                }
            });
            dialogGuide.show();
            SharedPreferences.Editor sharedPreferencesSettingsEditor = sharedPreferencesSettings.edit();
            sharedPreferencesSettingsEditor.putBoolean("DIALOG1",true);
            sharedPreferencesSettingsEditor.commit();
        }
    }

    void FindViews()
    {
        isLoadingPosts = false;
        Posts = new ArrayList<Post>();
        LoadedPosts = new ArrayList<Post>();
        LastPostNumber = 0;
        //dohvati email iz proslog activity-a
        userEmail = getIntent().getExtras().getString("EMAIL");
        //sakrij action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //pronadi view-ove
        scrollViewContent = (ScrollView)findViewById(R.id.scrollViewContent);
        editTextPost = (EditText)findViewById(R.id.editTextPost);//Post edittext
        buttonPost = (Button)findViewById(R.id.buttonPost);//button za postanje
        editTextSearchPerson = (EditText)findViewById(R.id.editTextSearchPerson);//edittext za search
        buttonSearchPerson = (Button)findViewById(R.id.buttonSearchPerson);//button za search
        relativeLayoutContent = (RelativeLayout)findViewById(R.id.RelativeLayoutContent);//relative layout u scrollviewu gdje su prikazani postovi.

        //Postavi event listenere
        SetEvents();
        new RunScripts().execute("GET",userEmail+"",LastPostNumber+"");
    }
    void SetEvents()
    {
        buttonPost.setOnClickListener(this);
        buttonSearchPerson.setOnClickListener(this);

        scrollViewContent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (IsScrollAtBottom() && isLoadingPosts == false) {
                    new RunScripts().execute("GET", userEmail, LastPostNumber+"");
                    isLoadingPosts = true;
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        //Post button je kliknut
        if(v == buttonPost)
        {
            //Provjeri da post nije prazan
            if(IsPostValid(editTextPost.getText().toString())) {

                new RunScripts().execute("POST");
            }
            else
            {
                Toast.makeText(this,"Post field can't be empty!",Toast.LENGTH_SHORT).show();
            }
        }
        //Search button je pritisnut
        else if(v == buttonSearchPerson)
        {
            //provjeri da editTextSearchPerson text nije prazan
            if(editTextSearchPerson.getText().toString().length() > 0)
            {
                String SearchText = editTextSearchPerson.getText().toString();
                String Firstname = "";//Ime
                String Lastname = "";//Prezime
                if(SearchText.contains(" "))//Ima razmak tako da je user vjerovatno upisao i ime i prezime
                {
                    char LastLetter = SearchText.charAt(SearchText.length()-1);
                    if(LastLetter != ' ')
                    {
                        String[] Name = SearchText.split(" ");
                        Firstname = Name[0];
                        Lastname = Name[1];
                    }
                }
                else//Nema razmak sto znaci da je upisano samo ime ili samo prezime
                {
                    Firstname = SearchText;
                }

                Intent SearchPersonIntent = new Intent(this,SearchPersonActivity.class);
                SearchPersonIntent.putExtra("FIRSTNAME",Firstname);
                SearchPersonIntent.putExtra("LASTNAME",Lastname);
                SearchPersonIntent.putExtra("USEREMAIL",userEmail);
                startActivity(SearchPersonIntent);
            }

        }
    }
    public void RestartActivity() {

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    boolean IsScrollAtBottom()
    {
        int scrollY = scrollViewContent.getScrollY() + scrollViewContent.getHeight();
        int maxScrollY = scrollViewContent.getChildAt(0).getHeight();


        if (maxScrollY - scrollY <= 0) {
            return true;
        }
        return false;
    }

    void PostsToViews()
    {
        if(Posts.size() > 0)
        {
            int counter = 1;
            for (final Post CurrentPost : Posts) {//za svaki post dinamicki napravi novi textview

                /*
                LastPostNumber * 3 + 1 = ID za TextView za autora posta
                LastPostNumber * 3 + 2 = ID za TextView za tekst posta
                LastPostNumber * 3 + 3 = ID za TextView za datum posta
                LastPostNumber * 3     = ID za prethodni TextView za datum posta
                 */
                //Prvo napraviti textview za autora posta
                TextView textViewPoster = new TextView(getApplicationContext());//novi textview
                textViewPoster.setId(LastPostNumber * 3 + 1);//postavi id od textviewa
                RelativeLayout.LayoutParams layoutParamsPoster = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//parametri za textview
                if (LastPostNumber != 0)//Ukoliko nije prvi post, stavi pravilo da bude ispod prethodnog
                {
                    layoutParamsPoster.addRule(RelativeLayout.BELOW, LastPostNumber*3);
                }
                else
                {
                    layoutParamsPoster.addRule(RelativeLayout.BELOW,buttonPost.getId());
                }
                textViewPoster.setLayoutParams(layoutParamsPoster);
                textViewPoster.setText("Posted by " + CurrentPost.getPosterName() + ":");//text na post
                textViewPoster.setTextSize(15);
                textViewPoster.setTextColor(Color.parseColor("#FFFFFF"));//boja fonta na bijelu
                textViewPoster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent personProfileActivity = new Intent(getApplicationContext(), PersonProfileActivity.class);
                        personProfileActivity.putExtra("FULLNAME",CurrentPost.getPosterName());
                        personProfileActivity.putExtra("EMAIL",CurrentPost.getPosterEmail());
                        personProfileActivity.putExtra("USEREMAIL",userEmail);
                        startActivity(personProfileActivity);
                    }
                });
                relativeLayoutContent.addView(textViewPoster);


                //Drugo napravit textview za post
                TextView textViewPost= new TextView(getApplicationContext());//novi textview
                textViewPost.setId(LastPostNumber * 3 + 2);//postavi id od textviewa
                RelativeLayout.LayoutParams layoutParamsPost = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//parametri za textview
                layoutParamsPost.addRule(RelativeLayout.BELOW, LastPostNumber * 3 + 1);//Dodati pravila kako ce biti poslozen; Postaviti ga ispod Teksta posta.
                textViewPost.setLayoutParams(layoutParamsPost);
                textViewPost.setText(CurrentPost.getPostContent());
                textViewPost.setTextSize(15);
                textViewPost.setTextColor(Color.parseColor("#FFFFFF"));
                relativeLayoutContent.addView(textViewPost);

                //trece napravit textview za datum
                TextView textViewPostDate= new TextView(getApplicationContext());//novi textview
                textViewPostDate.setId(LastPostNumber * 3 + 3);//postavi id od textviewa
                RelativeLayout.LayoutParams layoutParamsDate = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//parametri za textview
                layoutParamsDate.addRule(RelativeLayout.BELOW, LastPostNumber * 3 + 2);//Dodati pravila kako ce biti poslozen; Postaviti ga ispod Teksta posta.
                textViewPostDate.setLayoutParams(layoutParamsDate);
                textViewPostDate.setText("Posted on: " + CurrentPost.getPostDate());
                textViewPostDate.setTextSize(10);
                textViewPostDate.setTextColor(Color.parseColor("#FFFFFF"));
                relativeLayoutContent.addView(textViewPostDate);

                LastPostNumber++;
            }
            Posts.clear();
            isLoadingPosts = false;
        }
    }

    boolean IsPostValid(String Post)
    {
        Post = Post.replaceAll("\\s+","");//makni sve razmake, tabove, nove redove itd..
        if(Post.length()>0)
            return true;
        return false;
    }

    class RunScripts extends AsyncTask<String, String, String>
    {

        ProgressDialog progDialog;
        @Override
        protected  void onPreExecute()
        {
            super.onPreExecute();
            progDialog = new ProgressDialog(HomeActivity.this);
            progDialog.setMessage("Loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {//O prvom parametru ovisi hoce li dohvatiti postove ili postat
            if (params[0].equals("POST")) {//Ukoliko je prvi parametar "POST" treba spremiti post u bazu
                //Pohrani podatke u arraylist koje ce proslijedit u PHP za ubacivanje u bazu
                ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
                podatci.add(new BasicNameValuePair("email", userEmail));
                podatci.add(new BasicNameValuePair("content", editTextPost.getText().toString()));
                //odlazi na stranicu koja se pokusava ulogirat
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://socializeme.site90.com/Post.php");//php koji ce executeat nasu skriptu
                    httpPost.setEntity(new UrlEncodedFormEntity(podatci, HTTP.UTF_8));//Proslijedi podatke php-u
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    //Dohvati response da dobijemo source
                    HttpEntity entity = httpResponse.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String result = reader.readLine();
                    inputStream.close();
                    return result;//Vrati source od stranice koji ce nam reci dali je post uspio
                } catch (Exception e) {

                }
                return "Login failed!";
            }
            else if(params[0].equals("GET"))
            {
                //Pohrani podatke u arraylist koje ce proslijedit u PHP za ubacivanje u bazu
                ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
                podatci.add(new BasicNameValuePair("email", userEmail));
                podatci.add(new BasicNameValuePair("lastpost", LastPostNumber+ ""));
                //odlazi na stranicu koja se pokusava ulogirat
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://socializeme.site90.com/getPostsFromFollowers.php");//php koji ce executeat nasu skriptu
                    httpPost.setEntity(new UrlEncodedFormEntity(podatci, HTTP.UTF_8));//Proslijedi podatke php-u
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    //Dohvati response da dobijemo source
                    HttpEntity entity = httpResponse.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) // citaj red po red
                    {

                        if (IsPostValid(line.toString())) {
                            if (line.toString().equals("<!-- Hosting24 Analytics Code -->"))
                                break;
                            String CurrentPostEmail = reader.readLine();
                            String CurrentPostContent = reader.readLine();
                            String CurrentPostDate = reader.readLine();
                            Post CurrentPost = new Post(line,CurrentPostEmail,CurrentPostContent,CurrentPostDate);
                            //provjeri da post ne postoji vec
                            boolean postAlreadyExists = false;
                            for(Post curPost : LoadedPosts)
                            {
                                if(curPost.ComparePostTo(CurrentPost))
                                    postAlreadyExists = true;
                            }
                            if(!postAlreadyExists) {
                                Posts.add(CurrentPost);
                                LoadedPosts.add(CurrentPost);
                            }
                        }
                    }
                    inputStream.close();
                    return "PostToViews";
                } catch (Exception e) {

                }
            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("Post Created!\t"))//Provjerava dali je post uspjesno napravljen
            {
                RestartActivity();//reloada activity kako bi ucitao ponovo sve postove
            }
            else if(s.equals("PostToViews"))
            {
                PostsToViews();
            }

            progDialog.dismiss();
        }
    }
}
