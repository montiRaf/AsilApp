package it.uniba.dib.sms232436.classes;

public class Vaccinazione {
    private String idVaccinazione;
    private String vaccino;
    private String data;
    private String idPaziente;
    private String richiamo;
    private String nomeDottore;

    // Costruttore vuoto richiesto da Firebase
    public Vaccinazione() {
    }

    // Costruttore con parametri
    public Vaccinazione(String idPaziente, String vaccino, String data, String nomeDottore, String richiamo) {
        this.vaccino = vaccino;
        this.data = data;
        this.idPaziente = idPaziente;
        this.richiamo = richiamo;
        this.nomeDottore = nomeDottore;
    }

    public String getIdVaccinazione() {
        return idVaccinazione;
    }

    public void setIdVaccinazione(String idVaccinazione) {
        this.idVaccinazione = idVaccinazione;
    }

    public String getVaccino() {
        return vaccino;
    }

    public void setVaccino(String vaccino) {
        this.vaccino = vaccino;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIdPaziente() {
        return idPaziente;
    }

    public void setIdPaziente(String idPaziente) {
        this.idPaziente = idPaziente;
    }

    public String getRichiamo() {
        return richiamo;
    }

    public void setRichiamo(String richiamo) {
        this.richiamo = richiamo;
    }

    public String getNomeDottore() {
        return nomeDottore;
    }

    public void setNomeDottore(String nomeDottore) {
        this.nomeDottore = nomeDottore;
    }
}
