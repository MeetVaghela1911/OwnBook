package own.meet.ownbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup radioGroup ;
    RadioButton lightThemeLt;
    RadioButton darkThemeLt;
    RadioButton systemThemeLt;
    private Integer radioTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_OwnBook);
        EdgeToEdge.enable(this);
        setContentView(R.layout.settings_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Setting");
        }


//        radiogroup and buttons
         radioGroup = findViewById(R.id.themeGroup);
         lightThemeLt = radioGroup.findViewById(R.id.lightTheame);
         darkThemeLt = radioGroup.findViewById(R.id.darkTheame);
         systemThemeLt = radioGroup.findViewById(R.id.systemTheame);
         radioTheme = getcheckPre();

        if(radioTheme == 2){
            lightThemeLt.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (radioTheme == 1) {
            darkThemeLt.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            systemThemeLt.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        lightThemeLt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightThemeLt.setChecked(true);
                darkThemeLt.setChecked(false);
                systemThemeLt.setChecked(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                savecheckPre(2);
            }
        });
        darkThemeLt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightThemeLt.setChecked(false);
                darkThemeLt.setChecked(true);
                systemThemeLt.setChecked(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                savecheckPre(1);
            }
        });

        systemThemeLt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightThemeLt.setChecked(false);
                darkThemeLt.setChecked(false);
                systemThemeLt.setChecked(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                savecheckPre(0);
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


    private void savecheckPre(int theme)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("themes",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme",theme);
        editor.apply();
    }

    private int getcheckPre()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("themes",0);
        return sharedPreferences.getInt("theme",0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}