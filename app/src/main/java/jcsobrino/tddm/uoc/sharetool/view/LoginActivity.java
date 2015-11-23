package jcsobrino.tddm.uoc.sharetool.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jcsobrino.tddm.uoc.sharetool.R;
import jcsobrino.tddm.uoc.sharetool.common.ApiFactory;
import jcsobrino.tddm.uoc.sharetool.dto.ITool;
import jcsobrino.tddm.uoc.sharetool.dto.IUser;
import jcsobrino.tddm.uoc.sharetool.service.ApiService;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGGED_USER = "LOGGED_USER";
    private ApiService mAPI = ApiFactory.INSTANCE.getApi();
    private EditText mEmail;
    private EditText mPassword;
    private TextView mCreateUser;
    private Button mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //deleteDatabase("ShareTool.db");

        mEmail = (EditText) findViewById(R.id.emailLoginEditText);
        mPassword = (EditText) findViewById(R.id.passwordLoginEditText);
        mCreateUser = (TextView) findViewById(R.id.createUserLoginTextView);
        mLoginButton = (Button) findViewById(R.id.startSessionLoginButton);


        mCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, CreateUserActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {

                    new LoginAsyncTask().execute(mEmail.getText().toString(),mPassword.getText().toString());
                }
            }
        });

    }

    private boolean validateFields() {

        boolean result = true;

        if (TextUtils.isEmpty(mPassword.getText())) {

            mPassword.setError(getString(R.string.error_field_required));
            mPassword.requestFocus();
            result = false;
        }

        if (TextUtils.isEmpty(mEmail.getText())) {

            mEmail.setError(getString(R.string.error_field_required));
            mEmail.requestFocus();
            result = false;
        }

        return result;
    }


    private class LoginAsyncTask extends AsyncTask<String, Void, IUser> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LoginActivity.this, "Cargando", "Espere mientras se ejecuta la acción..", true, true);
        }

        @Override
        protected IUser doInBackground(String... params) {

            if (params != null && params.length == 2) {
                return mAPI.login(params[0], params[1]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(IUser result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result == null) {
                Toast.makeText(getApplicationContext(), "El usuario o el password no son correctos", Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                intent.putExtra(LoginActivity.LOGGED_USER, result);
                startActivity(intent);
            }

        }
    }
}
