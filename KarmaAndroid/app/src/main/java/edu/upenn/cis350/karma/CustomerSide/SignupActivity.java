package edu.upenn.cis350.karma.CustomerSide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
 * Sign up page for new customers (not vendors -- they have to sign up on the website)
 */
public class SignupActivity extends FragmentActivity {

    private final String TAG = "SignupActivity";

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private Button registerBtn;
    private TextView tvCustomerLogin;
    private TextView tvVendorLogin;

    private DataProcessor dp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        tvCustomerLogin = (TextView) findViewById(R.id.tvCustomerLogin);
        tvVendorLogin = (TextView) findViewById(R.id.tvVendorLogin);

        dp = new DataProcessor();

    }

    private void createNewUser(String firstName, String lastName, String email, String password) {
        final Context ctx = this;
        dp.signUpUser(firstName, lastName, email, password, ctx.getApplicationContext(),
                new FetchDataListener() {
            @Override
            public void onFetchComplete(JSONObject data) {
                if (data == null) {
                    Log.d(TAG, "got back null data");
                    displayLoginError("Error creating account");
                } else {
                    Log.d(TAG, "successfully logged in user");
                    Intent i = new Intent(ctx, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                Log.d(TAG, msg);
                displayLoginError("Failed to create account");
            }

            @Override
            public void onFetchStart() {
                // starting adding user to database
            }
        });
    }

    public void onRegisterBtnClicked(View view) {
        if (view == registerBtn) {
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (validateFields(firstName, lastName, email, password)) {
                createNewUser(firstName, lastName, email, password);
            }
        }
    }

    private boolean validateFields(String firstName, String lastName, String email,
                                   String password) {
        boolean allValid = true;
        if (firstName.equals("")) {
            etFirstName.setError("First name cannot be empty");
            allValid = false;
        }

        if (lastName.equals("")) {
            etLastName.setError("Last name cannot be empty");
            allValid = false;
        }

        if (email.equals("")) {
            etEmail.setError("Email cannot be empty");
            allValid = false;
        }

        if (password.equals("")) {
            etPassword.setError("Password cannot be empty");
            allValid = false;
        }

        return allValid;
    }

    /**
     * Shows an error pop up
     *
     * @param error -- error message for the user
     */
    protected void displayLoginError(String error) {
        DialogFragment errorMessage = new ErrorDialogFragment(error);
        errorMessage.show(getSupportFragmentManager(), "loginError");
    }

    public void onCustomerLoginClicked(View view) {
        if (view == tvCustomerLogin) {
            Intent goToCustomerLogin = new Intent(this, CustomerLoginActivity.class);
            startActivity(goToCustomerLogin);
        }
    }

    public void onVendorLoginTextClicked(View view) {
        if (view == tvVendorLogin) {
            // go to vendor login page
            Intent goToVendorLogin = new Intent(this, VendorLoginActivity.class);
            startActivity(goToVendorLogin);
        }
    }
}
