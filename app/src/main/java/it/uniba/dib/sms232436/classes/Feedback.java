package it.uniba.dib.sms232436.classes;

import java.io.Serializable;

public class Feedback implements Serializable {
    private String codiceUtente;
    private String tipo;
    private String descrizione;

    public Feedback(String codiceUtente, String tipo, String descrizione){
        this.codiceUtente = codiceUtente;
        this.tipo = tipo;
        this.descrizione = descrizione;
    }

    public Feedback(String codiceUtente, String tipo){
        this.codiceUtente = codiceUtente;
        this.tipo = tipo;
    }

    public Feedback(){

    }

    public String getCodiceUtente(){
        return codiceUtente;
    }

    public String getTipo(){
        return tipo;
    }
    public String getDescrizione() { return descrizione;}

    public String descrizione(){
        return descrizione;
    }

    public void set_codiceUtente(String codiceUtente){
        this.codiceUtente = codiceUtente;
    }

    public void set_tipo(String tipo){
        this.tipo = tipo;
    }

    public void set_descrizione(String descrizione){
        this.descrizione = descrizione;
    }
}