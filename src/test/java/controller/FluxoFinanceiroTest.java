package controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/** Fluxo completo pela API: cadastro, contas, categorias, transações, transferência e saldo. */
@QuarkusTest
class FluxoFinanceiroTest {

    /** Registra um usuário novo e devolve o JWT que veio no cookie httpOnly. */
    private String registrar(String email) {
        return given().contentType(ContentType.JSON)
                .body(Map.of("nome", "Teste", "email", email, "senha", "segredo123"))
                .post("/api/auth/registrar")
                .then().statusCode(201)
                .extract().cookie("finance_token");
    }

    private int criar(String token, String recurso, Object corpo) {
        return given().cookie("finance_token", token).contentType(ContentType.JSON).body(corpo)
                .post("/api/" + recurso)
                .then().statusCode(201)
                .extract().path("id");
    }

    @Test
    void semLoginNaoAcessa() {
        given().get("/api/conta").then().statusCode(401);
    }

    @Test
    void loginComSenhaErradaFalha() {
        registrar("senha@teste.com");
        given().contentType(ContentType.JSON)
                .body(Map.of("email", "senha@teste.com", "senha", "errada"))
                .post("/api/auth/login")
                .then().statusCode(401);
    }

    @Test
    void fluxoCompleto() {
        String token = registrar("fluxo@teste.com");

        int carteira = criar(token, "conta", Map.of("descricao", "Carteira"));
        int banco = criar(token, "conta", Map.of("descricao", "Banco"));
        int salario = criar(token, "categoria", Map.of("descricao", "Salário"));
        int mercado = criar(token, "categoria", Map.of("descricao", "Mercado"));

        criar(token, "transacao", Map.of("descricao", "Salário de novembro", "valor", 3000, "tipo", "R",
                "dataTransacao", "2026-11-05", "conta", Map.of("id", banco), "categoria", Map.of("id", salario)));
        criar(token, "transacao", Map.of("descricao", "Compras", "valor", 250.50, "tipo", "D",
                "dataTransacao", "2026-11-06", "conta", Map.of("id", banco), "categoria", Map.of("id", mercado)));

        // Transferência: sai 500 do Banco e entra 500 na Carteira.
        given().cookie("finance_token", token).contentType(ContentType.JSON)
                .body(Map.of("contaOrigemId", banco, "contaDestinoId", carteira, "valor", 500, "data", "2026-11-07"))
                .post("/api/transferencia")
                .then().statusCode(201);

        // Filtros
        given().cookie("finance_token", token).queryParam("contaId", banco)
                .get("/api/transacao").then().statusCode(200).body("$", hasSize(3));
        given().cookie("finance_token", token).queryParam("categoriaId", mercado)
                .get("/api/transacao").then().statusCode(200).body("$", hasSize(1));

        // Saldos: Banco = 3000 - 250,50 - 500; Carteira = 500; geral = 2749,50
        given().cookie("finance_token", token).get("/api/saldo")
                .then().statusCode(200)
                .body("contas.find { it.descricao == 'Banco' }.saldo", equalTo(2249.50f))
                .body("contas.find { it.descricao == 'Carteira' }.saldo", equalTo(500.00f))
                .body("total", equalTo(2749.50f));
    }

    @Test
    void usuarioNaoVeContaDeOutro() {
        String tokenA = registrar("a@teste.com");
        String tokenB = registrar("b@teste.com");
        int contaDoA = criar(tokenA, "conta", Map.of("descricao", "Conta do A"));

        given().cookie("finance_token", tokenB).get("/api/conta/" + contaDoA).then().statusCode(404);
        given().cookie("finance_token", tokenB).get("/api/conta").then().body("$", hasSize(0));
    }
}
