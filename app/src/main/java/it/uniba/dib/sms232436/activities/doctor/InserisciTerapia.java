package it.uniba.dib.sms232436.activities.doctor;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;

public class InserisciTerapia extends AppCompatActivity {

    private EditText idPaziente;
    private EditText editTextDiagnosi;
    private EditText editTextNomeFarmaco;
    private EditText editTextDosaggio;
    private EditText editTextPosologia;
    private EditText editTextDataInizio;
    private EditText editTextDataFine;
    private Button aggiungiTerapia;
    private FirebaseAuth mAuth;
    private DatabaseReference dbTerapie, dbPazienti;
    private String nomeDottore;
    private String cognomeDottore;
    private Medico medico;
    private Context context;
    private String pazienteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_terapia);
        pazienteId = getIntent().getStringExtra("paziente_id");
        nomeDottore = getIntent().getStringExtra("medico_nome");
        cognomeDottore = getIntent().getStringExtra("medico_cognome");

        init();
        initializeDB();

        medico = (Medico) getIntent().getSerializableExtra("medico");

        // Listener per la selezione della data
        editTextDataInizio.setOnClickListener(v -> mostraDatePickerDialog(editTextDataInizio));
        editTextDataFine.setOnClickListener(v -> mostraDatePickerDialog(editTextDataFine));

        // Listener per il bottone di salvataggio
        aggiungiTerapia.setOnClickListener(v -> aggiungiTerapiaOnClick());

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.add_therapy));
    }

    private void init(){
        idPaziente = findViewById(R.id.idPaziente);
        editTextDiagnosi = findViewById(R.id.editTextDiagnosi);
        editTextNomeFarmaco = findViewById(R.id.editTextNomeFarmaco);
        editTextDosaggio = findViewById(R.id.editTextDosaggio);
        editTextPosologia = findViewById(R.id.editTextPosologia);
        editTextDataInizio = findViewById(R.id.editTextDataInizio);
        editTextDataFine = findViewById(R.id.editTextDataFine);
        aggiungiTerapia = findViewById(R.id.buttonSalva);

        idPaziente.setText(pazienteId);

        // Recupero dati del dottore dal database -- il DB non esiste al momento
        // String dottoreUid = mAuth.getCurrentUser().getUid();
        // recuperaDatiDottore(dottoreUid);
        // Recupera l'oggetto Medico passato tramite intent
    }

    // Inizializzazione FirebaseAuth e DatabaseReference
    private void initializeDB(){
        mAuth = FirebaseAuth.getInstance();
        dbTerapie = FirebaseDatabase.getInstance().getReference("Terapie");
        dbPazienti = FirebaseDatabase.getInstance().getReference("Pazienti");
    }

    private void recuperaDatiDottore(String dottoreUid) {
        // recupero dei dati del dottore
    }

    private void aggiungiTerapiaOnClick() {
        // Recupero dei dati inseriti
        String idPazienteVal = idPaziente.getText().toString().trim();
        String diagnosi = editTextDiagnosi.getText().toString().trim();
        String nomeFarmaco = editTextNomeFarmaco.getText().toString().trim();
        String dosaggio = editTextDosaggio.getText().toString().trim();
        String posologia = editTextPosologia.getText().toString().trim();
        String dataInizio = editTextDataInizio.getText().toString().trim();
        String dataFine = editTextDataFine.getText().toString().trim();

        // Controllo che tutti i campi siano stati completati
        if (TextUtils.isEmpty(idPazienteVal) || TextUtils.isEmpty(diagnosi) || TextUtils.isEmpty(nomeFarmaco) ||
                TextUtils.isEmpty(dosaggio) || TextUtils.isEmpty(posologia) || TextUtils.isEmpty(dataInizio) || TextUtils.isEmpty(dataFine)) {
            Toast.makeText(this, getString(R.string.compila_campi), Toast.LENGTH_SHORT).show();
            return;
        }

        // Controllo se il paziente esiste nel database
        dbPazienti.orderByChild("id").equalTo(idPazienteVal).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Se il paziente esiste, prelevo i dati e salvo la terapia
                    for (DataSnapshot pazienteSnapshot : snapshot.getChildren()) {
                        String nomePaziente = pazienteSnapshot.child("nome").getValue(String.class);
                        String cognomePaziente = pazienteSnapshot.child("cognome").getValue(String.class);

                        // Genera e salva la terapia
                        generaEGuardaTerapia(idPazienteVal, nomePaziente, cognomePaziente, diagnosi, nomeFarmaco, dosaggio, posologia, dataInizio, dataFine);
                    }
                } else {
                    // Il paziente non è presente nel db
                    Toast.makeText(InserisciTerapia.this, getString(R.string.paziente_non_trovato), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InserisciTerapia.this, getString(R.string.er_recupero_paziente), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generaEGuardaTerapia(String idPaziente, String nomePaziente, String cognomePaziente, String diagnosi,
                                      String nomeFarmaco, String dosaggio, String posologia, String dataInizio, String dataFine) {
        dbTerapie.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int nextId = 1;
                if (snapshot.exists()) {
                    for (DataSnapshot terapiaSnapshot : snapshot.getChildren()) {
                        String key = terapiaSnapshot.getKey();
                        if (key != null && key.startsWith("T")) {
                            try {
                                int currentId = Integer.parseInt(key.substring(1));
                                if (currentId >= nextId) {
                                    nextId = currentId + 1;
                                }
                            } catch (NumberFormatException e) {
                                // Ignora le chiavi che non seguono il formato previsto
                            }
                        }
                    }
                }
                String terapiaId = String.format("T%04d", nextId);
                salvaTerapia(terapiaId, idPaziente, nomePaziente, cognomePaziente, diagnosi, nomeFarmaco, dosaggio, posologia, dataInizio, dataFine);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InserisciTerapia.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvaTerapia(String terapiaId, String idPaziente, String nomePaziente, String cognomePaziente, String diagnosi,
                              String nomeFarmaco, String dosaggio, String posologia, String dataInizio, String dataFine) {

        Map<String, Object> terapia = new HashMap<>();
        terapia.put("idPaziente", idPaziente);
        terapia.put("nomePaziente", nomePaziente);
        terapia.put("cognomePaziente", cognomePaziente);
        terapia.put("diagnosi", diagnosi);
        terapia.put("nomeFarmaco", nomeFarmaco);
        terapia.put("dosaggio", dosaggio);
        terapia.put("posologia", posologia);
        terapia.put("dataInizio", dataInizio);
        terapia.put("dataFine", dataFine);
        terapia.put("nomeDottore", nomeDottore);
        terapia.put("cognomeDottore", cognomeDottore);

        dbTerapie.child(terapiaId).setValue(terapia).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(InserisciTerapia.this, getString(R.string.successo_terapia), Toast.LENGTH_SHORT).show();
                // Pulisce i campi di inserimento
                this.editTextDiagnosi.setText("");
                this.editTextNomeFarmaco.setText("");
                this.editTextDosaggio.setText("");
                this.editTextPosologia.setText("");
                this.editTextDataInizio.setText("");
                this.editTextDataFine.setText("");
            } else {
                Toast.makeText(InserisciTerapia.this, "Errore nell'inserimento della terapia.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostraDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Formatta la data selezionata nel formato DD/MM/YYYY
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    editText.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
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
            case "Sos":
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
