package com.me.socialize.socializeme;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MakeAccountActivity extends ActionBarActivity implements View.OnClickListener{
    //Definiraj view-e
    Spinner spinnerCountries;
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextPassword;
    EditText editTextConfirmPassword;
    EditText editTextEmail;
    Button buttonSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_account);
        //Pronadi View-e
        FindViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_account, menu);
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
        //Sakrij action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //Pronadi view-e
        spinnerCountries = (Spinner)findViewById(R.id.spinnerCountries);
        editTextFirstName = (EditText)findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText)findViewById(R.id.editTextLastName);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        buttonSubmit = (Button)findViewById(R.id.buttonSubmit);

        String[] Countries = getResources().getStringArray(R.array.countriesArray);//Dohvati sve zemlje is resource xml-a
        ArrayList<String> arrayListCountries = new ArrayList<String>(Arrays.asList(Countries)); //Spremi zemlje u ArrayListu
        Collections.sort(arrayListCountries);//Poredaj arraylistu abecedno

        ArrayAdapter<String> arrayAdapterCountries = new ArrayAdapter<String>(this,R.layout.layout_spinner_row,arrayListCountries);//Novi adapter za spinner koji ima poredanu arraylistu
        arrayAdapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//Psotavlja dropdown za spinner
        spinnerCountries.setAdapter(arrayAdapterCountries);//Postavlja adapter za spinner
        //Postavi event listenere
        SetEvents();
    }
    //Postavi event listenere
    void SetEvents()
    {
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Submit kliknut
        if(v == buttonSubmit)
        {
            if(editTextFirstName.getText().length()>0 && editTextLastName.getText().length()>0 && editTextEmail.getText().length() > 0 && editTextPassword.getText().length() > 0 && editTextConfirmPassword.getText().length() > 0)
            {
                //Provjeri dali je email ispravan
                if(!editTextEmail.getText().toString().contains("@"))
                {
                    Toast.makeText(this,"Please enter valid email",Toast.LENGTH_SHORT).show();
                    return;
                }
                //Provjeri dali su lozinke iste
                if(!editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString()))
                {
                    Toast.makeText(this,"Passwords do not match!",Toast.LENGTH_SHORT).show();
                    return;
                }
                //Sve je ispravno provjeri u bazi
                Toast.makeText(this,"Account Created",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"All fields must be filled!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
