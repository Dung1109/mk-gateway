package org.andy.democloudgatewaybff.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${app.auth.post-login-redirect}")
    private String postLoginRedirect;

    @GetMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var cookies = request.getCookies();

        if(cookies != null) {
            log.debug("=====> cookies: {}", Arrays.asList(cookies));
        }

        log.info("Redirecting to: {}", postLoginRedirect);
        response.sendRedirect(postLoginRedirect);
    }

    @GetMapping("/username")
    public Map<String, String> username(Authentication authentication) {
        String username = authentication.getName();
        log.info("username: {}",authentication.getAuthorities());
        return Map.of("username", username);
    }

    // (1)
    @GetMapping("/profile")
    public Map<String, Object> idToken(@AuthenticationPrincipal OidcUser oidcUser) {
        log.info("oidcUser: {}", oidcUser);
        log.info("id token: {}", oidcUser.getIdToken().getTokenValue());

        if(oidcUser == null) {
            return Map.of("error", "No id_token found", "id_token", null);

        } else {
            return oidcUser.getClaims();
        }
    }


}
