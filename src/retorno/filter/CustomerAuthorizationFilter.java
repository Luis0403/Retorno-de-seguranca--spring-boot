package com.algamoney.filter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class CustomerAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if(request.getServletPath().equals("/api/login")){
            //|| request.getServletPath().equals("/api/refreshToken")
            System.err.println("Classe de autorizacao\nCom o username: "+request.getParameter("username"));
            filterChain.doFilter(request, response);
        }
        else {
            String authorizationHeader=request.getHeader(AUTHORIZATION);

            if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer")){
                try {
                    String token=authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm=Algorithm.HMAC256("secrete".getBytes());
                    JWTVerifier verifier=JWT.require(algorithm).build();
                    DecodedJWT decodedJWT=verifier.verify(token);
                    String username=decodedJWT.getSubject();
                    String [] roles=decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
                    for (String regras: roles) {
                        authorities.add(new SimpleGrantedAuthority(regras));
                    }

                    UsernamePasswordAuthenticationToken authenticationToken=
                            new UsernamePasswordAuthenticationToken(username,null,authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    // TODO: handle exception
                    response.setHeader("error: ",e.getMessage());
                    Map<String,String> erros=new HashMap<>();
                    erros.put("error_message: ",e.getMessage());
                    new ObjectMapper().writeValue(response.getOutputStream(), erros);
                }
            }

            else {
                filterChain.doFilter(request, response);
            }

        }


    }




}

