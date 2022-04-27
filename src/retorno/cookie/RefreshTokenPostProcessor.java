package com.algamoney.cookie;
import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.algamoney.filter.CustomerAuthenticationFilter;
import com.algamoney.services.UserServices;

import org.apache.catalina.util.ParameterMap;

@Service
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RefreshTokenPostProcessor implements Filter {
    @Autowired
    private UserServices userServices;

    public void adicionarRefreshTokenCookie(HttpServletResponse resp, HttpServletRequest req, String tokenRefresh, String url) {
        Cookie cookieToken= new Cookie("tokenRefresh", tokenRefresh);
        cookieToken.setHttpOnly(true);
        cookieToken.setSecure(false); //trocar para true para quando estiver em producao
        cookieToken.setPath("/api/login");
        cookieToken.setMaxAge(3600*24*30);
        resp.addCookie(cookieToken);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request2=(HttpServletRequest) request;
        if("/api/refreshToken".equals(request2.getRequestURI())) {
            for(Cookie cookie:request2.getCookies()) {
                if(cookie.getName().equalsIgnoreCase("tokenRefresh")) {
                    String tokenRefresh=cookie.getValue();
                    request2= new MyServerletRequestWraper(request2,tokenRefresh);
                    request2.getParameterMap();
                    CustomerAuthenticationFilter caf=new CustomerAuthenticationFilter();
                    System.err.println("O TOKEN: "+tokenRefresh);
                    caf.refreshToken(request2, (HttpServletResponse)response,userServices);
                }
                else {
                    throw new NullPointerException();
                }
            }
        }


    }

    static class MyServerletRequestWraper extends HttpServletRequestWrapper {
        private  String tokenRefresh;

        public MyServerletRequestWraper(HttpServletRequest request, String tokenRefresh) {
            super(request);
            this.tokenRefresh=tokenRefresh;
            System.err.println("hala a vida construtor..");
        }

        @Override
        public Map<String, String[]> getParameterMap() {

            ParameterMap<String, String []> map=new ParameterMap<>(getRequest().getParameterMap());
            map.put("tokenRefresh", new String[] { "Bearer "+tokenRefresh});
            map.setLocked(true);
            return map;
        }

    }


}








