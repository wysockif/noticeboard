package pl.wysockif.noticeboard.controllers.token;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.services.token.VerificationTokenService;

import java.util.logging.Logger;

@RestController
@RequestMapping(("/api/1.0"))
public class VerificationTokenController {

    private final VerificationTokenService tokenService;
    private final Logger LOGGER = Logger.getLogger(VerificationTokenController.class.getName());


    public VerificationTokenController(VerificationTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/tokens/{token}")
    public void verifyToken(@PathVariable String token) {
        LOGGER.info("Request VerifyToken started (token: " + token + " )");
        tokenService.verifyToken(token);
        LOGGER.info("Request VerifyToken finished (token: " + token + " )");
    }
}
