package own.meet.ownbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    EditText emailAddress, userName, passWord;
    TextView login;
    String email,user,pass;
    Button createAccountButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_OwnBook);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        firebaseAuth = FirebaseAuth.getInstance();
        emailAddress=findViewById(R.id.emailaddress);
        userName=findViewById(R.id.UserName);
        passWord=findViewById(R.id.passWord);
        login = findViewById(R.id.login_in_signup);
        createAccountButton=findViewById(R.id.createaccount);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Login.class);
                startActivity(intent1);
                finish();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailAddress.getText().toString();
                user = userName.getText().toString();
                pass = passWord.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(user) || TextUtils.isEmpty(pass))
                {
                    Toast.makeText(SignUp.this, "Enter all the data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(SignUp.this, "Account Created Successfuly", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent1);
                                        saveUserName(user);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(SignUp.this, "Account is not created , Try Again latter.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
    private void saveUserName(String userName)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("userName",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",userName);
        editor.apply();
    }
}