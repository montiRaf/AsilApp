package it.uniba.dib.sms232436.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Paziente;
import it.uniba.dib.sms232436.classes.Spesa;

public class SpeseHome extends AppCompatActivity {
    private AnyChartView chartView;
    private TextView textDateRange;
    private DatabaseReference mDatabase;
    private float totFood = 0, totSan = 0, totVis = 0, totSvago = 0, totAltro = 0;
    private String codiceUtente;
    private Date startDate = new Date();
    private Date endDate = new Date();
    private String startDateString;
    private String endDateString;
    private boolean isFragmentDisplayed = false; // Variabile per tenere traccia della visibilità del fragment
    private Context context;
    private Paziente paziente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spese_home);
        textDateRange = findViewById(R.id.text_date_range);
        chartView = findViewById(R.id.shop_chart);
        Calendar[] currentWeekDates = getCurrentWeekDates();
        updateDateRangeText(currentWeekDates[0], currentWeekDates[1]);
        mDatabase = FirebaseDatabase.getInstance().getReference("Spese");
        initializeButtons();
        calculateTotalSpent();

        Button chartBtn = findViewById(R.id.show_chart_button);
        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragmentDisplayed) {
                    closeFragment();
                    chartBtn.setText("Mostra grafico");
                } else {
                    openFragment();
                    chartBtn.setText("Mostra spese");
                }
            }
        });

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
            codiceUtente = paziente.getId();
            Log.d("Nome Paziente", "" + paziente.getNome());
        }

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.homespese_title));
    }

    private void setupBarChart() {
        Cartesian barChart = AnyChart.bar();
        String[] colors = {"#0ABAB5", "#5BC0EB", "#3A86FF", "#028090", "#4C6EF5"};
        List<DataEntry> data = new ArrayList<>();
        data.add(new CustomColorDataEntry("Cibo", totFood, colors[0]));
        data.add(new CustomColorDataEntry("Prodotti Sanitari", totSan, colors[1]));
        data.add(new CustomColorDataEntry("Visite sanitarie", totVis, colors[2]));
        data.add(new CustomColorDataEntry("Svago", totSvago, colors[3]));
        data.add(new CustomColorDataEntry("Altro", totAltro, colors[4]));

        barChart.data(data);
        barChart.tooltip().format(" {%Value} €");
        barChart.yAxis(0).labels().format("{%Value} €");

        chartView.setChart(barChart);
    }

    private class CustomColorDataEntry extends ValueDataEntry {
        CustomColorDataEntry(String x, Number value, String color) {
            super(x, value);
            setValue("fill", color);
        }
    }

    private void openFragment() {
        TableLayout table = findViewById(R.id.tabellaSpese);
        table.setVisibility(View.GONE);
        setupBarChart();
        chartView.setVisibility(View.VISIBLE);
        isFragmentDisplayed = true;
    }

    private void closeFragment() {
        TableLayout table = findViewById(R.id.tabellaSpese);
        chartView.setVisibility(View.GONE);
        table.setVisibility(View.VISIBLE);

        isFragmentDisplayed = false;
    }

    private void calculateTotalSpent() {
        List<Spesa> spese;
        spese = new ArrayList<>();
        mDatabase.orderByChild("dataSpesa").startAt(startDateString).endAt(endDateString).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DATA INIZIO", convertDatetoString(startDate));
                Log.d("DATA FINE", convertDatetoString(endDate));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("FirebaseDebug", "DataSnapshot Key: " + dataSnapshot.getKey());
                    Log.d("FirebaseDebug", "DataSnapshot Value: " + dataSnapshot.getValue());
                    Spesa spesa = dataSnapshot.getValue(Spesa.class);
                    if (spesa != null && spesa.getCodiceUtente() != null && spesa.getCodiceUtente().equals(codiceUtente)) {
                        spese.add(spesa);
                    }
                }

                for (Spesa spesa : spese) {
                    if (spesa.getCategoria() == null) spesa.setCategoria("Altro");
                    String cat = spesa.getCategoria();
                    float importo = Float.parseFloat(spesa.getImporto());
                    switch (cat) {
                        case "Cibo":
                            totFood += importo;
                            break;
                        case "Prodotti Sanitari":
                            totSan += importo;
                            break;
                        case "Visite Sanitarie":
                            totVis += importo;
                            break;
                        case "Svago":
                            totSvago += importo;
                            break;
                        case "Altro":
                            totAltro += importo;
                            break;
                    }
                }

                TextView food = findViewById(R.id.foodtot);
                TextView sanProd = findViewById(R.id.sptot);
                TextView sanApp = findViewById(R.id.satot);
                TextView leisure = findViewById(R.id.leistot);
                TextView other = findViewById(R.id.othtot);

                String total_cat = String.format("%.2f", totFood);
                food.setText(total_cat + " €");
                total_cat = String.format("%.2f", totSan);
                sanProd.setText(total_cat + " €");
                total_cat = String.format("%.2f", totVis);
                sanApp.setText(total_cat + " €");
                total_cat = String.format("%.2f", totSvago);
                leisure.setText(total_cat + " €");
                total_cat = String.format("%.2f", totAltro);
                other.setText(total_cat + " €");

                TextView total = findViewById(R.id.total_money);
                float total_week = totFood + totSan + totVis + totSvago + totAltro;
                String total_spent = String.valueOf(total_week);
                total_spent = String.format("%.2f", total_week);
                total.setText(total_spent + " €");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SpeseHome.this, "Errore nel recupero delle spese!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertDatetoString(Date dataSpesa) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(dataSpesa);
    }

    private void initializeButtons() {
        Button btnOpenDetails = findViewById(R.id.buttonDettagliSpese);
        Button btnOpenRegister = findViewById(R.id.buttonRegistraSpese);

        btnOpenDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpeseHome.this, SpeseDettagli.class);
                intent.putExtra("paziente", paziente);
                startActivity(intent);
            }
        });

        btnOpenRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpeseHome.this, InserisciSpese.class);
                intent.putExtra("paziente", paziente);
                startActivity(intent);
            }
        });
    }

    private Calendar[] getCurrentWeekDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Calendar startOfWeek = (Calendar) calendar.clone();
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Calendar endOfWeek = (Calendar) calendar.clone();

        return new Calendar[]{startOfWeek, endOfWeek};
    }

    private void updateDateRangeText(Calendar startDate, Calendar endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        startDateString = dateFormat.format(startDate.getTime());
        endDateString = dateFormat.format(endDate.getTime());

        //textDateRange.setText("Settimana: " + startDateString + " - " + endDateString);

        // Creare un'altra istanza solo per la visualizzazione nel formato dd/MM/yyyy
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateDisplay = displayFormat.format(startDate.getTime());
        String endDateDisplay = displayFormat.format(endDate.getTime());

        textDateRange.setText("Settimana: " + startDateDisplay + " - " + endDateDisplay);
    }

    private Date convertStringtoDate(String dataSpesa) throws ParseException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            return sdf.parse(dataSpesa);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return null;
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