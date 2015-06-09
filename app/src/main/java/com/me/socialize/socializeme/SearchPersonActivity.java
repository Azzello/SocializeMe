package com.me.socialize.socializeme;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
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


public class SearchPersonActivity extends ActionBarActivity {
    String Firstname;
    String Lastname;
    ArrayList<Person> Persons;

    ListView listViewSearchResults;
    SearchArrayAdapter searchArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_person);
        FindViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_person, menu);
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

    public void FindViews()
    {
        //sakrij action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //dohvati ime i prezime iz proslog activity-a
        Intent intent = getIntent();
        Firstname = intent.getExtras().getString("FIRSTNAME","");
        Lastname = intent.getExtras().getString("LASTNAME","");
        Persons = new ArrayList<Person>();

        listViewSearchResults = (ListView)findViewById(R.id.listViewSearchPerson);

        new SearchForPerson().execute(Firstname,Lastname);
    }
    class SearchForPerson extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            //Pohrani podatke u arraylist koje ce proslijedit u PHP za ubacivanje u bazu
            ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
            podatci.add(new BasicNameValuePair("firstname",params[0]));
            podatci.add(new BasicNameValuePair("lastname",params[1]));
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://socializeme.site90.com/SearchPeople.php");//php koji ce executeat nasu skriptu
                httpPost.setEntity(new UrlEncodedFormEntity(podatci, HTTP.UTF_8));//Proslijedi podatke php-u
                HttpResponse httpResponse = httpClient.execute(httpPost);
                //Dohvati response da dobijemo source
                HttpEntity entity = httpResponse.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) // Read line by line
                {
                    Log.d("SocializeMe",line);
                    if(line.equals("<!-- Hosting24 Analytics Code -->"))
                        break;
                    line = line.replace("<br>","");
                    Persons.add(Person.ParsePerson(line));
                }
                inputStream.close();
                return null;
            }
            catch(Exception e)
            {
                Log.d("SocializeMe", e.toString());
            }

            return "Failed to make an account";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("SocializeMe", Persons.size() + "");
            searchArrayAdapter = new SearchArrayAdapter(SearchPersonActivity.this,R.layout.layout_listview_row,Persons);
            listViewSearchResults.setAdapter(searchArrayAdapter);
        }
    }

}
