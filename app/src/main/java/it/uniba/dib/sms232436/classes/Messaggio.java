package it.uniba.dib.sms232436.classes;

import java.io.Serializable;

public class Messaggio implements Serializable{
    private String idMessaggio;
    private String idPaziente;
    private String messaggio;
    private String nomeMedico;
    private String cognomeMedico;
    private String dataInvio;
    private boolean letto;  // Campo che indica se il messaggio è stato letto o meno

    public Messaggio() {
        // Costruttore vuoto richiesto per Firebase
    }

    public Messaggio(String idPaziente, String testoMessaggio, String nomeMedico, String cognomeMedico, boolean letto, String dataInvio) {
        this.idPaziente = idPaziente;
        this.messaggio = testoMessaggio;
        this.nomeMedico = nomeMedico;
        this.cognomeMedico = cognomeMedico;
        this.letto = letto;
        this.dataInvio = dataInvio;
    }

    public String getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(String dataInvio) {
        this.dataInvio = dataInvio;
    }

    public String getIdMessaggio() {
        return idMessaggio;
    }

    public void setIdMessaggio(String idMessaggio) {
        this.idMessaggio = idMessaggio;
    }

    // Getter e Setter per il campo 'letto'
    public boolean isLetto() {
        return letto;
    }

    public void setLetto(boolean letto) {
        this.letto = letto;
    }

    // Getters e Setters
    public String getIdPaziente() {
        return idPaziente;
    }

    public void setIdPaziente(String idPaziente) {
        this.idPaziente = idPaziente;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getNomeMedico() {
        return nomeMedico;
    }

    public void setNomeMedico(String nomeMedico) {
        this.nomeMedico = nomeMedico;
    }

    public String getCognomeMedico() {
        return cognomeMedico;
    }

    public void setCognomeMedico(String cognomeMedico) {
        this.cognomeMedico = cognomeMedico;
    }
}
