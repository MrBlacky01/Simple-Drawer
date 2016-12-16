package com.blacky.mr.simpledrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    Button enterButton;
    Button changeUserButton;
    Button logoutButton;
    Toolbar toolbar;
    TextView profileTextView;
    LinearLayout actionLayout;

    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);
        enterButton = (Button) findViewById(R.id.enter_button);
        changeUserButton = (Button) findViewById(R.id.change_user);
        logoutButton = (Button) findViewById(R.id.logout_button);
        profileTextView = (TextView) findViewById(R.id.profile_info);
        actionLayout = (LinearLayout) findViewById(R.id.action_linear_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
            new FacebookCallback<LoginResult>() {
                private ProfileTracker mProfileTracker;
                @Override
                public void onSuccess(LoginResult loginResult) {

                    if(Profile.getCurrentProfile() == null) {
                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                profileTextView.setText(getString(R.string.hello_string) +profile2.getName());
                                mProfileTracker.stopTracking();
                            }
                        };
                    }
                    else {
                        Profile profile = Profile.getCurrentProfile();
                        profileTextView.setText(getString(R.string.hello_string) +profile.getName());
                    }
                    fetchProfile();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    LoginManager.getInstance().logOut();
                }
            });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });

    }

    private void fetchProfile(){
        VisibilityChange(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        changeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager manager = LoginManager.getInstance();
                manager.logOut();
                manager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }

        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,DrawerActivity.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                VisibilityChange(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void VisibilityChange(int visibility)
    {
        profileTextView.setVisibility(visibility);
        actionLayout.setVisibility(visibility);
        logoutButton.setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_item_login:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

}
