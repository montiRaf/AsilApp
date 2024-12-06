package it.uniba.dib.sms232436.activities.doctor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;

public class ProfiloMedico extends AppCompatActivity {

    private EditText etUsername, etNome, etCognome, etNumTelefono, etEmail, etPassword;
    private Button btnModifica;
    private DatabaseReference databaseReference;
    private boolean passwordVisible = false;
    private Context context;
    private Medico medico;
    private String medicoUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_medico);

        // Inizializza Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Medici");

        // Recupera l'oggetto Medico passato tramite intent
        medico = (Medico) getIntent().getSerializableExtra("medico");
        medicoUsername = medico.getUsername();

        // Inizializza i campi
        etUsername = findViewById(R.id.et_username);
        etNome = findViewById(R.id.et_nome);
        etCognome = findViewById(R.id.et_cognome);
        etNumTelefono = findViewById(R.id.et_num_telefono);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnModifica = findViewById(R.id.btn_modifica);

        String username = medico.getUsername().replace("_", ".");
        String nome = medico.getNome();
        String cognome = medico.getCognome();
        String telefono = medico.getNumTelefono();
        String email = medico.getEmail();
        String password = medico.getPassword();

        // Popola i campi con i dati del medico
        etUsername.setText(username);
        etNome.setText(nome);
        etCognome.setText(cognome);
        etNumTelefono.setText(telefono);
        etEmail.setText(email);
        etPassword.setText(password);

        // Gestisce la modifica dei dati
        btnModifica.setOnClickListener(v -> {
            String nomeModificato = etNome.getText().toString().trim();
            String cognomeModificato = etCognome.getText().toString().trim();
            String telefonoModificato = etNumTelefono.getText().toString().trim();
            String emailModificato = etEmail.getText().toString().trim();
            String passwordModificato = etPassword.getText().toString().trim();
            String usernameModificato = etUsername.getText().toString().trim();

            if (TextUtils.isEmpty(nomeModificato) || TextUtils.isEmpty(cognomeModificato)
                    || TextUtils.isEmpty(telefonoModificato) || TextUtils.isEmpty(emailModificato)
                    || TextUtils.isEmpty(passwordModificato) || TextUtils.isEmpty(usernameModificato)) {
                Toast.makeText(ProfiloMedico.this, R.string.compila_campi, Toast.LENGTH_SHORT).show();
            } else {
                // Aggiorna i dati nel database Firebase
                databaseReference.child(medicoUsername).child("nome").setValue(nomeModificato);
                databaseReference.child(medicoUsername).child("cognome").setValue(cognomeModificato);
                databaseReference.child(medicoUsername).child("numTelefono").setValue(telefonoModificato);
                databaseReference.child(medicoUsername).child("email").setValue(emailModificato);
                databaseReference.child(medicoUsername).child("password").setValue(passwordModificato);
                databaseReference.child(medicoUsername).child("username").setValue(usernameModificato)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showDialog(getString(R.string.successo), getString(R.string.succ_profilo));
                            } else {
                                showDialog(getString(R.string.errore), getString(R.string.errore_profilo));
                            }
                        });
            }
        });

        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // Indice del drawable a destra
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        initializeBottomBar();

        // Impostazione del nome della schermata nella toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.tb_profile));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ricarica i dati del medico da Firebase quando l'activity viene ripresa
        databaseReference.child(medicoUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medico = snapshot.getValue(Medico.class);
                if (medico != null) {
                    String username = medico.getUsername().replace("_", ".");
                    etUsername.setText(username);
                    etNome.setText(medico.getNome());
                    etCognome.setText(medico.getCognome());
                    etNumTelefono.setText(medico.getNumTelefono());
                    etEmail.setText(medico.getEmail());
                    etPassword.setText(medico.getPassword());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfiloMedico", "Errore nel recupero dei dati del medico: " + error.getMessage());
            }
        });
    }

    public void qrcodeOnClick(final View v) {
        Intent intent = new Intent(this, QRCodeGeneratorMedico.class);
        intent.putExtra("medico", medico);
        startActivity(intent);
    }

    // Metodo per alternare la visibilità della password
    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Nascondi la password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_baseline_visibility_off_24, 0);
        } else {
            // Mostra la password
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_baseline_visibility_24, 0);
        }
        passwordVisible = !passwordVisible;
        // Per mantenere il cursore alla fine del testo
        etPassword.setSelection(etPassword.getText().length());
    }

    // Metodo per mostrare la finestra di dialogo
    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
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

