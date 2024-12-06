package it.uniba.dib.sms232436.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

import it.uniba.dib.sms232436.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Imposta il ritardo per la splash screen (es. 3 secondi)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Avvia la MainActivity
                //Setta la lingua italiana come lingua d'avvio
                setPreferredLocale();

                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);
                finish(); // Chiude la SplashActivity
            }
        }, 3000); // 3000 ms = 3 secondi
    }

    private void setPreferredLocale() {
        // Ottieni le SharedPreferences dell'app
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        // Recupera la lingua salvata o imposta una lingua predefinita (ad esempio "en")
        String languageCode = prefs.getString("language", null);

        if (languageCode == null) {
            // Se non è salvata una lingua preferita, imposta una lingua predefinita (es. "en")
            languageCode = "it";
            saveLanguage(languageCode);  // Salva la lingua predefinita nelle SharedPreferences
        }

        // Applica la lingua impostata alla configurazione dell'attività
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void saveLanguage(String languageCode) {
        // Metodo per salvare la lingua selezionata nelle SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageCode);
        editor.apply();
    }
}
