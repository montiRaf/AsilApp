package it.uniba.dib.sms232436.activities.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Messaggio;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InviaMessaggio extends AppCompatActivity {

    private EditText etIdPaziente, etMessaggio;
    private Button btnInviaMessaggio;
    private DatabaseReference databaseMessaggi;
    private Medico medico;
    private Context context;
    private String idPaziente;
    private String messaggio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invia_messaggio);

        etIdPaziente = findViewById(R.id.et_id_paziente);
        etMessaggio = findViewById(R.id.et_messaggio);
        btnInviaMessaggio = findViewById(R.id.btn_invia_messaggio);

        // Recupera l'oggetto Medico passato tramite intent
        medico = (Medico) getIntent().getSerializableExtra("medico");

        // Firebase Database reference
        databaseMessaggi = FirebaseDatabase.getInstance().getReference("Messaggi");

        // Recupera i dati del medico
        String nomeMedico = medico.getNome();
        String cognomeMedico = medico.getCognome();

        initializeBottomBar();

        btnInviaMessaggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviaMessaggio(nomeMedico, cognomeMedico);
            }
        });

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.send_message));
    }

    private void inviaMessaggio(String nomeMedico, String cognomeMedico) {
        messaggio = etMessaggio.getText().toString().trim();
        idPaziente = etIdPaziente.getText().toString().trim();

        if (idPaziente.isEmpty() || messaggio.isEmpty()) {
            Toast.makeText(this, getString(R.string.compila_campi), Toast.LENGTH_SHORT).show();
            return;
        }

        databaseMessaggi.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nuovoIdMessaggio = "M001";  // Default ID se non ci sono messaggi precedenti

                if (dataSnapshot.exists()) {
                    // Ottieni l'ultimo ID
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String ultimoId = snapshot.getKey();

                        int numero = Integer.parseInt(ultimoId.substring(1));
                        numero++;  // Incrementa il numero
                        nuovoIdMessaggio = String.format("M%03d", numero);  // Crea un ID nel formato M001, M002, ecc.
                    }
                }

                // Ottieni la data e l'ora attuale
                String dataInvio = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Nuovo oggetto Messaggio
                Messaggio nuovoMessaggio = new Messaggio(idPaziente, messaggio, nomeMedico, cognomeMedico, false, dataInvio);

                // Salva il messaggio su Firebase con il nuovo ID
                nuovoMessaggio.setIdMessaggio(nuovoIdMessaggio); // Imposta l'ID del messaggio
                databaseMessaggi.child(nuovoIdMessaggio).setValue(nuovoMessaggio);

                // Messaggio inviato
                Toast.makeText(InviaMessaggio.this, getString(R.string.invio_mess), Toast.LENGTH_LONG).show();

                // Pulisce i campi
                etMessaggio.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gestione degli errori di lettura
                Toast.makeText(InviaMessaggio.this, "Errore nel salvataggio del messaggio", Toast.LENGTH_SHORT).show();
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
                value = 1;
                break;
            case "SosMedico":
                value = 2;
                break;
            case "HomeMedico":
                value = 3;
                break;
            case "InviaMessaggio":
                icon = findViewById(R.id.message_btn);
                icon.setImageResource(R.drawable.ic_message_bold);
                value = 4;
                break;
            case "StrumentoBiometrico":
                value = 5;
                break;
        }

        return value;
    }
}
