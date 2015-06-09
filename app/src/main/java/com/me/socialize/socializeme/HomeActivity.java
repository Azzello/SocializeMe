package com.me.socialize.socializeme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FindViews();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    void FindViews()
    {
        //dohvati email iz proslog activity-a
        userEmail = getIntent().getExtras().getString("EMAIL");
        //sakrij action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //pronadi view-ove
        editTextPost = (EditText)findViewById(R.id.editTextPost);
        buttonPost = (Button)findViewById(R.id.buttonPost);
        editTextSearchPerson = (EditText)findViewById(R.id.editTextSearchPerson);
        buttonSearchPerson = (Button)findViewById(R.id.buttonSearchPerson);
        //Postavi event listenere
        SetEvents();
    }
    void SetEvents()
    {
        buttonPost.setOnClickListener(this);
        buttonSearchPerson.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Post button je kliknut
        if(v == buttonPost)
        {
            //Provjeri da post nije prazan
            if(editTextPost.getText().length() > 0) {

                new Post().execute();
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
                startActivity(SearchPersonIntent);
            }
            else
            {
                Toast.makeText(this,"Search field can't be empty!",Toast.LENGTH_SHORT).show();
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
    class Post extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params) {
            if (params[0].equals("POST")) {//Provjeri parametar dali treba postat ili dohvatiti postove
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
                    StringBuilder stringBuilder = new StringBuilder();
                    String result = reader.readLine();
                    inputStream.close();
                    return result;//Vrati source od stranice koji ce nam reci dali je post uspio
                } catch (Exception e) {
                    Log.d("SocializeMe", e.toString());
                }
                return "Login failed!";
            }
            else if(params[0].equals("GET"))
            {

            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("Post Created!\t"))//Provjerava dali je post uspjesno napravljen
            {
                Toast.makeText(getApplicationContext(),"Post created!",Toast.LENGTH_SHORT).show();
                RestartActivity();//reloada activity kako bi ucitao ponovo sve postove
            }
        }
    }
}
