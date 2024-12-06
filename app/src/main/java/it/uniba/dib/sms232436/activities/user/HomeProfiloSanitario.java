package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Paziente;

public class HomeProfiloSanitario extends AppCompatActivity {

    Paziente paziente;
    private Context context;
    private TextView idPaziente;
    private TextView emailMedico;
    private TextView medicoCurante;
    private TextView telMedico;
    private String usernameMedico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_profilo_sanitario);

        initializeBottomBar();

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
        }

        // Imposta le informazioni del paziente
        //idPaziente = findViewById(R.id.idPaziente);
        medicoCurante = findViewById(R.id.medicoCurante);
        //username = findViewById(R.id.usernamePaziente);
        //email = findViewById(R.id.emailPaziente);
        emailMedico = findViewById(R.id.emailMedico);
        telMedico = findViewById(R.id.telMedico);

        medicoCurante.setVisibility(View.GONE);
        emailMedico.setVisibility(View.GONE);
        telMedico.setVisibility(View.GONE);

        //idPaziente.setText(paziente.getId());
        //String processedUserName = paziente.getUsername().replace("_", ".");
        //username.setText(processedUserName);
        //email.setText(paziente.getEmail());

        if(paziente.getMedico_curante() != null && !paziente.getMedico_curante().trim().isEmpty()){
            usernameMedico = paziente.getMedico_curante().replace(".", "_");
            retrieveMedico();
        }else{
            medicoCurante.setText(getString(R.string.no_doc));
            medicoCurante.setVisibility(View.VISIBLE);
        }

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.medical_record));
    }

    public void terapieOnClick(View v){

        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        Intent intent = new Intent(v.getContext(), ListaTerapie.class);
                        intent.putExtra("paziente", paziente);
                        v.getContext().startActivity(intent);
                    }
                }).start();
    }

    public void vaccinazioniOnClick(View v){

        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        Intent intent = new Intent(v.getContext(), ListaVaccinazioni.class);
                        intent.putExtra("paziente", paziente);
                        v.getContext().startActivity(intent);
                    }
                }).start();
    }

    public void allergieOnClick(View v){

        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

                        Intent intent = new Intent(v.getContext(), ListaAllergie.class);
                        intent.putExtra("paziente", paziente);
                        v.getContext().startActivity(intent);
                    }
                }).start();
    }

    // Recupero dati del medico curante
    private void retrieveMedico() {
        DatabaseReference mediciRef = FirebaseDatabase.getInstance().getReference("Medici");

        mediciRef.child(usernameMedico).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Medico medico = dataSnapshot.getValue(Medico.class);
                if (medico != null) {
                    Log.d("Medico", "Medico trovato: " + medico.getNome() + " " + medico.getCognome());
                    medicoCurante.setText(medico.getNome() + " " + medico.getCognome());
                    emailMedico.setText(medico.getEmail());
                    telMedico.setText(medico.getNumTelefono());

                    // Rendi visibili le informazioni del medico
                    medicoCurante.setVisibility(View.VISIBLE);
                    emailMedico.setVisibility(View.VISIBLE);
                    telMedico.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Medico", "Errore nel recupero del medico", databaseError.toException());
            }
        });

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
