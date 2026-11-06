package br.ifsp.demo.security;

import br.ifsp.demo.domain.PapelUsuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    private final Key chave;
    private final long expiracaoEmMilissegundos;

    public JwtService(@Value("${jwt.secret}") String segredo,
                      @Value("${jwt.expiration-ms}") long expiracaoEmMilissegundos) {
        this.chave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(segredo));
        this.expiracaoEmMilissegundos = expiracaoEmMilissegundos;
    }

    public String gerarToken(String email) {
        return gerarToken(email, PapelUsuario.CLIENTE);
    }

    public String gerarToken(String email, PapelUsuario papel) {
        Date agora = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("papel", papel.name())
                .setIssuedAt(agora)
                .setExpiration(new Date(agora.getTime() + expiracaoEmMilissegundos))
                .signWith(chave, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(chave)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public PapelUsuario extrairPapel(String token) {
        String papel = Jwts.parserBuilder()
                .setSigningKey(chave)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("papel", String.class);
        return papel == null ? PapelUsuario.CLIENTE : PapelUsuario.valueOf(papel);
    }
}
