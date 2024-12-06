package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;
import it.uniba.dib.sms232436.classes.Spesa;
import it.uniba.dib.sms232436.activities.adapters.SpeseAdapter;

public class SpeseDettagli extends AppCompatActivity {
    private TextView textDateRange;
    private DatabaseReference mDatabase;
    private SpeseAdapter adapter;
    private List<Spesa> spese;
    private Date startDate;
    private Date endDate;
    private String codiceUtente;
    private Context context;
    private Paziente paziente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spese_dettagli);
        Spinner weekSpinner = findViewById(R.id.week_spinner);
        TextView title = findViewById(R.id.toolbar_title);
        String tit = getString(R.string.spesdett_tit);
        title.setText(tit);
        textDateRange = findViewById(R.id.text_date_range);
        int currentWeek = getCurrentWeekOfYear();
        List<String> weeksList = generateWeeksWithDateRange(currentWeek);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
            codiceUtente = paziente.getId();
            Log.d("Nome Paziente", "" + paziente.getNome());
        }

        setupSpinner(weekSpinner, weeksList);
        weekSpinner.setSelection(currentWeek - 1);
        setSpinnerListener(weekSpinner);

        RecyclerView recyclerView = findViewById(R.id.shop_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spese = new ArrayList<>();
        adapter = new SpeseAdapter(this, spese);
        recyclerView.setAdapter(adapter);
        // Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("Spese");
        Log.i("DATA", convertDatetoString(endDate));
        fetchSpese(codiceUtente);

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.spesdett_tit));
    }

    private void fetchSpese(String codiceUtente) {
        mDatabase.orderByChild("dataSpesa").startAt(convertDatetoString(startDate)).endAt(convertDatetoString(endDate)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double SpeseTotali = 0.0;
                spese.clear();
                Log.d("DATA1", convertDatetoString(startDate));
                Log.d("DATA2", convertDatetoString(endDate));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("FirebaseDebug", "DataSnapshot Key: " + dataSnapshot.getKey());
                    Log.d("FirebaseDebug", "DataSnapshot Value: " + dataSnapshot.getValue());
                    Spesa spesa = dataSnapshot.getValue(Spesa.class);
                    Log.d("DATA CORRENTE", spesa.getDataSpesa());
                    SpeseTotali = SpeseTotali + Double.parseDouble(spesa.getImporto());
                    Log.d("FirebaseDebug", "Spesa: " + spesa.getCodiceSpesa() + ", " + spesa.getCodiceUtente());
                    Log.d("Codice Utente", codiceUtente);
                    if (spesa != null && spesa.getCodiceUtente().equals(codiceUtente)) {
                        spese.add(spesa);
                    }
                }

                if (spese.isEmpty()) {
                    Toast.makeText(SpeseDettagli.this, getString(R.string.no_spese), Toast.LENGTH_LONG).show();
                }
                TextView totaleSpese = findViewById(R.id.total_spese);
                DecimalFormat df = new DecimalFormat("#.00");
                String totale = df.format(SpeseTotali);
                if(SpeseTotali == 0.0 || SpeseTotali == null) totale = "0.00";
                totaleSpese.setText("Totale spese: " + totale + " €");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SpeseDettagli.this, getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Funzione per ottenere la settimana corrente dell'anno
    private int getCurrentWeekOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    // Funzione per generare la lista delle settimane con intervallo di date
    private List<String> generateWeeksWithDateRange(int currentWeek) {
        List<String> weeksList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        for (int i = 1; i <= currentWeek; i++) {
            // Calcola la data di inizio e fine settimana
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.WEEK_OF_YEAR, i);
            startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            startDate = startCalendar.getTime();

            Calendar endCalendar = (Calendar) startCalendar.clone();
            endCalendar.add(Calendar.DAY_OF_WEEK, 6);
            endDate = endCalendar.getTime();

            // Formatta le date e aggiungi alla lista
            String weekRange = sdf.format(startDate) + " - " + sdf.format(endDate);
            weeksList.add(weekRange);
        }

        return weeksList;
    }

    // Funzione per configurare lo spinner con i dati
    private void setupSpinner(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Funzione per impostare il listener dello spinner
    private void setSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedWeek = parentView.getItemAtPosition(position).toString();
                try {
                    handleWeekSelection(selectedWeek);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                fetchSpese(codiceUtente);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Codice da eseguire se non viene selezionato nulla
            }
        });
    }

    private String convertDatetoString(Date dataSpesa){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(dataSpesa);
    }

    private Date convertStringtoDate(String dataSpesa) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.parse(dataSpesa);
    }

    // Funzione per gestire la selezione della settimana
    private void handleWeekSelection(String selectedWeek) throws ParseException {
        Toast.makeText(getApplicationContext(), "Selected: " + selectedWeek, Toast.LENGTH_SHORT).show();
        String dates[] = selectedWeek.split("-");
        SimpleDateFormat original_date = new SimpleDateFormat("dd MMM", Locale.getDefault());
        SimpleDateFormat target_date = new SimpleDateFormat("MM/dd/yyyy");
        Date date_start = original_date.parse(dates[0]);
        String newStartDate = target_date.format(date_start);
        Date date_end = original_date.parse(dates[1]);
        String newEndDate = target_date.format(date_end);
        startDate = target_date.parse(newStartDate);
        endDate = target_date.parse(newEndDate);
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
