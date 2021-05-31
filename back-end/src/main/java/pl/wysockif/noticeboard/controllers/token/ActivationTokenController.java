package pl.wysockif.noticeboard.controllers.token;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.services.token.ActivationTokenService;

import java.util.logging.Logger;

@RestController
@RequestMapping(("/api/1.0"))
public class ActivationTokenController {

    private final ActivationTokenService tokenService;
    private final Logger LOGGER = Logger.getLogger(ActivationTokenController.class.getName());


    public ActivationTokenController(ActivationTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/tokens/{token}")
    @CrossOrigin(origins = "https://noticeboard.pl")
    public void verifyToken(@PathVariable String token) {
        LOGGER.info("Request verifyToken started (token: " + token + " )");
        tokenService.verifyToken(token);
        LOGGER.info("Request verifyToken finished (token: " + token + " )");
    }
}
