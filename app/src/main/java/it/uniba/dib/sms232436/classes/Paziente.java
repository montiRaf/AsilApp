package it.uniba.dib.sms232436.classes;

import java.io.Serializable;
import java.util.Random;

public class Paziente implements Serializable {
    private String id;
    private String username;
    private String nome;
    private String cognome;
    private int età;
    private String indirizzo;
    private String email;
    private String password;
    private String medico_curante;
    private String sesso;

    public Paziente() {
        // Costruttore vuoto necessario per Firebase
    }

    // Costruttore per il login
    public Paziente(String id, String username, String nome, String cognome, int età, String indirizzo, String email, String password, String medico_curante, String sesso){
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.età = età;
        this.indirizzo = indirizzo;
        this.email = email;
        this.password = password;
        this.medico_curante = medico_curante;
        this.sesso = sesso;
    }

    // Costruttore per testare
    public Paziente(String id, String username, String nome, String cognome, int età, String indirizzo, String email, String password, String medico_curante){
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.età = età;
        this.indirizzo = indirizzo;
        this.email = email;
        this.password = password;
        this.medico_curante = medico_curante;
    }

    public Paziente(String nome, String cognome, String username, String email, String password, String sesso, int eta) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
        this.medico_curante = "";
        this.indirizzo = "";
        this.sesso = sesso;
        sb.append("P");
        sb.append(rand.nextInt(1000));
        this.id = sb.toString();
        this.età = eta;
    }

    public Paziente(String nome, String cognome, String username, String email, String password, String sesso) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
        this.medico_curante = "";
        this.età = 0;
        this.indirizzo = "";
        this.sesso = sesso;
        sb.append("P");
        sb.append(rand.nextInt(1000));
        this.id = sb.toString();
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

    public int getEtà(){
        return età;
    }

    public String getIndirizzo(){
        return indirizzo;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getMedico_curante(){
        return medico_curante;
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

    public void setEtà(int età){
        this.età = età;
    }

    public void setIndirizzo(String indirizzo){
        this.indirizzo = indirizzo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setMedicoCurante(String medico_curante){
        this.medico_curante = medico_curante;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }
}
