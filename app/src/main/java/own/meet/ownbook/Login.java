package own.meet.ownbook;

import android.content.Intent;
import android.os.Bundle;
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

public class Login extends AppCompatActivity {

    Button loginButton;
    EditText emaile, passe ;
    TextView signup ;

    private FirebaseAuth firebaseAuth1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_OwnBook);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

         loginButton = findViewById(R.id.loginButton);
         emaile = findViewById(R.id.loginEmail) ;
         passe = findViewById(R.id.loginPassWord);
         signup = findViewById(R.id.login_in_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseAuth1 = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String email = emaile.getText().toString();
               String pass = passe.getText().toString();

               if (email.isEmpty() || pass.isEmpty())
               {
                   Toast.makeText(Login.this, "Enter all the data", Toast.LENGTH_SHORT).show();
               }
               else {
                   firebaseAuth1.signInWithEmailAndPassword(email,pass)
                           .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful())
                                   {
                                       Toast.makeText(Login.this, "Successful Login", Toast.LENGTH_SHORT).show();
                                       Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                       startActivity(intent1);
                                       finish();
                                   }
                                   else {
                                       Toast.makeText(Login.this, "Login Fail", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
               }
            }
        });
    }
}