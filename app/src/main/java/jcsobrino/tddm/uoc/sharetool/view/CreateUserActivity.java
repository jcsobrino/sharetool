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
                        Toast.makeText(getApplicationContext(), "El usuario ha sido registrado", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (RuntimeException re) {
                        Toast.makeText(getApplicationContext(), "Se ha producido un error durante la creación del usuario", Toast.LENGTH_LONG).show();
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

        if (TextUtils.isEmpty(mUsername.getText())) {

            mUsername.setError("Campo obligatorio");
            result = false;
        }

        if (TextUtils.isEmpty(mEmail.getText())) {
            mEmail.setError("Campo obligatorio");
            result = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches()) {
            mEmail.setError("El e-mail es incorrecto");
            result = false;
        } else if (mAPI.userExistsByEmail(mEmail.getText().toString())) {

            mEmail.setError("E-mail ya existente");
            result = false;
        }

        if (TextUtils.isEmpty(mPassword.getText())) {

            mPassword.setError("Campo obligatorio");
            result = false;
        }

        if (TextUtils.isEmpty(mRepeatPassword.getText())) {

            mRepeatPassword.setError("Campo obligatorio");
            result = false;
        }

        if(!TextUtils.isEmpty(mPassword.getText()) && !TextUtils.isEmpty(mRepeatPassword.getText()) && !mPassword.getText().toString().equals(mRepeatPassword.getText().toString())){

            mRepeatPassword.setError("Los passwords son diferentes");
            result = false;

        }

        return result;
    }
}
