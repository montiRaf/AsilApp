package it.uniba.dib.sms232436.classes;

public class Misurazione {
    private String id_paziente;
    private String strumento;
    private double valore;
    private String data_ora; // Nuovo campo per la data e l'ora della misurazione

    // Costruttore vuoto richiesto per Firebase
    public Misurazione() {}

    // Costruttore con tutti i parametri
    public Misurazione(String id_paziente, String strumento, double valore, String dataOra) {
        this.id_paziente = id_paziente;
        this.strumento = strumento;
        this.valore = valore;
        this.data_ora = dataOra; // Imposta data e ora
    }

    // Getter e Setter per id_paziente
    public String getId_paziente() {
        return id_paziente;
    }

    public void setId_paziente(String id_paziente) {
        this.id_paziente = id_paziente;
    }

    // Getter e Setter per strumento
    public String getStrumento() {
        return strumento;
    }

    public void setStrumento(String strumento) {
        this.strumento = strumento;
    }

    // Getter e Setter per valore
    public double getValore() {
        return valore;
    }

    public void setValore(double valore) {
        this.valore = valore;
    }

    public void setValoreString(String valore) {
        this.valore = Double.parseDouble(valore);
    }

    // Getter e Setter per dataOra
    public String getData_ora() {
        return data_ora;
    }

    public void setData_ora(String data_ora) {
        this.data_ora = data_ora;
    }
}


