package com.example.password_generator_project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.password_generator_project.adapter.RecyclerViewAdapter;
import com.example.password_generator_project.data.MyDBHandler;
import com.example.password_generator_project.model.pass_db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout mainLayout;
    SeekBar seekBar;
    TextView passLength; //Variable password length display
    int user_defined_pass_length=6; //Minimum password length is set to 6
    String websitename=""; //Local String for input from Layout
    EditText user_website_name; //EditText from layout to take input
    Button gen_button; //Button to generate password

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ArrayList<pass_db> passArraylist;
    private ArrayAdapter<String> arrayAdapter;
    TextView gen_password; //To Display Generated Password
    private BiometricPrompt.PromptInfo promptInfo;
    private void auth(){ //Biometric Authentication
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL | BiometricManager.Authenticators.BIOMETRIC_WEAK)){
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
        }
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt BiometricPrompt = new BiometricPrompt(MainActivity.this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                mainLayout.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "User Authenticated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("UrPassword Manager")
                .setDescription("Use Fingerprint to Login").setDeviceCredentialAllowed(true).build();
        BiometricPrompt.authenticate(promptInfo);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mainLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
       auth();
    }
    public void previous_data_display(){
        passArraylist = new ArrayList<>();
        MyDBHandler db = new MyDBHandler(MainActivity.this);
        //Record Accessing and display
        List<pass_db> passlist = db.getAllPasswords();
        for(pass_db pass : passlist){
            passArraylist.add(pass);
        }
        //Using recyclerview
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this,passArraylist);
        recyclerView.setAdapter(recyclerViewAdapter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        //Authentication
        auth();
        //Recycler view initializtion
        recyclerView = findViewById(R.id.recyclerid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //DB initialization
        MyDBHandler db = new MyDBHandler(MainActivity.this);
        previous_data_display();

        //Main Password Generation Code
        gen_button = (Button) findViewById(R.id.pass_gen_button);
        gen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To Hide Keyboard after pressing generate button
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                user_website_name = (EditText) findViewById(R.id.websitename);
                websitename = String.valueOf(user_website_name.getText()); //Gets the name of website for which the password is for
                if (websitename.equals(""))
                    Toast.makeText(view.getContext(), "Select length and enter website name", Toast.LENGTH_SHORT).show();
                else { //Executes if the website name field is not empty
                    String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                            + "0123456789"
                            + "abcdefghijklmnopqrstuvxyz"
                            + "!@#$%^&*";
                    //Generates a password string of desired length entered by the user
                    StringBuffer sb = new StringBuffer(user_defined_pass_length);
                    for (int i = 0; i < user_defined_pass_length; i++) {
                        int index = (int) (AlphaNumericString.length() * Math.random());
                        sb.append(AlphaNumericString.charAt(index));
                    }
                    Toast.makeText(view.getContext(), "Password Generated ", Toast.LENGTH_SHORT).show();
                    gen_password = (TextView)findViewById(R.id.generated_password);
                    gen_password.setText("Generated Password : "+sb);
                    //Copies the new generated password to clipboard to use it
                    ClipboardManager clipboard =(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(null,sb);
                    clipboard.setPrimaryClip(clip);
                    //Adding password to database
                    pass_db record = new pass_db(sb.toString(),websitename);
                    db.addpass(record);
                    previous_data_display();
                }
            }
        });
        //range slider to take input for desired length of the string from the user
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        passLength = (TextView) findViewById(R.id.passLength);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                user_defined_pass_length = i;
                passLength.setText(String.valueOf("Password Length : " + i + "\n(Ideally set 8 characters or more) "));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

}