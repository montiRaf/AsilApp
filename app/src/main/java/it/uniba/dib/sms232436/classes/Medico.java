package it.uniba.dib.sms232436.classes;

import java.io.Serializable;
import java.util.Random;

public class Medico implements Serializable {
    private String id;
    private String username;
    private String nome;
    private String cognome;
    private String numTelefono;
    private String email;
    private String password;
    private String sesso;

    public Medico() {
        // Costruttore vuoto necessario per Firebase
    }

    public Medico(String id, String username, String nome, String cognome, String numTelefono, String email, String password, String sesso){
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.numTelefono = numTelefono;
        this.email = email;
        this.password = password;
        this.sesso = sesso;
    }

    // costruttore per la registrazione
    public Medico(String nome, String cognome, String username, String email, String password, String sesso) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
        this.numTelefono = "";
        sb.append("P");
        sb.append(rand.nextInt(1000));
        this.id = sb.toString();
        this.sesso = sesso;
    }

    public String getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getNome(){
        return nome;
    }

    public String getCognome(){
        return cognome;
    }

    public String getNumTelefono(){
        return numTelefono;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setCognome(String cognome){
        this.cognome = cognome;
    }

    public void setNumTelefono(String numTelefono){
        this.numTelefono = numTelefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }
}