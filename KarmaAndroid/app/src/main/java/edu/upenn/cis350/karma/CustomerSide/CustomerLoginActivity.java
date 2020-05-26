package edu.upenn.cis350.karma.CustomerSide;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import org.json.simple.JSONObject;

import edu.upenn.cis350.karma.ErrorDialogFragment;
import edu.upenn.cis350.karma.MainActivity;
import edu.upenn.cis350.karma.R;
import edu.upenn.cis350.karma.VendorSide.VendorLoginActivity;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

/**
 * Login page for customers
 */
public class CustomerLoginActivity extends FragmentActivity {

    private final String TAG = "CustomerLoginActivity";
    private EditText etEmail;
    private EditText etPassword;
    private Button loginBtn;
    private TextView tvRegister;
    private TextView tvVendorLogin;
    private DataProcessor processor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvVendorLogin = (TextView) findViewById(R.id.tvVendorLogin);

        processor = new DataProcessor();
    }

    public void onRegisterTextClicked(View view) {
        if (view == tvRegister) {
            Intent signUp = new Intent(this, SignupActivity.class);
            startActivity(signUp);
        }
    }

    public void onLoginBtnClicked(View view) {
        if (view == loginBtn) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            
            if (email.equals("") && password.equals("")) {
                etPassword.setError(getResources().getString(R.string.pass_empty_error));
                etEmail.setError(getResources().getString(R.string.email_empty_error));

            } else if (email.equals("")) {
                etEmail.setError(getResources().getString(R.string.email_empty_error));

            } else if (password.equals("")) {
                etPassword.setError(getResources().getString(R.string.pass_empty_error));

            } else {
                Log.d(TAG, "starting user credentials lookup");
                checkLogin(email, password);
            }
        }
    }

    /**
     * sends validation request to the server
     *
     * @param email -- user inputted email
     * @param password -- user inputted password
     */
    private void checkLogin(String email, String password) {
        final Context ctx = getApplicationContext();

        processor.checkLoginCredentials(email, password, ctx, new FetchDataListener() {
            @Override
            public void onFetchComplete(JSONObject data) {
                Boolean resp = (Boolean) data.get("response");
                if (resp) {
                    // go to home page
                    Intent goToHome = new Intent(ctx, MainActivity.class);
                    goToHome.putExtra("user", etEmail.getText().toString());
                    goToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goToHome);

                    // finish prevents people from going back to login without logging out
                    finish();
                } else {
                    displayLoginError("Couldn't log in");
                    etEmail.setText("");
                    etPassword.setText("");
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                Log.d(TAG, msg);
                displayLoginError("Error connecting to server");
            }

            @Override
            public void onFetchStart() {
                // do nothing
            }
        });

    }

    /**
     * Shows an error pop up
     *
     * @param error -- error message for the user
     */
    protected void displayLoginError(String error) {
        DialogFragment errorMessage = new ErrorDialogFragment(getResources()
                .getString(R.string.login_error_message));
        errorMessage.show(getSupportFragmentManager(), "loginError");
    }

    public void onVendorLoginTextClicked(View view) {
        if (view == tvVendorLogin) {
            // go to vendor login page
            Intent goToVendorLogin = new Intent(this, VendorLoginActivity.class);
            startActivity(goToVendorLogin);
        }
    }
}
