package com.me.socialize.socializeme;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
            Toast.makeText(this,"Login",Toast.LENGTH_SHORT).show();
        }
    }
}
