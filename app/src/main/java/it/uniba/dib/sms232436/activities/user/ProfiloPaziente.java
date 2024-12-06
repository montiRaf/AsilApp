package it.uniba.dib.sms232436.activities.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Medico;
import it.uniba.dib.sms232436.classes.Paziente;

import android.app.AlertDialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfiloPaziente extends AppCompatActivity {

    private EditText etUsername, etNome, etCognome, etIndirizzo, etEmail, etPassword;
    private Button btnModifica;
    private DatabaseReference databaseReference;
    private String pazienteUsername;
    private Paziente paziente;
    private boolean passwordVisible = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_paziente);

        // Inizializza Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Pazienti");

        // Inizializza i campi
        etUsername = findViewById(R.id.et_username);
        etNome = findViewById(R.id.et_nome);
        etCognome = findViewById(R.id.et_cognome);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnModifica = findViewById(R.id.btn_modifica);

        // Recupera l'Intent e i dati
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paziente = (Paziente) intent.getSerializableExtra("paziente");
            pazienteUsername = paziente.getUsername().replace(".", "_");
            Log.d("Nome Paziente", "" + paziente.getNome());
        }

        String username = paziente.getUsername().replace("_", ".");
        String nome = paziente.getNome();
        String cognome = paziente.getCognome();
        String email = paziente.getEmail();
        String password = paziente.getPassword();

        // Popola i campi con i dati del paziente
        etUsername.setText(username);
        etNome.setText(nome);
        etCognome.setText(cognome);
        etEmail.setText(email);
        etPassword.setText(password);

        // Gestisce la modifica dei dati
        btnModifica.setOnClickListener(v -> {
            String nomeModificato = etNome.getText().toString().trim();
            String cognomeModificato = etCognome.getText().toString().trim();
            String emailModificato = etEmail.getText().toString().trim();
            String passwordModificato = etPassword.getText().toString().trim();
            String usernameModificato = etUsername.getText().toString().trim();

            if (TextUtils.isEmpty(nomeModificato) || TextUtils.isEmpty(cognomeModificato) || TextUtils.isEmpty(emailModificato)
                    || TextUtils.isEmpty(passwordModificato) || TextUtils.isEmpty(usernameModificato)) {
                Toast.makeText(ProfiloPaziente.this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
            } else {
                // Aggiorna i dati nel database Firebase
                databaseReference.child(pazienteUsername).child("nome").setValue(nomeModificato);
                databaseReference.child(pazienteUsername).child("cognome").setValue(cognomeModificato);
                databaseReference.child(pazienteUsername).child("email").setValue(emailModificato);
                databaseReference.child(pazienteUsername).child("password").setValue(passwordModificato);
                databaseReference.child(pazienteUsername).child("username").setValue(usernameModificato)
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

    public void qrcodeOnClick(final View v) {
        Intent intent = new Intent(this, QRCodeGenerator.class);
        intent.putExtra("paziente", paziente);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Ricarica i dati del medico da Firebase quando l'activity viene ripresa
        databaseReference.child(pazienteUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paziente = snapshot.getValue(Paziente.class);
                if (paziente != null) {
                    String username = paziente.getUsername().replace("_", ".");
                    etUsername.setText(username);
                    etNome.setText(paziente.getNome());
                    etCognome.setText(paziente.getCognome());
                    etEmail.setText(paziente.getEmail());
                    etPassword.setText(paziente.getPassword());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfiloPaziente", "Errore nel recupero dei dati del paziente: " + error.getMessage());
            }
        });
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
