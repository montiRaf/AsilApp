package it.uniba.dib.sms232436.activities.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;

public class HomeMedico extends AppCompatActivity {

    private Medico medico;
    private Context context;
    private TextView benvenuto;
    private TextView nomeUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_medico);

        // Recupera l'oggetto Medico passato tramite intent
        medico = (Medico) getIntent().getSerializableExtra("medico");

        if (medico != null) {
            String medicoUsername = medico.getUsername();
        } else {
            Toast.makeText(this, getString(R.string.error_doc), Toast.LENGTH_SHORT).show();
        }

        if(medico != null) {
            benvenuto = findViewById(R.id.benvenuto);

            // Lingua corrente del dispositivo
            String language = Locale.getDefault().getLanguage();

            if (medico.getSesso().equalsIgnoreCase("femmina")) {
                if (language.equals("it")) {
                    benvenuto.setText(R.string.welcome_txt_f);
                }
            } else {
                if (language.equals("it")) {
                    benvenuto.setText(R.string.welcome_txt_m);
                }

            }
        }

            nomeUtente = findViewById(R.id.nomeUtente);
            nomeUtente.setText(medico.getNome());

        initializeBottomBar();

    }

    public void listaPazientiOnCLick(View v) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        Intent intent = new Intent(v.getContext(), ListaPazienti.class);
                        intent.putExtra("medico", medico);
                        v.getContext().startActivity(intent);
                    }
                }).start();
    }

    public void registraPazienteOnClick(View v) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        Intent intent = new Intent(v.getContext(), RegistraPaziente.class);
                        intent.putExtra("medico", medico);
                        v.getContext().startActivity(intent);
                    }
                }).start();
    }

    public void profiloMedicoOnClick(final View v) {
        Intent intent = new Intent(this, ProfiloMedico.class);
        intent.putExtra("medico", medico);
        startActivity(intent);
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
        switch(classname){
            case "ImpostazioniMedico":
                value = 1;
                break;
            case "Sos":
                value = 2;
                break;
            case "HomeMedico":
                icon = findViewById(R.id.home_btn);
                icon.setImageResource(R.drawable.ic_home_bold);
                value = 3;
                break;
            case "InviaMessaggio":
                value = 4;
                break;
            case "StrumentoBiometrico":
                value = 5;
                break;
        }

        return value;
    }
}