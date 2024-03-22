    package own.meet.ownbook;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.recyclerview.widget.RecyclerView;
    import androidx.recyclerview.widget.StaggeredGridLayoutManager;

    import com.bumptech.glide.Glide;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.android.material.chip.Chip;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.lang.reflect.Executable;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.Hashtable;
    import java.util.List;
    import java.util.Objects;
    import java.util.concurrent.Executors;

    import own.meet.Model.Data_Model;
    import own.meet.adapter.FirewallAdapter;
    import own.meet.adapter.NoteAdapter;
    import own.meet.notes.Note;
    import own.meet.notesListning.NotesListner;

    public class User_profile extends AppCompatActivity{

        ImageView profilePhoto ;
        TextView email,name,id;
        List<Data_Model> list ;
        Button logOutAccount, deleteAccount;
        Toolbar toolbar;

    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_OwnBook);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setBackgroundDrawable(null);
        }


//        Initialization
        logOutAccount = findViewById(R.id.LogOutButton);
        deleteAccount = findViewById(R.id.delteAccountButton);
        profilePhoto = findViewById(R.id.userProfile);
        toolbar = findViewById(R.id.toolbar);
        email = findViewById(R.id.uerMail);
        name = findViewById(R.id.userName);
//        id = findViewById(R.id.uniqId);
        list = new ArrayList<>();


        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            email.setText(user.getEmail());
            if(user.getDisplayName().toString().trim().isEmpty())
            {
                name.setText("User");
            }
            else
            {
                name.setText(user.getDisplayName());
            }
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).into(profilePhoto);
            }
        }

        logOutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(getApplicationContext(), Welcome.class);
                            startActivity(intent);
                            Toast.makeText(User_profile.this, "Account deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(User_profile.this, "Account is not Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

//  back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

        //    get the user name to show
        private String getUserName()
        {
            SharedPreferences sharedPreferences = getSharedPreferences("userName",0);
            return sharedPreferences.getString("name","Note able to load the name");
        }

        @Override
        public void onBackPressed()
        {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }

    }