package it.uniba.dib.sms232436.activities.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.activities.adapters.AllergieAdapter;
import it.uniba.dib.sms232436.classes.Medico;

public class InserisciAllergie extends AppCompatActivity {

    private EditText editTextIdPaziente, editTextNomePaziente;
    private Spinner spinnerAllergie;
    private RecyclerView recyclerViewAllergie;
    private Button buttonAggiungiAllergia, buttonSalva;

    private AllergieAdapter allergieAdapter;
    private List<String> listaAllergie;
    private List<String> allergiePredefinite;

    private DatabaseReference databaseReferencePazienti;
    private DatabaseReference databaseReferenceAllergie;

    private String pazienteId;
    private String nomePaziente;
    private String cognomePaziente;
    private Medico medico;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_allergie);

        pazienteId = getIntent().getStringExtra("paziente_id");
        nomePaziente = getIntent().getStringExtra("paziente_nome");
        cognomePaziente = getIntent().getStringExtra("paziente_cognome");
        medico = (Medico) getIntent().getSerializableExtra("medico");

        init();
        initDB();
        popolaSpinner();

        // Listener per aggiungere un'allergia dalla lista predefinita
        buttonAggiungiAllergia.setOnClickListener(v -> {
            String allergiaSelezionata = spinnerAllergie.getSelectedItem().toString();
            if (!listaAllergie.contains(allergiaSelezionata)) {
                listaAllergie.add(allergiaSelezionata);
                allergieAdapter.notifyItemInserted(listaAllergie.size() - 1);
            }
        });

        // Listener per rimuovere un'allergia dalla lista
        allergieAdapter.setOnItemClickListener(position -> {
            listaAllergie.remove(position);
            allergieAdapter.notifyItemRemoved(position);
        });

        // Listener per salvare le allergie nel database
        buttonSalva.setOnClickListener(v -> {
            if (!pazienteId.isEmpty()) {
                salvaAllergie();
            } else {
                Toast.makeText(InserisciAllergie.this, getString(R.string.id_paziente_mancante), Toast.LENGTH_SHORT).show();
            }
        });

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.add_allergy));
    }

    private void init(){
        // Inizializzazione delle view
        editTextIdPaziente = findViewById(R.id.editTextIdPaziente);
        editTextNomePaziente = findViewById(R.id.editTextNomePaziente);
        spinnerAllergie = findViewById(R.id.spinnerAllergie);
        recyclerViewAllergie = findViewById(R.id.recyclerViewAllergie);
        buttonAggiungiAllergia = findViewById(R.id.buttonAggiungiAllergia);
        buttonSalva = findViewById(R.id.buttonSalva);

        // impostazione dei parametri del paziente
        editTextIdPaziente.setText(pazienteId);
        String nomeCognome = nomePaziente + " " + cognomePaziente;
        editTextNomePaziente.setText(nomeCognome);

        // Inizializzazione della lista delle allergie
        listaAllergie = new ArrayList<>();
        allergieAdapter = new AllergieAdapter(listaAllergie);
        recyclerViewAllergie.setAdapter(allergieAdapter);
        recyclerViewAllergie.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initDB(){
        // Inizializzazione di Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReferencePazienti = database.getReference("Pazienti");
        databaseReferenceAllergie = database.getReference("Allergie");
    }

    private void popolaSpinner(){
        // Popolazione dello spinner con allergie predefinite
        allergiePredefinite = Arrays.asList(getString(R.string.arachidi), getString(R.string.banana), getString(R.string.crostacei), getString(R.string.fragole), getString(R.string.glutine), getString(R.string.latte), getString(R.string.lieviti), getString(R.string.mele), getString(R.string.molluschi),getString(R.string.patate), getString(R.string.pesce), getString(R.string.polline), getString(R.string.pomodoro), getString(R.string.uova));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allergiePredefinite);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAllergie.setAdapter(adapter);
    }

    private void salvaAllergie() {

        databaseReferencePazienti.orderByChild("id").equalTo(pazienteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    databaseReferenceAllergie.child(pazienteId).setValue(listaAllergie)
                            .addOnSuccessListener(aVoid -> Toast.makeText(InserisciAllergie.this, getString(R.string.save_allergie), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(InserisciAllergie.this, getString(R.string.errore_allergie), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(InserisciAllergie.this, getString(R.string.paziente_non_trovato), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InserisciAllergie.this, getString(R.string.allergie_error), Toast.LENGTH_SHORT).show();
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
                value = 4;
                break;
            case "StrumentoBiometrico":
                value = 5;
                break;
        }

        return value;
    }
}