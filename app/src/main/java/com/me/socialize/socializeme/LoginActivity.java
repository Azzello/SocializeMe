package com.me.socialize.socializeme;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {
    //definiranje vieweva
    TextView textViewForgotPassword;
    TextView textViewNeedAccount;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Pronalazi sve view-e
        FindViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    //Pronalazi View-e
    void FindViews()
    {
        //Sakriva action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textViewForgotPassword = (TextView)findViewById(R.id.textViewForgotPassword);
        textViewNeedAccount = (TextView)findViewById(R.id.textViewNeedAccount);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);

        SetEventListeners();
    }
    //Postavlja event listenere na gumbove/textview-ove
    void SetEventListeners()
    {
        textViewForgotPassword.setOnClickListener(this);
        textViewNeedAccount.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Kliknut "Forgot Password"
        if(v == textViewForgotPassword)
        {
            Toast.makeText(this,"Password",Toast.LENGTH_SHORT).show();
        }
        //Kliknut "Need an account"
        if(v == textViewNeedAccount)
        {
            //Pokreni NeedAccount activity
            Intent intentNeedAccount = new Intent(this,MakeAccountActivity.class);
            startActivity(intentNeedAccount);
        }
        //Kliknut Login button
        if(v == buttonLogin)
        {
            if(editTextEmail.getText().length() > 0 && editTextPassword.getText().length()>0)
            {
                if(editTextEmail.getText().toString().contains("@"))
                {
                    new LoginIntoDatabase().execute();
                }
                else
                {
                    Toast.makeText(this,"Email is not valid!",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this,"All fields must be filled!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    class LoginIntoDatabase extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params) {
            //Pohrani podatke u arraylist koje ce proslijedit u PHP za ubacivanje u bazu
            ArrayList<NameValuePair> podatci = new ArrayList<NameValuePair>();
            podatci.add(new BasicNameValuePair("email",editTextEmail.getText().toString()));
            podatci.add(new BasicNameValuePair("password",editTextPassword.getText().toString()));
            //odlazi na stranicu koja se pokusava ulogirat
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://socializeme.site90.com/LoginInto.php");//php koji ce executeat nasu skriptu
                httpPost.setEntity(new UrlEncodedFormEntity(podatci, HTTP.UTF_8));//Proslijedi podatke php-u
                HttpResponse httpResponse = httpClient.execute(httpPost);
                //Dohvati response da dobijemo source
                HttpEntity entity = httpResponse.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String result=reader.readLine();
                inputStream.close();
                return result;//Vrati source od stranice koji ce nam reci dali je registracija uspijela
            }
            catch(Exception e)
            {
                Log.d("SocializeMe", e.toString());
            }
            return "Login failed!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            if(s.equals("Login successful!"))
            {
                Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
