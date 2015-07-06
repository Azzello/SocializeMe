package com.me.socialize.socializeme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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


public class PersonProfileActivity extends ActionBarActivity{
    String userEmail;//Email od trenutno ulogirane osobe
    String PersonName;//Ime osobe
    String PersonEmail;//Email osobe
    boolean IsFollowing;

    RelativeLayout relativeLayoutContent;
    Button buttonFollow;
    ScrollView scrollViewContent;
    TextView textViewName;
    ArrayList<Post> Posts;
    ArrayList<Post> LoadedPosts;//Sadrzava vec ucitane postove koji ce posluziti da duplo ucitavanje istog posta
    int LastPostNumber;
    boolean isLoadingPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);
        FindViews();
    }
    void FindViews()//varijable i view-i
    {
        IsFollowing = false;
        isLoadingPosts = false;
        LastPostNumber = 0;
        Posts = new ArrayList<Post>();
        LoadedPosts = new ArrayList<Post>();

        //sakrij actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Dohvati proslijedene varijable
        Bundle bundleVariables = getIntent().getExtras();
        userEmail = bundleVariables.getString("USEREMAIL", "");
        PersonName = bundleVariables.getString("FULLNAME", "");
        PersonEmail = bundleVariables.getString("EMAIL", "");

        //Dohvati view-e
        textViewName = (TextView)findViewById(R.id.textViewPersonName);
        buttonFollow = (Button)findViewById(R.id.buttonFollow);
        relativeLayoutContent = (RelativeLayout)findViewById(R.id.RelativeLayoutContent);
        scrollViewContent = (ScrollView)findViewById(R.id.scrollViewContent);

        //promjeni text textviewName-a
        textViewName.setText(PersonName);
        SetEventListeners();
        //Provjerava dali je korisnik na svom profilu, ako je treba sakriti Follow button.
        if(userEmail.equals(PersonEmail))
        {
            buttonFollow.setVisibility(View.INVISIBLE);
        }
        else
        {
            new RunScripts().execute("ISFOLLOW",userEmail,PersonEmail);
        }
        new RunScripts().execute("GET", PersonEmail, String.valueOf(LastPostNumber));
        isLoadingPosts = true;
        ShowGuideDialog();
    }

    void ShowGuideDialog()
    {
        SharedPreferences sharedPreferencesSettings = getSharedPreferences("SocializeMeSettings",0);
        boolean dialogSeen = sharedPreferencesSettings.getBoolean("DIALOG2",false);
        if(dialogSeen == false)
        {
            final Dialog dialogGuide = new Dialog(PersonProfileActivity.this, R.style.custom_dialog);
            dialogGuide.setContentView(R.layout.custom_dialog);
            dialogGuide.setTitle("Guide");

            TextView textViewGuideText = (TextView) dialogGuide.findViewById(R.id.textViewGuideText);
            textViewGuideText.setText("Scroll down to load more posts. If you like content from this user make sure you follow him!");
            Button buttonDialogButtonOk = (Button) dialogGuide.findViewById(R.id.buttonDialogOk);
            buttonDialogButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogGuide.dismiss();//ugasi dialog na "OK"
                }
            });
            dialogGuide.show();
            SharedPreferences.Editor sharedPreferencesSettingsEditor = sharedPreferencesSettings.edit();
            sharedPreferencesSettingsEditor.putBoolean("DIALOG2", true);
            sharedPreferencesSettingsEditor.commit();
        }
    }
    void SetEventListeners()
    {
        //scrollview scroll listener
        scrollViewContent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                if (IsScrollAtBottom() && isLoadingPosts == false)
                {
                    new RunScripts().execute("GET", PersonEmail, String.valueOf(LastPostNumber));
                    isLoadingPosts = true;
                }

            }
        });
        //Klik na gumb event
        buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsFollowing)
                    new RunScripts().execute("UNFOLLOW",userEmail,PersonEmail);
                else
                    new RunScripts().execute("FOLLOW", userEmail, PersonEmail);
            }
        });
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //dinamicki stvaranje textviewo za svaki post
    void PostsToViews()
    {
        if(Posts.size() > 0)
        {
            for (Post CurrentPost : Posts) {//za svaki post dinamicki napravi novi textview

                /*
                LastPostNumber * 2 + 1 = ID za TextView za tekst posta
                LastPostNumber * 2 + 2 = ID za TextView za datum posta
                LastPostNumber * 2     = ID za prethodni textview za datum
                 */
                //Prvo napraviti textview za tekst posta
                TextView textViewPost = new TextView(getApplicationContext());//novi textview
                textViewPost.setId(LastPostNumber * 2 + 1);//postavi id od textviewa
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//parametri za textview
                if (LastPostNumber != 0)//Ukoliko nije prvi post, stavi pravilo da bude ispod prethodnog
                {
                    layoutParams.addRule(RelativeLayout.BELOW, LastPostNumber*2);
                }
                textViewPost.setLayoutParams(layoutParams);
                textViewPost.setText(CurrentPost.getPostContent());//text na post
                textViewPost.setTextSize(15);//velicina fonta na 25
                textViewPost.setTextColor(Color.parseColor("#FFFFFF"));//boja fonta na bijelu
                relativeLayoutContent.addView(textViewPost);


                //Drugo napravit textview za datum posta
                TextView textViewPostDate = new TextView(getApplicationContext());//novi textview
                textViewPostDate.setId(LastPostNumber * 2 + 2);//postavi id od textviewa
                RelativeLayout.LayoutParams layoutParamsDate = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//parametri za textview
                layoutParamsDate.addRule(RelativeLayout.BELOW, LastPostNumber * 2 + 1);//Dodati pravila kako ce biti poslozen; Postaviti ga ispod Teksta posta.
                textViewPostDate.setLayoutParams(layoutParamsDate);
                textViewPostDate.setText("Posted on: " + CurrentPost.getPostDate());
                textViewPostDate.setTextSize(13);
                textViewPostDate.setTextColor(Color.parseColor("#FFFFFF"));
                relativeLayoutContent.addView(textViewPostDate);

                LastPostNumber++;
            }
            Posts.clear();
        }
    }
    //Funkcija koja provjerava da post nije prazan tako sto makne sve razmake i provjeri duljinu stringa.
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
        protected void onPreExecute()
        {
            //Pokazi loading progress bar
            super.onPreExecute();
            progDialog = new ProgressDialog(PersonProfileActivity.this);
            progDialog.setMessage("Loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            /*
            U params[0] se pohranjuje string o kojem ovisi sto ce se izvrsit.
            Prihvaca sljedece strinogve:

            GET -> Dobiva postove korisnika
            ISFOLLOW -> Provjerava dali trenutno ulogiran korisnik prati profil korisnika
            FOLLOW -> Prati korisnika
            UNFOLLOW -> Prestani pratitit korisnika
             */
            if(params[0].equals("GET")) {//Ovisno o prvom parametru dohvaca postove ili provjerava dali user vec prati ovog korisnika
                //Pohrani podatke u arraylist koje ce proslijedit u PHP za dohvacanje iz baze
                ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
                podatci.add(new BasicNameValuePair("email", params[1]));
                podatci.add(new BasicNameValuePair("lastpost", params[2]));
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://socializeme.site90.com/GetPostsFromUser.php");//php koji ce executeat nasu skriptu
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
                            String CurrentPostDate = reader.readLine();
                            Post CurrentPost = new Post(PersonName, PersonEmail, line, CurrentPostDate);
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

                    return "Success";
                } catch (Exception e) {

                }
            }

            else if(params[0].equals("ISFOLLOW"))//Provjerava dali vec pratimo korisnika ili ne
            {
                ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
                podatci.add(new BasicNameValuePair("loggedEmail", params[1]));//email od trenutno ulogirane osobe
                podatci.add(new BasicNameValuePair("profileEmail", params[2]));//email od profila na cijem smo
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://socializeme.site90.com/CheckForFollow.php");//php koji ce executeat nasu skriptu
                    httpPost.setEntity(new UrlEncodedFormEntity(podatci, HTTP.UTF_8));//Proslijedi podatke php-u
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    //Dohvati response da dobijemo source
                    HttpEntity entity = httpResponse.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String Result = reader.readLine();
                    return Result;
                } catch (Exception e) {

                }
            }
            else if(params[0].equals("FOLLOW"))
            {
                ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
                podatci.add(new BasicNameValuePair("loggedEmail", params[1]));//email od trenutno ulogirane osobe
                podatci.add(new BasicNameValuePair("profileEmail", params[2]));//email od profila na cijem smo
                try
                {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://socializeme.site90.com/followUser.php");//php koji ce executeat nasu skriptu
                    httpPost.setEntity(new UrlEncodedFormEntity(podatci, HTTP.UTF_8));//Proslijedi podatke php-u
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    return "2";
                }
                catch (Exception e)
                {

                }
            }
            else if(params[0].equals("UNFOLLOW"))
            {
                ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
                podatci.add(new BasicNameValuePair("loggedEmail", params[1]));//email od trenutno ulogirane osobe
                podatci.add(new BasicNameValuePair("profileEmail", params[2]));//email od profila na cijem smo
                try
                {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://socializeme.site90.com/unfollowUser.php");//php koji ce executeat nasu skriptu
                    httpPost.setEntity(new UrlEncodedFormEntity(podatci, HTTP.UTF_8));//Proslijedi podatke php-u
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    return "3";
                }
                catch (Exception e)
                {

                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /*
            String s vraca vrijednost nakon sto je thread gotov. Moze vratiti sljedece stringove:
            1 -> Provjerio je dali pratimo korisnika i pratimo ga
            2 -> Pritisnuli smo gumb da pratimo korisnika i pratimo ga
            3 -> Pritisnili smo gumb da prestanemo pratit korisnika i vise ga ne pratimo

            Ako ne vrati niti jedan od gore navedenih stringova to znaci da je thread samo dohvatio postove od usera te se trebaju napraviti textViewi.
             */
            //ako je result 1 znaci da ulogirani korisnik vec prati trenutnog korisnika
            if(s.charAt(0) == '1')
            {
                IsFollowing = true;
                buttonFollow.setText("Unfollow user");
            }
            else if(s.charAt(0) == '2')
            {
                IsFollowing = true;
                buttonFollow.setText("Unfollow user");
            }
            else if(s.charAt(0) == '3')
            {
                IsFollowing = false;
                buttonFollow.setText("Follow user");
            }
            else
            {
                PostsToViews();
            }
            progDialog.dismiss();
            isLoadingPosts=false;


        }
    }
}
