package it.uniba.dib.sms232436.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.uniba.dib.sms232436.R;

public class PasswordDimenticata extends AppCompatActivity {

    EditText inputEmail;
    Button resetPasswordButton;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_dimenticata);

        inputEmail = findViewById(R.id.inputEmail);
        resetPasswordButton = findViewById(R.id.btnResetPassword);

        auth = FirebaseAuth.getInstance();
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = inputEmail.getText().toString();

        if (email.isEmpty()) {
            inputEmail.setError(getString(R.string.checkEmailReq));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError(getString(R.string.checkEmail));
            return;
        }

        Log.d("PasswordDimenticata", "Email inserita: " + email);

        // Riferimenti ai database Pazienti e Medici
        DatabaseReference pazientiReference = FirebaseDatabase.getInstance().getReference("Pazienti");
        DatabaseReference mediciReference = FirebaseDatabase.getInstance().getReference("Medici");

        // Prima verifica nel database Pazienti
        Query queryPazienti = pazientiReference.orderByChild("email").equalTo(email);
        queryPazienti.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // L'email esiste tra i pazienti
                    Log.d("PasswordDimenticata", "Email trovata tra i pazienti.");
                    inviaEmailReset(email);
                } else {
                    // Se non è tra i Pazienti, verifica nel database Medici
                    Log.d("PasswordDimenticata", "Email non trovata tra i pazienti, controllo nei medici.");
                    verificaMedici(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PasswordDimenticata", "Errore nel database: " + databaseError.getMessage());
                Toast.makeText(PasswordDimenticata.this, R.string.failedPassword, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verificaMedici(String email) {
        DatabaseReference mediciReference = FirebaseDatabase.getInstance().getReference("Medici");
        Query queryMedici = mediciReference.orderByChild("email").equalTo(email);

        queryMedici.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // L'email esiste tra i medici
                    Log.d("PasswordDimenticata", "Email trovata tra i medici.");
                    inviaEmailReset(email);
                } else {
                    // Se non esiste né nei Pazienti né nei Medici
                    Log.d("PasswordDimenticata", "Email non trovata tra i medici.");
                    inputEmail.setError("Email non trovata.");
                    inputEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PasswordDimenticata", "Errore nel database: " + databaseError.getMessage());
                Toast.makeText(PasswordDimenticata.this, R.string.failedPassword, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inviaEmailReset(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("PasswordDimenticata", "Email inviata con successo, avvio Login");
                    Toast.makeText(PasswordDimenticata.this, R.string.checkEmailResetPass, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PasswordDimenticata.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("PasswordDimenticata", "Errore nell'invio dell'email: " + task.getException());
                    Toast.makeText(PasswordDimenticata.this, R.string.failedPassword, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}