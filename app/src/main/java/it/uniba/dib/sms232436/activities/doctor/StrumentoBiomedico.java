package it.uniba.dib.sms232436.activities.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;

import android.app.AlertDialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StrumentoBiomedico extends AppCompatActivity {

    private EditText etPatientId;
    private Spinner spinnerStrumento;
    private Button btnAvviaScansione;
    private ProgressBar progressBarScansione;
    private DatabaseReference pazientiRef, misurazioniRef;
    private Medico medico;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strumento_biometrico);

        etPatientId = findViewById(R.id.etPatientId);
        spinnerStrumento = findViewById(R.id.spinnerStrumento);
        btnAvviaScansione = findViewById(R.id.btnAvviaScansione);
        progressBarScansione = findViewById(R.id.progressBarScansione);

        // Database Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        pazientiRef = database.getReference("Pazienti");
        misurazioniRef = database.getReference("Misurazioni");

        // Recupera l'oggetto Medico passato tramite intent
        medico = (Medico) getIntent().getSerializableExtra("medico");

        btnAvviaScansione.setOnClickListener(view -> avviaScansione());

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.strumento));
    }

    private void avviaScansione() {
        String idPaziente = etPatientId.getText().toString().trim();
        String strumento = spinnerStrumento.getSelectedItem().toString();

        if (idPaziente.isEmpty()) {
            Toast.makeText(this, getString(R.string.id_paziente_mancante), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarScansione.setVisibility(View.VISIBLE);

        // Controlla la presenza del paziente nel database
        pazientiRef.orderByChild("id").equalTo(idPaziente).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Simula la scansione e genera dati casuali
                    new Handler().postDelayed(() -> {
                        aggiornaMisurazioni(idPaziente, strumento);
                        mostraDialogoFineScansione();
                        progressBarScansione.setVisibility(View.GONE);
                    }, 3000); // Simula il tempo di scansione
                } else {
                    Toast.makeText(StrumentoBiomedico.this, getString(R.string.paziente_non_trovato), Toast.LENGTH_SHORT).show();
                    progressBarScansione.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StrumentoBiomedico.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.measurements));

    }

    private void aggiornaMisurazioni(String patientId, String strumento) {
        // Ottieni l'ultimo ID dal database per generare il prossimo ID
        misurazioniRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nuovoIdMisurazione;
                if (snapshot.exists()) {
                    // Ottieni l'ultimo ID e incrementa
                    String ultimoId = snapshot.getChildren().iterator().next().getKey();
                    int numeroUltimoId = Integer.parseInt(ultimoId.substring(1)); // Rimuove "M" e converte il resto a intero
                    nuovoIdMisurazione = "M" + String.format("%03d", numeroUltimoId + 1);
                } else {
                    // Se non ci sono misurazioni, parte da "M001"
                    nuovoIdMisurazione = "M001";
                }

                // Genera valori casuali per la simulazione
                Random random = new Random();
                Map<String, Object> misurazioni = new HashMap<>();
                double valoreScansione = 0;
                String descrizioneStrumento = "";

                switch (strumento) {
                    case "Termometro":
                        descrizioneStrumento = "Temperatura corporea";
                        valoreScansione = 36 + random.nextDouble() * 3;
                        valoreScansione = Math.round(valoreScansione * 100.0) / 100.0;
                        misurazioni.put("strumento", "Termometro");
                        misurazioni.put("valore", valoreScansione);
                        break;
                    case "Misuratore di pressione":
                        descrizioneStrumento = "Pressione sanguigna";
                        valoreScansione = 90 + random.nextInt(50);
                        misurazioni.put("strumento", "Pressione");
                        misurazioni.put("valore", valoreScansione);
                        break;
                    case "Glucometro":
                        descrizioneStrumento = "Livello di glucosio";
                        valoreScansione = 70 + random.nextInt(90);
                        misurazioni.put("strumento", "Glucometro");
                        misurazioni.put("valore", valoreScansione);
                        break;
                    case "Bilancia":
                        descrizioneStrumento = "Peso corporeo";
                        valoreScansione = 50 + random.nextInt(100);
                        misurazioni.put("strumento", "Bilancia");
                        misurazioni.put("valore", valoreScansione);
                        break;
                    case "Elettrocardiografo":
                        descrizioneStrumento = "Frequenza cardiaca";
                        valoreScansione = 60 + random.nextInt(40);
                        misurazioni.put("strumento", "Elettrocardiografo");
                        misurazioni.put("valore", valoreScansione);
                        break;
                }

                // Aggiungi la data e l'ora corrente
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String dataOraCorrente = dateFormat.format(new Date());

                misurazioni.put("id_paziente", patientId);
                misurazioni.put("data_ora", dataOraCorrente);

                // Salva i dati su Firebase con l'ID generato
                String finalDescrizioneStrumento = descrizioneStrumento;
                double finalValoreScansione = valoreScansione;
                misurazioniRef.child(nuovoIdMisurazione).setValue(misurazioni)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                mostraRisultatoScansione(finalDescrizioneStrumento, finalValoreScansione, dataOraCorrente);
                            } else {
                                Toast.makeText(StrumentoBiomedico.this, "Errore nel salvataggio dei dati", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StrumentoBiomedico.this, "Errore nel database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostraRisultatoScansione(String strumento, double valore, String dataOra) {
        String messaggio = String.format("Strumento: %s\nValore: %.2f\nData e Ora: %s", strumento, valore, dataOra);

        new AlertDialog.Builder(this)
                .setTitle("Risultato Scansione")
                .setMessage(messaggio)
                .setPositiveButton("OK", null)
                .show();
    }

    private void mostraDialogoFineScansione() {
        new AlertDialog.Builder(this)
                .setTitle("Scansione Completa")
                .setMessage("La scansione è stata completata con successo!")
                .setPositiveButton("OK", null)
                .show();
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
                value = 4;
                break;
            case "StrumentoBiometrico":
                icon = findViewById(R.id.hp_btn);
                icon.setImageResource(R.drawable.ic_misurazioni_bold);
                value = 5;
                break;
        }

        return value;
    }
}

