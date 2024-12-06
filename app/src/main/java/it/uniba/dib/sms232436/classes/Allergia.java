package it.uniba.dib.sms232436.classes;

public class Allergia {
    private String nomeAllergia;
    private String nomePaziente;
    private String idPaziente;

    public Allergia(){ }

    public Allergia(String nomeAllergia, String nomePaziente, String idPaziente) {
        this.nomeAllergia = nomeAllergia;
        this.nomePaziente = nomePaziente;
        this.idPaziente = idPaziente;
    }

    public String getNomeAllergia() {
        return nomeAllergia;
    }

    public void setNomeAllergia(String nomeAllergia) {
        this.nomeAllergia = nomeAllergia;
    }

    public String getNomePaziente() {
        return nomePaziente;
    }

    public void setNomePaziente(String nomePaziente) {
        this.nomePaziente = nomePaziente;
    }

    public String getIdPaziente() {
        return idPaziente;
    }

    public void setIdPaziente(String idPaziente) {
        this.idPaziente = idPaziente;
    }
}
