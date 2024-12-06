package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;

public class StrutturaHome extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Paziente paziente;
    private Context context;
    private double lat, lng;
    private String nomeStr;
    private String codiceStruttura = "STR-001";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadStruttura();
        setContentView(R.layout.activity_struttura_home);
        ImageView strutturaImg = findViewById(R.id.imageStruttura);
        strutturaImg.setImageResource(R.drawable.struttura);
        //initializeBottomBar();
        Button btnMap = findViewById(R.id.view_map_button);
        Button btnDoc = findViewById(R.id.view_doc_button);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
            Log.d("Nome Paziente", "" + paziente.getNome());
        }


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StrutturaHome.this, MappaStruttura.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("nomeStruttura", nomeStr);
                intent.putExtra("codiceStruttura", codiceStruttura);
                intent.putExtra("paziente", paziente);
                startActivity(intent);
            }
        });

        btnDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StrutturaHome.this, LettoreDocumento.class);
                intent.putExtra("paziente", paziente);
                startActivity(intent);
            }
        });

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.centers));
    }

    private void loadStruttura(){
        mDatabase = FirebaseDatabase.getInstance().getReference("Strutture");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nomeStruttura = "", indirizzo = "", capacità = "", descrizione = "", città = "", provincia = "", latitudine="", longitudine ="";
                    if(Objects.equals(snapshot.getKey(), codiceStruttura)){
                        nomeStruttura = snapshot.child("nomeStruttura").getValue(String.class);
                        indirizzo = snapshot.child("indirizzo").getValue(String.class);
                        capacità = snapshot.child("capacità").getValue(String.class);
                        descrizione = snapshot.child("descrizione").getValue(String.class);
                        città = snapshot.child("città").getValue(String.class);
                        provincia = snapshot.child("provincia").getValue(String.class);
                        latitudine = snapshot.child("latitudine").getValue(String.class);
                        longitudine = snapshot.child("longitudine").getValue(String.class);
                        lat = Double.parseDouble(latitudine);
                        lng = Double.parseDouble(longitudine);
                        nomeStr = nomeStruttura;
                    }

                    // Puoi aggiornare la UI, ad esempio con una TextView
                    TextView textNS = findViewById(R.id.str_value);
                    TextView textInd = findViewById(R.id.addr_value);
                    TextView textCapac = findViewById(R.id.cap_value);
                    TextView textDesc = findViewById(R.id.desc_value);
                    TextView textCit = findViewById(R.id.city_value);
                    TextView textProv = findViewById(R.id.prov_value);

                    textNS.setText(nomeStruttura);
                    textInd.setText(indirizzo);
                    textCapac.setText(capacità);
                    textDesc.setText(descrizione);
                    textCit.setText(città);
                    textProv.setText(provincia);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Gestisci l'errore
                Log.w("FirebaseError", "Failed to read value.", error.toException());
            }
        });
    }

    private void loadLocale() {
        String language = getSharedPreferences("Settings", MODE_PRIVATE)
                .getString("My_Lang", "");
        setLocale(language);

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