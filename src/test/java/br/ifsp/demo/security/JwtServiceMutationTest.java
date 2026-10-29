package br.ifsp.demo.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Mutation")
class JwtServiceMutationTest {

    private static final String SEGREDO = "t35dYPALrh8tCDlqQR0waxWC2/h49TsqV7RyeFSo8aGbFtpRf4Ps0FSvyqrNRzbD";
    private static final String OUTRO_SEGREDO = "XI2Sh8kIV2F2ehrn8TTb4KpkCgDCiRAy7FE6zpEtSV/M5/0CosMVINCih2etseaa";
    private static final long UMA_HORA = 3_600_000L;

    @Test
    @DisplayName("Gera token que permite recuperar o e-mail do usuário")
    void geraTokenComEmailDoUsuario() {
        JwtService service = new JwtService(SEGREDO, UMA_HORA);

        String token = service.gerarToken("ana@email.com");

        assertThat(service.extrairEmail(token)).isEqualTo("ana@email.com");
    }

    @Test
    @DisplayName("Rejeita token assinado com outra chave")
    void rejeitaTokenAssinadoComOutraChave() {
        JwtService service = new JwtService(SEGREDO, UMA_HORA);
        String tokenDeOutraChave = new JwtService(OUTRO_SEGREDO, UMA_HORA).gerarToken("ana@email.com");

        assertThatThrownBy(() -> service.extrairEmail(tokenDeOutraChave))
                .isInstanceOf(JwtException.class);
    }
}