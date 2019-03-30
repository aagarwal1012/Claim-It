package com.iitr.mdg.claimit;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog progressDialog;

    EditText username;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String username = this.username.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
//                        // onLoginFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);

        GraphQLClient.getApolloClient().query(FetchQuery.builder().build()).enqueue(new ApolloCall.Callback<FetchQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<FetchQuery.Data> response) {
                Log.e("Response from GraphQL", "onResponse: " + response.toString());
                //Log.e("sixe: ", Integer.toString(response.data().hint_user_info.get(0).));
                int x =0;

                for (int i=0; i<response.data().hint_user_info.size(); i++){
                    if(username.equals(response.data().hint_user_info.get(i).username)){
                        if (password.equals(response.data().hint_user_info.get(i).password)){
                            x=1;
                            break;
                        }
                    }
                }
                if(x==1){
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onLoginSuccess();
                        }
                    });
                }
                else if(x==0) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onLoginFailed();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        progressDialog.dismiss();
        _loginButton.setEnabled(true);
       // finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String uname = username.getText().toString();
        String password = _passwordText.getText().toString();

        if (uname.isEmpty()) {
            username.setError("enter a valid email address");
            valid = false;
        } else {
            username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 15) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}