package it.uniba.dib.sms232436.activities.doctor;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Vaccinazione;

public class InserisciVaccinazione extends AppCompatActivity {

    private EditText nomeVaccino;
    private EditText nomeDottore;
    private EditText dataVaccinazione;
    private EditText nomePaziente;
    private EditText idPaziente;
    private EditText richiamo;
    private CheckBox checkBoxRichiamo;
    private Button inserisciVaccinazione;
    private Medico medico;
    private Context context;
    private DatabaseReference dbVaccinazioni, dbPersone;
    private String pazienteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_vaccinazione);
        pazienteId = getIntent().getStringExtra("paziente_id");

        init();
        initializeDB();

        dataVaccinazione.setOnClickListener(v -> showDatePicker(dataVaccinazione));
        medico = (Medico) getIntent().getSerializableExtra("medico");

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.add_vaccination));
        initializeBottomBar();

    }

    private void init(){
        nomeVaccino = findViewById(R.id.editTextDiagnosi);
        nomeDottore = findViewById(R.id.editTextNomeFarmaco);
        dataVaccinazione = findViewById(R.id.editTextData);
        inserisciVaccinazione = findViewById(R.id.buttonSalva);
        nomePaziente = findViewById(R.id.editTextNomePaziente);
        idPaziente = findViewById(R.id.idPaziente);
        richiamo = findViewById(R.id.editTextRichiamo);
        checkBoxRichiamo = findViewById(R.id.checkBoxRichiamo);

        idPaziente.setText(pazienteId);
    }

    private void initializeDB(){
        dbVaccinazioni = FirebaseDatabase.getInstance().getReference("Vaccinazioni");
        dbPersone = FirebaseDatabase.getInstance().getReference("Pazienti");
    }

    private void inserisciVaccino() {
        final String vaccino = nomeVaccino.getText().toString().trim();
        final String nomeDot = nomeDottore.getText().toString().trim();
        final String data = dataVaccinazione.getText().toString().trim();
        final String codiceP = idPaziente.getText().toString().trim();
        final String richiamoStr = checkBoxRichiamo.isChecked() ? richiamo.getText().toString().trim() : null;

        if (!vaccino.isEmpty() && !nomeDot.isEmpty() && !data.isEmpty()  && !codiceP.isEmpty()) {
            dbPersone.orderByChild("id").equalTo(codiceP).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dbVaccinazioni.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long count = snapshot.getChildrenCount();
                                String id = String.format("V%04d", count + 1);

                                // Crea un'istanza della classe Vaccinazione
                                Vaccinazione vaccinazione = new Vaccinazione(codiceP, vaccino, data, nomeDot, richiamoStr);

                                // Salvataggio nel database
                                dbVaccinazioni.child(id).setValue(vaccinazione).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(InserisciVaccinazione.this, getString(R.string.successo_vaccinazione), Toast.LENGTH_LONG).show();
                                            // Resetta i campi
                                            nomeVaccino.setText("");
                                            nomeDottore.setText("");
                                            dataVaccinazione.setText("");
                                            nomePaziente.setText("");
                                            richiamo.setText("");
                                            checkBoxRichiamo.setChecked(false);
                                        } else {
                                            Toast.makeText(InserisciVaccinazione.this, "Errore nel salvataggio della vaccinazione", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(InserisciVaccinazione.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(InserisciVaccinazione.this, getString(R.string.er_codice_paziente), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(InserisciVaccinazione.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.compila_campi), Toast.LENGTH_SHORT).show();
        }
    }


    public void inserisciVaccinazioneOnClick(View view){
        inserisciVaccino();
    }

    public void showDatePicker(View view){
        final Calendar calendar = Calendar.getInstance();
        int anno = calendar.get(Calendar.YEAR);
        int mese = calendar.get(Calendar.MONTH);
        int giorno = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, anno1, mese1, giornoMese) -> {
                    // il mese parte da 0
                    String selezioneData = giornoMese + "/" + (mese1 + 1) + "/" + anno1;
                    dataVaccinazione.setText(selezioneData);
                },
                anno, mese, giorno);
        datePickerDialog.show();
    }

    public void onCheckBoxClicked(View view) {
        // Verifica che il view sia una CheckBox e gestisci il suo stato
        if (view instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) view;
            richiamo.setVisibility(checkBox.isChecked() ? View.VISIBLE : View.GONE);
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
                value = 5;
                break;
        }

        return value;
    }

}