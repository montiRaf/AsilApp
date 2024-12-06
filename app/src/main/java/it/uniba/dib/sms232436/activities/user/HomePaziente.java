package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;


public class HomePaziente extends AppCompatActivity {

    private TextView benvenuto;
    private TextView nomeUtente;
    private Context context;
    private Paziente paziente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
        }

        if(paziente != null){
            benvenuto = findViewById(R.id.benvenuto);

            // Lingua corrente del dispositivo
            String language = Locale.getDefault().getLanguage();

            if(paziente.getSesso().equalsIgnoreCase("femmina")){
                if(language.equals("it")){
                    benvenuto.setText(R.string.welcome_txt_f);
                }
            }else{
                if(language.equals("it")){
                    benvenuto.setText(R.string.welcome_txt_m);
                }

            }

            nomeUtente = findViewById(R.id.nomeUtente);
            String nome = paziente.getNome();
            String id = paziente.getId();
            nomeUtente.setText(String.format("%s (%s)", nome, id));
        }

        initializeBottomBar();
    }

    public void profiloOnClick(final View v) {
        Intent intent = new Intent(this, ProfiloPaziente.class);
        intent.putExtra("paziente", paziente);
        startActivity(intent);
    }

    public void videoOnClick(final View v) {
        Intent intent = new Intent(this, YTPlayer.class);
        intent.putExtra("paziente", paziente);
        startActivity(intent);
    }

    // Gestione della voce -- SPESE
    public void speseOnClick(View v) {
        // Esegui l'animazione di scala quando viene premuta la CardView
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        Intent intent = new Intent(v.getContext(), SpeseHome.class);
                        intent.putExtra("paziente", paziente);
                        v.getContext().startActivity(intent);
                    }
                }).start();
    }

    // Gestione della voce -- CARTELLA CLINICA
    public void profiloSanitarioOnClick(final View v) {
        // Esegui l'animazione di scala quando viene premuta la CardView
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        // passaggio dei dati del paziente
                        Intent intent = new Intent(v.getContext(), HomeProfiloSanitario.class);
                        intent.putExtra("paziente", paziente);
                        v.getContext().startActivity(intent);
                    }
                }).start();
    }

    // Gestione della voce -- CENTRI DI ACCOGLIENZA
    public void centriDiAccoglienzaOnClick(final View v) {
        // Esegui l'animazione di scala quando viene premuta la CardView
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        // passaggio dei dati del paziente
                        Intent intent = new Intent(v.getContext(), StrutturaHome.class);
                        intent.putExtra("paziente", paziente);
                        v.getContext().startActivity(intent);
                    }
                }).start();
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
                icon = findViewById(R.id.home_btn);
                icon.setImageResource(R.drawable.ic_home_bold);
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

