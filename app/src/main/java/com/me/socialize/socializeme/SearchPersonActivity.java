package com.me.socialize.socializeme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
    String userEmail;
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
        userEmail = intent.getExtras().getString("USEREMAIL","");
        Persons = new ArrayList<Person>();

        listViewSearchResults = (ListView)findViewById(R.id.listViewSearchPerson);

        new SearchForPerson().execute(Firstname,Lastname);//Pokreni asynctask za trazenje u bazi
        SetEventListeners();
    }

    void SetEventListeners()
    {
        //Listview item click listener
        listViewSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //kliknut je item i pokreni activity za profil od kliknute osobe
                String PersonEmail = Persons.get(position).m_Email.substring(0, Persons.get(position).m_Email.length() - 1);
                Intent personProfileActivity = new Intent(getApplicationContext(), PersonProfileActivity.class);
                personProfileActivity.putExtra("FULLNAME",Persons.get(position).GetFullname());
                personProfileActivity.putExtra("EMAIL",PersonEmail);
                personProfileActivity.putExtra("USEREMAIL",userEmail);
                startActivity(personProfileActivity);
            }
        });
    }

    class SearchForPerson extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            //Pohrani podatke u arraylist koje ce proslijedit u PHP za dohvacanje iz baze
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
                while ((line = reader.readLine()) != null) // citaj red po red
                {

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

            }

            return "Failed to make an account";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            searchArrayAdapter = new SearchArrayAdapter(SearchPersonActivity.this,R.layout.layout_listview_row,Persons);
            listViewSearchResults.setAdapter(searchArrayAdapter);
        }
    }

}
