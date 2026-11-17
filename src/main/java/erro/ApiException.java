package erro;

/** Erro de regra de negócio com o status HTTP a devolver; vira JSON {"mensagem": ...}. */
public class ApiException extends RuntimeException {
    private final int status;

    public ApiException(int status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public int status() {
        return status;
    }

    public static ApiException naoEncontrado(String mensagem) {
        return new ApiException(404, mensagem);
    }

    public static ApiException invalido(String mensagem) {
        return new ApiException(400, mensagem);
    }

    public static ApiException conflito(String mensagem) {
        return new ApiException(409, mensagem);
    }

    public static ApiException naoAutorizado(String mensagem) {
        return new ApiException(401, mensagem);
    }
}
