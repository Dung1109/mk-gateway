package org.andy.democloudgatewaybff.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@Slf4j
public class LogoutController {

    @Value("${app.auth.post-logout-redirect}")
    private String postLogoutRedirect;

    @GetMapping("/logged-out")
    public void loggedOut(HttpServletResponse response) throws IOException {
        log.info("logged-out");
        log.info("postLogoutRedirect: {}", postLogoutRedirect);
        response.sendRedirect(postLogoutRedirect);
    }
}
