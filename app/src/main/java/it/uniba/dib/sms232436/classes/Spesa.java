package it.uniba.dib.sms232436.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Spesa {
    private String CodiceSpesa;
    private String CodiceUtente;
    private String DataSpesa;
    private String Importo;
    private int Quantità;
    private String NomeProdotto;
    private String Categoria;

    public Spesa() {}
    public Spesa(String codiceSpesa, String codiceUtente, String dataSpesa, String importo, int quantità, String nomeProdotto, String categoria) {
        CodiceSpesa = codiceSpesa;
        CodiceUtente = codiceUtente;
        DataSpesa = dataSpesa;
        Importo = importo;
        Quantità = quantità;
        NomeProdotto = nomeProdotto;
        Categoria = categoria;
    }

    public String getCodiceSpesa() {
        return CodiceSpesa;
    }

    public void setCodiceSpesa(String codiceSpesa) {
        CodiceSpesa = codiceSpesa;
    }

    public String getCodiceUtente() {
        return CodiceUtente;
    }

    public void setCodiceUtente(String codiceUtente) {
        this.CodiceUtente = codiceUtente;
    }

    public String getDataSpesa() {
        return DataSpesa;
    }

    public void setDataSpesa(String dataSpesa) {
        DataSpesa = dataSpesa;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getImporto() {
        return Importo;
    }

    public void setImporto(String importo) {
        Importo = importo;
    }

    public String getNomeProdotto() {
        return NomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        NomeProdotto = nomeProdotto;
    }

    public int getQuantità() {
        return Quantità;
    }

    public void setQuantità(int quantità) {
        Quantità = quantità;
    }

    public String convertDatetoString(Date dataSpesa){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(dataSpesa);
    }

    public Date convertStringtoDate(String dataSpesa) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.parse(dataSpesa);
    }
}
