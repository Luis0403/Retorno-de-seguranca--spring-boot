package com.algamoney.resource;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import com.algamoney.cookie.RefreshTokenPostProcessor;
//import com.algamoney.filter.CustomerAuthenticationFilter;
import com.algamoney.model.Roles;
import com.algamoney.model.User;
import com.algamoney.services.UserServices;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;


@RestController
@RequestMapping("/api")
public class UserResource {

    @Autowired
    private UserServices userServices;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok().body(userServices.getusers());
    }


    @PostMapping("/user/save")
    public ResponseEntity<?> saveUsers(@RequestBody User user){
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userServices.saveUser(user));
    }

    @PostMapping("/role/save")
    public ResponseEntity<?> saveUrs(@RequestBody Roles roles){
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userServices.saveRoles(roles));
    }

    @PostMapping("/addRoleToUser/save")
    public ResponseEntity<?> saveRole(@RequestBody User user){
        userServices.addRolesToUser(user.getUsername(),user.getPassword());
        return ResponseEntity.ok().build();
    }

//	@GetMapping("/refreshToken")
//	public void refreshTokens(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException{
//		System.err.println("\npoooo=================\n");
//
//
//		CustomerAuthenticationFilter caf=new CustomerAuthenticationFilter();
//		caf.refreshToken(request, response,userServices);
//	}
//



}

