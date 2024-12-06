package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.Locale;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.activities.Login;
import it.uniba.dib.sms232436.classes.Paziente;

public class ImpostazioniPaziente extends AppCompatActivity {

    private Context context;
    private Paziente paziente;
    private DatabaseReference dbPazienti;
    private Button feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int val = loadLocale();
        setContentView(R.layout.activity_settings_paziente);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
        }

        ImageButton buttonEnglish = findViewById(R.id.english_button);
        ImageButton buttonItalian = findViewById(R.id.italian_button);

        if(val == 1){
            TextView eng_txt = findViewById(R.id.english_text);
            eng_txt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            eng_txt.setTypeface(null, Typeface.BOLD); // Imposta il testo in grassetto
        }else if(val == 2){
            TextView ita_txt = findViewById(R.id.italian_text);
            ita_txt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            ita_txt.setTypeface(null, Typeface.BOLD); // Imposta il testo in grassetto
        }

        buttonEnglish.setOnClickListener(v -> setLocale("en")); // Cambia lingua in inglese
        buttonItalian.setOnClickListener(v -> setLocale("it")); // Cambia lingua in italiano

        // Gestione bottone di Logout
        ImageButton logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(ImpostazioniPaziente.this, "Logout effettuato", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(ImpostazioniPaziente.this, Login.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Rimuove lo stack delle activity precedenti
            startActivity(loginIntent);
            finish();  // Chiude l'activity corrente
        });

        // Gestione bottone di Eliminazione del profilo
        ImageButton deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(ImpostazioniPaziente.this)
                    .setTitle("Conferma eliminazione")
                    .setMessage("Sei sicuro di voler cancellare il tuo profilo?")
                    .setPositiveButton("Sì", (dialog, which) -> deleteProfile())
                    .setNegativeButton("No", null)
                    .show();
        });

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.settings));
    }

    public void feedbackOnClick(final View v) {
        Intent intent = new Intent(this, FeedbackHome.class);
        intent.putExtra("paziente", paziente);
        startActivity(intent);
    }

    private void deleteProfile() {
        String pazienteId = paziente.getId();

        DatabaseReference dbPazienti = FirebaseDatabase.getInstance().getReference("Pazienti");

        dbPazienti.orderByChild("id").equalTo(pazienteId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Trova il nodo da eliminare
                            for (DataSnapshot pazienteSnapshot : snapshot.getChildren()) {
                                // Elimina il paziente
                                pazienteSnapshot.getRef().removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Successo nella cancellazione dal database
                                                deleteFirebaseUser();
                                                Intent loginIntent = new Intent(ImpostazioniPaziente.this, Login.class);
                                                // loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(loginIntent);
                                                finish();
                                            } else {
                                                // Errore durante l'eliminazione dal database
                                                Toast.makeText(ImpostazioniPaziente.this, "Errore durante l'eliminazione dal database", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Nessun paziente trovato con l'id specificato
                            Toast.makeText(ImpostazioniPaziente.this, "Nessun paziente trovato", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Gestisci eventuali errori di accesso al database
                        Toast.makeText(ImpostazioniPaziente.this, "Errore di accesso al database: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteFirebaseUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ImpostazioniPaziente.this, "Profilo eliminato con successo", Toast.LENGTH_LONG).show();
                    Log.d("DeleteProfile", "Navigating to login screen");

                } else {
                    Toast.makeText(ImpostazioniPaziente.this, "Errore durante l'eliminazione dell'account", Toast.LENGTH_LONG).show();
                }
            });
        }
    }



    // Metodo per cambiare la lingua
    private int loadLocale() {
        int val = 0;
        String language = getSharedPreferences("Settings", MODE_PRIVATE)
                .getString("My_Lang", "");
        if(language.equals("en")){
            val = 1;
        }else if(language.equals("it")){
            val = 2;
        }
        if (!language.isEmpty()) {
            setLocale(language);
        }
        return val;
    }

    private void setLocale(String langCode) {
        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLang = currentLocale.getLanguage();

        // Evita di ricreare l'attività se la lingua è già impostata
        if (!currentLang.equals(langCode)) {
            Locale locale = new Locale(langCode);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);

            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            getSharedPreferences("Settings", MODE_PRIVATE)
                    .edit()
                    .putString("My_Lang", langCode)
                    .apply();

            if(langCode.equals("en")){
                Toast.makeText(ImpostazioniPaziente.this, "Language successfully changed!", Toast.LENGTH_SHORT).show();
            }
            if(langCode.equals("it")){
                Toast.makeText(ImpostazioniPaziente.this, "Lingua cambiata con successo!", Toast.LENGTH_SHORT).show();

            }
            recreate();
        }
    }

    // Gestione della bottom bar --> da copiare in tutte le altre activity
    private void initializeBottomBar(){
        int unclickable = 0;
        ImageButton btnHome = findViewById(R.id.home_btn);
        ImageButton btnSettings = findViewById(R.id.settings_btn);
        ImageButton btnSos = findViewById(R.id.sos_btn);
        ImageButton btnMessage = findViewById(R.id.message_btn);
        ImageButton btnHP = findViewById(R.id.hp_btn);

        context = this;

        unclickable = changeIconsColor();
        if(unclickable != 1){
            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImpostazioniPaziente.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 2){
            btnSos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Sos.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 3){
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HomePaziente.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }


        if(unclickable != 4){
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListaMessaggi.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }


        if(unclickable != 5){
            btnHP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Misurazioni.class);
                    intent.putExtra("paziente", paziente);
                    startActivity(intent);
                }
            });
        }

    }

    private int changeIconsColor(){
        int value = 0;
        ImageView icon;
        String classname = this.getClass().getSimpleName();
        Log.d("CurrentActivity", "La classe corrente è: " + classname);
        switch(classname){
            case "ImpostazioniPaziente":
                icon = findViewById(R.id.settings_btn);
                icon.setImageResource(R.drawable.ic_setting_bold);
                value = 1;
                break;
            case "Sos":
                value = 2;
                break;
            case "HomePaziente":
                value = 3;
                break;
            case "ListaMessaggi":
                value = 4;
                break;
            case "Misurazioni":
                value = 5;
                break;
        }

        return value;
    }
}
