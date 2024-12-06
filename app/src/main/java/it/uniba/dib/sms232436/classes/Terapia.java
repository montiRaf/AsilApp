package it.uniba.dib.sms232436.classes;

public class Terapia {

    // i campi nome e cognome del dottore devono essere avvalorati nel momento in cui il dottore fa il login
    private String id;
    private String nomeFarmaco;
    private String diagnosi;
    private String posologia;   // numero e orario di assunzione
    private String dosaggio;
    private String nomeDottore;
    private String cognomeDottore;
    private String dataInizio;
    private String dataFine;
    private String idPaziente;

    // i campi nome e cognome del paziente devono essere prelevati dal DB, utilizzando l'id del paziente
    private String nomePaziente;
    private String cognomePaziente;

    public Terapia() {
        // Costruttore vuoto necessario per Firebase
    }

    public Terapia(String id, String farmaco, String diagnosi, String posologia, String dosaggioFarmaco, String nomeDottore, String cognomeDottore, String dataInizio, String dataFine, String idPaziente, String nomePaziente, String cognomePaziente) {
        this.id = id;
        this.nomeFarmaco = farmaco;
        this.diagnosi = diagnosi;
        this.posologia = posologia;
        this.dosaggio = dosaggioFarmaco;
        this.nomeDottore = nomeDottore;
        this.cognomeDottore = cognomeDottore;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.idPaziente = idPaziente;
        this.nomePaziente = nomePaziente;
        this.cognomePaziente = cognomePaziente;
    }

    // metodi getter e setter per gestire lo stato
    public String getIdPaziente() {
        return idPaziente;
    }

    public void setIdPaziente(String idPaziente) {
        this.idPaziente = idPaziente;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getNomeFarmaco() { return nomeFarmaco; }

    public void setNomeFarmaco(String nomeFarmaco) { this.nomeFarmaco = nomeFarmaco; }

    public String getDiagnosi() { return diagnosi; }

    public void setDiagnosi(String diagnosi) { this.diagnosi = diagnosi; }

    public String getDosaggio() { return dosaggio; }

    public void setDosaggio(String dosaggio) { this.dosaggio = dosaggio; }

    public String getNomeDottore() { return nomeDottore; }

    public void setNomeDottore(String nomeDottore) { this.nomeDottore = nomeDottore; }

    public String getCognomeDottore() { return cognomeDottore; }

    public void setCognomeDottore(String cognomeDottore) { this.cognomeDottore = cognomeDottore; }

    public String getDataInizio() { return dataInizio; }

    public void setDataInizio(String dataInizio) { this.dataInizio = dataInizio; }

    public String getDataFine() { return dataFine; }

    public void setDataFine(String dataFine) { this.dataFine = dataFine; }

    public String getNomePaziente() { return nomePaziente; }

    public void setNomePaziente(String nomePaziente) { this.nomePaziente = nomePaziente; }

    public String getCognomePaziente() { return cognomePaziente; }

    public void setCognomePaziente(String cognomePaziente) { this.cognomePaziente = cognomePaziente; }

    public String getPosologia() { return posologia; }

    public void setPosologia(String posologia) { this.posologia = posologia; }

}
