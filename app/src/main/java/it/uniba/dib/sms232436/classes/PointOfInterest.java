package it.uniba.dib.sms232436.classes;

public class PointOfInterest {

    private String codiceStrutturaVicina;
    private String nomeStruttura;
    private String latitudine;
    private String longitudine;
    private String categoria;

    public PointOfInterest(String codiceStrutturaVicina, String nomeStruttura, String latitudine, String longitudine, String categoria) {
        this.codiceStrutturaVicina = codiceStrutturaVicina;
        this.nomeStruttura = nomeStruttura;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.categoria = categoria;
    }

    public String getCodiceStrutturaVicina() {
        return codiceStrutturaVicina;
    }

    public void setCodiceStrutturaVicina(String codiceStrutturaVicina) {
        this.codiceStrutturaVicina = codiceStrutturaVicina;
    }

    public String getNomeStruttura() {
        return nomeStruttura;
    }

    public void setNomeStruttura(String nomeStruttura) {
        this.nomeStruttura = nomeStruttura;
    }

    public String getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(String latitudine) {
        this.latitudine = latitudine;
    }

    public String getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(String longitudine) {
        this.longitudine = longitudine;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
