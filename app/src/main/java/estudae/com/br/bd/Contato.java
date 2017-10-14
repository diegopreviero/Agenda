package estudae.com.br.bd;

import java.io.Serializable;

public class Contato implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    public String foto;
    public String nome;
    public String endereco;
    public String telefone;
    public Long dataNascimento;
    public String email;
    public String site;

    @Override
    public String toString() {
        return this.nome;
    }
}