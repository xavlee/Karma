package edu.upenn.cis350.karma.VendorSide;

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

import edu.upenn.cis350.karma.CustomerSide.CustomerLoginActivity;
import edu.upenn.cis350.karma.ErrorDialogFragment;
import edu.upenn.cis350.karma.R;
import edu.upenn.cis350.karma.WebConnection.DataProcessor;
import edu.upenn.cis350.karma.WebConnection.FetchDataListener;

/**
 * Login page for vendors
 */
public class VendorLoginActivity extends FragmentActivity {
    private static final String TAG = "Vendor Login Activity";
    EditText etEmail;
    EditText etPassword;
    Button loginBtn;
    TextView tvCustomerLogin;
    DataProcessor dp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        tvCustomerLogin = (TextView) findViewById(R.id.tvCustomerLogin);

        dp = new DataProcessor();
    }

    public void onLoginBtnClicked(View view) {
        if (view == loginBtn) {
            final String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            final Context ctx = this.getApplicationContext();

            if (validateFields(email, password)) {
                dp.checkVendorLoginCredentials(email, password, ctx, new FetchDataListener() {
                    @Override
                    public void onFetchComplete(JSONObject data) {
                        Boolean resp = (Boolean) data.get("response");
                        if (resp) {
                            // go to vendor home page
                            Intent goToHome = new Intent(ctx, VendorHomeActivity.class);
                            goToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            goToHome.putExtra("email", email);
                            startActivity(goToHome);
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

                    }
                });
            }
        }
    }

    public void onNavigateCustomerLoginClicked(View view) {
        if (view == tvCustomerLogin) {
            Intent goToCustomerLogin = new Intent(this, CustomerLoginActivity.class);
            startActivity(goToCustomerLogin);
        }
    }

    private boolean validateFields(String email,
                                   String password) {
        boolean allValid = true;
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

    protected void displayLoginError(String error) {
        DialogFragment errorMessage = new ErrorDialogFragment(getResources()
                .getString(R.string.login_error_message));
        errorMessage.show(getSupportFragmentManager(), "loginError");
    }
}
