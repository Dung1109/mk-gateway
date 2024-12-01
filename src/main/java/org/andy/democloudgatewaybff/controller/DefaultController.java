package org.andy.democloudgatewaybff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @Value("${app.base-uri}")
    private String appBaseUri;

    @GetMapping("/")
    public String root() {
        return "redirect:" + this.appBaseUri;
    }

    // '/authorized' is the registered 'redirect_uri' for authorization_code
    @GetMapping("/authorized")
    public String authorized() {
        return "redirect:" + this.appBaseUri;
    }

}
