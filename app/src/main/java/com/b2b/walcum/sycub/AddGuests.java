package com.b2b.walcum.sycub;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.b2b.walcum.sycub.model.GuestWaiting;
import com.b2b.walcum.sycub.model.Guests;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;



public class AddGuests extends ActionBarActivity {

    private static final String WAITING = "Waiting";

    EditText txtName,txtMobileNumber, txtNotes,txtNumOfGuests;
    TextView txtFrequency;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guests);

        //Storing all edit text values in variables
        txtName=(EditText)findViewById(R.id.editTextGuestName);
        txtMobileNumber=(EditText)findViewById(R.id.editTextMobileNumber);
        txtNumOfGuests=(EditText)findViewById(R.id.editTextNumberOfGuests);
        txtNotes=(EditText)findViewById(R.id.editTextNotes);

        txtFrequency = (TextView) findViewById(R.id.textViewFrequency);

        // Open the default realm ones for the UI thread.
        realm = Realm.getInstance(getApplicationContext());

        //Add text change listener in Mobile Number to fetch data from Realm
        addListenerOnTextChange();

    }

    public void addListenerOnTextChange() {

        //Add text change listener in Mobile Number to fetch data from Realm
        txtMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                //Clear the texts in the auto populated fields for a new onTextChange
                clearAllTextArea();

                if (s.length() >= 10) {

                    //Searching for mobile number in guests model that is in GuestWaiting
                    if (realm.where(GuestWaiting.class).findAll().size() > 0) {

                        GuestWaiting guestWaiting = realm.where(GuestWaiting.class)
                                .equalTo("guests.mobileNumber", s.toString())
                                .findFirst();

                        //Populating the other edit fields i.e. name and notes
                        if (guestWaiting != null) {

                            //Setting values to the text fields
                            txtName.setText(guestWaiting.getName());
                            if (guestWaiting.getNotes() != null)
                                txtNotes.setText(guestWaiting.getNotes());

                            SimpleDateFormat sdf = new SimpleDateFormat("E dd-MMM-yyyy", Locale.getDefault());
                            String firstVisit = sdf.format(guestWaiting.getGuests().getTimestamp());

                            // Searching for the count of the rows of GuestsWaiting for a specific guest
                            int frequency = realm.where(GuestWaiting.class)
                                    .equalTo("mobileNumber", guestWaiting.getMobileNumber()).findAll()
                                    .size();
                            txtFrequency.setText(" Visited " +frequency+ " times "+" since  " +firstVisit);
                        }else
                            txtFrequency.setText("***** YAY! NEW GUEST *****"); //Harcoded
                    }else
                        txtFrequency.setText("** WELCOME!! ADD YOUR FIRST GUEST **"); //Hardcoded

                }
            }
        });

    }

    public void insertDataInRealmModels() {

        realm.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                //For setting timestamp
                Date now = new Date(Calendar.getInstance().getTime().getTime());

                //Setting guest's details in GuestWaiting Model
                GuestWaiting gw = realm.createObject(GuestWaiting.class);

                //Setting data in GuestWaiting model
                gw.setId(UUID.randomUUID().toString());
                gw.setName(txtName.getText().toString());
                gw.setMobileNumber(txtMobileNumber.getText().toString());
                gw.setNumOfGuests(Integer.valueOf(txtNumOfGuests.getText().toString()));
                if (txtNotes.getText() != null)
                    gw.setNotes(txtNotes.getText().toString());
                gw.setETA(0);
                gw.setWaitTime(10);
                gw.setStatus(WAITING);
                gw.setTimestamp(now);

                // Checking if the entry for this guest in WaitingGuest model exists in Guests Model
                Guests guests = realm.where(Guests.class).equalTo("mobileNumber", gw.getMobileNumber())
                        .findFirst();

                // Setting guest's details in Guests Model
                if (guests == null) {

                    //Setting guest's details in GuestWaiting Model
                    guests = realm.createObject(Guests.class);

                    //Insert guest into Guests Model
                    guests.setGuestID(UUID.randomUUID().toString());
                    guests.setGuestName(gw.getName());
                    guests.setMobileNumber(gw.getMobileNumber());
                    guests.setDateOfBirth("");
                    guests.setDateOfAnniversary("");
                    guests.setRating("");
                    guests.setTimestamp(now);
                }

                gw.setGuests(guests);
            }
        });

        Log.v("TAG", "Records in GuestWaiting Model --" + realm.where(GuestWaiting.class).findAll());
        Log.v("TAG", "Records in Guests Model --" + realm.where(Guests.class).findAll());
    }

    private void clearAllTextArea() {

        txtName.setText("");
        txtNumOfGuests.setText("");
        txtNotes.setText("");
        txtFrequency.setText("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_guests, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_nav_to_main_activity:

                //Insert guest's details in GuestWaiting and Guests Model
                insertDataInRealmModels();

                Intent theIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(theIntent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }
}
