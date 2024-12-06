package it.uniba.dib.sms232436.activities.doctor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.activities.Login;
import it.uniba.dib.sms232436.classes.Medico;

public class ImpostazioniMedico extends AppCompatActivity {

    private Context context;
    private Medico medico;
    private DatabaseReference dbMedico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int val = loadLocale();
        setContentView(R.layout.activity_settings_medico);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            medico = (Medico) intent.getSerializableExtra("medico");
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
            Intent loginIntent = new Intent(ImpostazioniMedico.this, Login.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Rimuove lo stack delle activity precedenti
            startActivity(loginIntent);
            finish();  // Chiude l'activity corrente
        });

        // Gestione bottone di Eliminazione del profilo
        ImageButton deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(ImpostazioniMedico.this)
                    .setTitle(getString(R.string.conferma))
                    .setMessage(getString(R.string.conferma_dialog))
                    .setPositiveButton(getString(R.string.si), (dialog, which) -> deleteProfile())
                    .setNegativeButton("No", null)
                    .show();
        });

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.settings));
    }


    private void deleteProfile() {
        String medicoId = medico.getId();

        DatabaseReference dbPazienti = FirebaseDatabase.getInstance().getReference("Medici");

        dbPazienti.orderByChild("id").equalTo(medicoId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Trova il nodo da eliminare
                            for (DataSnapshot pazienteSnapshot : snapshot.getChildren()) {
                                // Elimina il medico
                                pazienteSnapshot.getRef().removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Successo nella cancellazione dal database
                                                deleteFirebaseUser();
                                                Intent loginIntent = new Intent(ImpostazioniMedico.this, Login.class);
                                                // loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(loginIntent);
                                                finish();
                                            } else {
                                                // Errore durante l'eliminazione dal database
                                                Toast.makeText(ImpostazioniMedico.this, getString(R.string.error_setting), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(ImpostazioniMedico.this, getString(R.string.no_doc_setting), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Gestisci eventuali errori di accesso al database
                        Toast.makeText(ImpostazioniMedico.this, getString(R.string.error_db) + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteFirebaseUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ImpostazioniMedico.this, getString(R.string.delete_profile), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ImpostazioniMedico.this, getString(R.string.error_setting), Toast.LENGTH_LONG).show();
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
                Toast.makeText(ImpostazioniMedico.this, "Language successfully changed!", Toast.LENGTH_LONG).show();
            }
            if(langCode.equals("it")){
                Toast.makeText(ImpostazioniMedico.this, "Lingua cambiata con successo!", Toast.LENGTH_LONG).show();

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
                    Intent intent = new Intent(context, ImpostazioniMedico.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 2){
            btnSos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SosMedico.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 3){
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HomeMedico.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }


        if(unclickable != 4){
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InviaMessaggio.class);
                    intent.putExtra("medico", medico);
                    startActivity(intent);
                }
            });
        }

        if(unclickable != 5){
            btnHP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StrumentoBiomedico.class);
                    intent.putExtra("medico", medico);
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
            case "ImpostazioniMedico":
                icon = findViewById(R.id.settings_btn);
                icon.setImageResource(R.drawable.ic_setting_bold);
                value = 1;
                break;
            case "SosMedico":
                value = 2;
                break;
            case "HomePaziente":
                //icon = findViewById(R.id.home_btn);
                //icon.setImageResource(R.drawable.ic_home_bold);
                value = 3;
                break;
            case "ListaMessaggi":
                //icon = findViewById(R.id.message_btn);
                //icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                value = 4;
                break;
            case "StrumentoBiometrico":
                value = 5;
                break;
        }

        return value;
    }
}
