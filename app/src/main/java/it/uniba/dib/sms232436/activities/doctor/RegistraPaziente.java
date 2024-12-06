package it.uniba.dib.sms232436.activities.doctor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;

public class RegistraPaziente extends AppCompatActivity {

    private EditText searchBar;
    private ListView pazientiListView;
    private Button aggiungiPazienteButton;
    private Context context;

    private ArrayAdapter<String> pazientiAdapter;
    private ArrayList<String> pazientiList = new ArrayList<>();
    private HashMap<String, String> pazienteIdMap = new HashMap<>();
    private String selectedPazienteId;
    private String medicoUsername;
    private Medico medico;

    private int selectedPosition = -1; // Variabile per tenere traccia della posizione selezionata

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_paziente);

        searchBar = findViewById(R.id.searchBar);
        pazientiListView = findViewById(R.id.pazientiListView);
        aggiungiPazienteButton = findViewById(R.id.aggiungiPazienteButton);

        // Recupera il medico attualmente loggato (assumi che il username sia passato come intent extra)
        // medicoUsername = getIntent().getStringExtra("medicoUsername");

        // Recupera l'oggetto Medico passato tramite intent
        medico = (Medico) getIntent().getSerializableExtra("medico");

        // Recupera lo username dall'oggetto Medico
        if (medico != null) {
            medicoUsername = medico.getUsername();
            Log.d("USERNAME MEDICO", "" + medicoUsername);

        } else {
            Toast.makeText(this, "Errore nel recupero del medico", Toast.LENGTH_SHORT).show();
        }

        // Imposta l'adattatore personalizzato
        pazientiAdapter = new PazientiAdapter(this, pazientiList);
        pazientiListView.setAdapter(pazientiAdapter);

        // Adapter per la lista di pazienti
        //pazientiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pazientiList);
        //pazientiListView.setAdapter(pazientiAdapter);

        // Ascoltatore per la barra di ricerca
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cercaPazienti(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Selezione di un paziente dalla lista
        pazientiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position; // Aggiorna la posizione selezionata
                String selectedPaziente = pazientiList.get(position);
                selectedPazienteId = pazienteIdMap.get(selectedPaziente);
                aggiungiPazienteButton.setEnabled(true); // Abilita il pulsante
                pazientiAdapter.notifyDataSetChanged(); // Notifica l'adattatore per aggiornare la vista
            }
        });

        // Aggiungi paziente alla lista del medico
        aggiungiPazienteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiPazienteAllaLista();
            }
        });

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.register_patient));
    }

    private void cercaPazienti(String query) {
        DatabaseReference pazientiRef = FirebaseDatabase.getInstance().getReference("Pazienti");
        pazientiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pazientiList.clear();
                pazienteIdMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nome = snapshot.child("nome").getValue(String.class);
                    String cognome = snapshot.child("cognome").getValue(String.class);
                    String id = snapshot.child("id").getValue(String.class);
                    if ((nome + " " + cognome).toLowerCase().contains(query.toLowerCase())) {
                        String entry = nome + " " + cognome + " (ID: " + id + ")";
                        pazientiList.add(entry);
                        pazienteIdMap.put(entry, snapshot.getKey());
                    }
                }
                pazientiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistraPaziente.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aggiungiPazienteAllaLista() {
        DatabaseReference pazienteRef = FirebaseDatabase.getInstance().getReference("Pazienti").child(selectedPazienteId);

        // Recupera il campo 'medico_curante' per verificare se il paziente ha già un medico
        pazienteRef.child("medico_curante").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String medicoCuranteAttuale = dataSnapshot.getValue(String.class);

                if (medicoCuranteAttuale != null && !medicoCuranteAttuale.isEmpty()) {
                    // Mostra un dialog se il paziente ha già un medico curante
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistraPaziente.this);
                    builder.setTitle("Paziente già registrato")
                            .setMessage("Questo paziente ha già un medico curante e non può essere registrato nuovamente.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    // Procedi ad aggiungere il medico come curante
                    pazienteRef.child("medico_curante").setValue(medicoUsername).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistraPaziente.this, getString(R.string.add_paziente), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistraPaziente.this, "Errore nell'aggiungere il paziente.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistraPaziente.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Classe adattatore personalizzata per gestire l'evidenziazione dell'elemento selezionato
    private class PazientiAdapter extends ArrayAdapter<String> {
        public PazientiAdapter(Context context, ArrayList<String> pazienti) {
            super(context, android.R.layout.simple_list_item_1, pazienti);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            // Cambia il colore di sfondo per evidenziare l'elemento selezionato
            if (position == selectedPosition) {
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grayLight)); // colore evidenziato
            } else {
                view.setBackgroundColor(Color.TRANSPARENT); // colore normale
            }

            return view;
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


