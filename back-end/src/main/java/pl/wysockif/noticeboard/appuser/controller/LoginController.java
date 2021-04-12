package pl.wysockif.noticeboard.appuser.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1.0")
public class LoginController {

    @PostMapping("/login")
    public void login() {

    }
}
