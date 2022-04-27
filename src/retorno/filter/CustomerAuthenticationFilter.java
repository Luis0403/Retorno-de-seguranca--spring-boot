package com.algamoney.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.algamoney.cookie.RefreshTokenPostProcessor;
import com.algamoney.model.Roles;
import com.algamoney.services.UserServices;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CustomerAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    public String tokenRefresh;
    private AuthenticationManager authenticationManager;

    public CustomerAuthenticationFilter() {
        // TODO Auto-generated constructor stub
    }

    public CustomerAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager=authenticationManager;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username, password);
        System.err.println("Sendo Autenticado: "+username);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        User user=(User)authResult.getPrincipal();
        Algorithm algorithm=Algorithm.HMAC256("secrete".getBytes());
        String access_token= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+1*60))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        System.err.println("The access_token: "+access_token);
        String refresh_token= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        //chamada da classe que trata o cookie
        RefreshTokenPostProcessor rf=new RefreshTokenPostProcessor();
        //chamada do metdo para para adionar o cookie ao browser
        //rf.adicionarRefreshTokenCookie(response, request, refresh_token,"/api/login");

        Map<String,String> tokensMap=new HashMap<>();
        tokensMap.put("access_token", access_token);
        tokensMap.put("refresh_token", refresh_token);
        tokensMap.put("O token expira aos: ", new Date(System.currentTimeMillis()+30*3600*1000)+"");
        response.setContentType("aplication/json");
        new ObjectMapper().writeValue(response.getOutputStream(), tokensMap);




    }



    public void refreshToken(HttpServletRequest request, HttpServletResponse response, UserServices userServices) throws StreamWriteException, DatabindException, IOException {
        String authorizationHeader=request.getHeader(AUTHORIZATION);

        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer")){
            try {
                String token=authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm=Algorithm.HMAC256("secrete".getBytes());
                JWTVerifier verifier=JWT.require(algorithm).build();
                DecodedJWT decodedJWT=verifier.verify(token);
                String username=decodedJWT.getSubject();
                com.algamoney.model.User user=userServices.getUser(username);
                String refresh_token= JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis()+1*60*10))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",user.getRoles().stream().map(Roles::getName).collect(Collectors.toList()))
                        .sign(algorithm);


                Map<String,String> tokensMap=new HashMap<>();
                tokensMap.put("refresh_token", refresh_token);
                tokensMap.put("O token expira aos: ", new Date(System.currentTimeMillis()+1*60*10)+"");
                response.setContentType("aplication/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokensMap);
            } catch (Exception e) {
                // TODO: handle exception
                response.setHeader("error: ",e.getMessage());
                Map<String,String> erros=new HashMap<>();
                erros.put("error_message: ",e.getMessage());
                new ObjectMapper().writeValue(response.getOutputStream(), erros);
            }
        }

        else {
            throw new NullPointerException();
        }
    }


}

