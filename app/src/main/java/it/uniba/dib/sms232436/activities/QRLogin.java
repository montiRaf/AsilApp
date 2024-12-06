package it.uniba.dib.sms232436.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.uniba.dib.sms232436.activities.user.HomePaziente;
import it.uniba.dib.sms232436.classes.Paziente;
import com.journeyapps.barcodescanner.CaptureActivity;


public class QRLogin extends Activity {
    private static final int REQUEST_CAMERA_PERMISSION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startQRScan();
        }
    }



    private void startQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scansiona il tuo QR code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.initiateScan();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScan();
            } else {
                Toast.makeText(this, "Permesso fotocamera non concesso", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Scansione annullata", Toast.LENGTH_LONG).show();
            } else {
                String qrContent = result.getContents();
                Toast.makeText(this, "QR Code riconosciuto: " + qrContent, Toast.LENGTH_LONG).show();
                checkUser(qrContent);
            }
        } else {
            Toast.makeText(this, "Errore durante la scansione", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void checkUser(String userName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pazienti");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String usernameFromDB = snapshot.child(userName).child("username").getValue(String.class);
                    String nomeFromDB = snapshot.child(userName).child("nome").getValue(String.class);
                    String cognomeFromDB = snapshot.child(userName).child("cognome").getValue(String.class);
                    String emailFromDB = snapshot.child(userName).child("email").getValue(String.class);
                    int etàFromDB = snapshot.child(userName).child("età").getValue(Integer.class);
                    String idFromDB = snapshot.child(userName).child("id").getValue(String.class);
                    String indirizzoFromDB = snapshot.child(userName).child("indirizzo").getValue(String.class);
                    String pswFromDB = snapshot.child(userName).child("password").getValue(String.class);
                    String medico_curante = snapshot.child(userName).child("medico_curante").getValue(String.class);
                    String sesso = snapshot.child(userName).child("sesso").getValue(String.class);
                    Paziente persona = new Paziente(idFromDB, usernameFromDB, nomeFromDB, cognomeFromDB, etàFromDB, indirizzoFromDB, emailFromDB, pswFromDB, medico_curante, sesso);
                    // Intent alla home del paziente
                    Intent intent = new Intent(QRLogin.this, HomePaziente.class);
                    intent.putExtra("paziente", persona);
                    startActivity(intent);
                } else {
                    Toast.makeText(QRLogin.this, "User does not exist", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}