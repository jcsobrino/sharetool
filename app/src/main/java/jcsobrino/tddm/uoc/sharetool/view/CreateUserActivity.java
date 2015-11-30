package jcsobrino.tddm.uoc.sharetool.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.ApiFactory;
import jcsobrino.tddm.uoc.sharetool.common.UtilFunctions;
import jcsobrino.tddm.uoc.sharetool.service.ApiService;

public class CreateUserActivity extends AppCompatActivity {

    private ApiService mAPI = ApiFactory.INSTANCE.getApi();
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mRepeatPassword;
    private Button mCreateUserButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mUsername = (EditText) findViewById(R.id.userNameEditText);
        mEmail = (EditText) findViewById(R.id.emailEditText);
        mPassword = (EditText) findViewById(R.id.passwordEditText);
        mRepeatPassword = (EditText) findViewById(R.id.repeatPasswodEditText);
        mCreateUserButton = (Button) findViewById(R.id.createUserButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mCreateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {

                    String username = mUsername.getText().toString();
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    try {
                        mAPI.createUser(username, email, password);
                        Toast.makeText(getApplicationContext(), R.string.user_created, Toast.LENGTH_LONG).show();
                        finish();
                    } catch (RuntimeException re) {
                        Toast.makeText(getApplicationContext(), R.string.error_creating_user, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean validateFields() {

        boolean result = true;

        if (UtilFunctions.isEmpty(mRepeatPassword.getText())) {

            mRepeatPassword.setError(getString(R.string.error_field_required));
            mRepeatPassword.requestFocus();
            result = false;
        }

        if (UtilFunctions.isEmpty(mPassword.getText())) {

            mPassword.setError(getString(R.string.error_field_required));
            mPassword.requestFocus();
            result = false;
        }

        if (!UtilFunctions.isEmpty(mPassword.getText()) && !UtilFunctions.isEmpty(mRepeatPassword.getText()) && !mPassword.getText().toString().equals(mRepeatPassword.getText().toString())) {

            mRepeatPassword.setError(getString(R.string.password_mismatch));
            mRepeatPassword.requestFocus();
            result = false;
        }

        if (UtilFunctions.isEmpty(mEmail.getText())) {

            mEmail.setError(getString(R.string.error_field_required));
            mEmail.requestFocus();
            result = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches()) {

            mEmail.setError(getString(R.string.format_error));
            mEmail.requestFocus();
            result = false;
        } else if (mAPI.userExistsByEmail(mEmail.getText().toString())) {

            mEmail.setError(getString(R.string.repeat_email));
            mEmail.requestFocus();
            result = false;
        }

        if (UtilFunctions.isEmpty(mUsername.getText())) {

            mUsername.setError(getString(R.string.error_field_required));
            mUsername.requestFocus();
            result = false;
        }

        return result;
    }
}
