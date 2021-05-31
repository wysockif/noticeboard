package pl.wysockif.noticeboard.services.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class MailService {

    private final Logger LOGGER = Logger.getLogger(MailService.class.getName());

    private static final Executor executor = Executors.newFixedThreadPool(10);

    private final JavaMailSender javaMailSender;


    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String to, String userName, String url) throws MessagingException, UnsupportedEncodingException {
        LOGGER.info("Sending email to: " + to);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        createMail(to, userName, url, mimeMessageHelper);
        Runnable task = () -> {
            try {
                javaMailSender.send(mimeMessage);
                LOGGER.info("Sent email to: " + to);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.warning("Cannot send email to: " + to);
            }
        };
        executor.execute(task);
    }

    private void createMail(String to, String userName, String url, MimeMessageHelper mimeMessageHelper) throws MessagingException, UnsupportedEncodingException {
        mimeMessageHelper.setFrom("no-reply@noticeboard.pl", "Noticeboard");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("Zweryfikuj adres email");
        mimeMessageHelper.setText(getHtmlContent(userName, url), true);
    }

    private String getHtmlContent(String name, String url) {
        return "<!doctype html>\n" +
                "<html lang=\"pl\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <style>\n" +
                "        #lnk {\n" +
                "            background-color: #b78e56;\n" +
                "            padding-left: 10px;\n" +
                "            padding-right: 10px;\n" +
                "            padding-top: 5px;\n" +
                "            padding-bottom: 5px;\n" +
                "            color: white;\n" +
                "            border-radius: 5px;\n" +
                "            cursor: pointer;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        #lnk:hover {\n" +
                "            background-color: #b84;\n" +
                "        }\n" +
                "\n" +
                "        .card {\n" +
                "            max-width: 500px;\n" +
                "            margin: auto;\n" +
                "            margin-top: 10px;\n" +
                "            margin-bottom: 10px;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0 2px 4px 0 lightgray;\n" +
                "            transition: 0.3s;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            padding: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .muted-note {\n" +
                "            color: gray;\n" +
                "        }\n" +
                "\n" +
                "        .signature {\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .bttn {\n" +
                "            margin-top: 20px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "\n" +
                "        #header {\n" +
                "            margin-top: 10px;\n" +
                "            font-size: larger;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div style=\"text-align: center\">\n" +
                "        <div class=\"card\">\n" +
                "            <div class=\"container\">\n" +
                "                <div id=\"header\">Witaj " + name + ",</div>\n" +
                "                <p>zweryfikuj swoje konto, klikając poniższy przycisk:</p>\n" +
                "                <div class=\"bttn\">\n" +
                "                    <a href=\"" + url + "\" target=\"_blank\" id=\"lnk\">Potwierdź adres e-mail</a>\n" +
                "                </div>\n" +
                "                <div class=\"signature\">\n" +
                "                    Noticeboard.pl\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <small class=\"muted-note\">Ta wiadomość została wysłana automatycznie. <br />\n" +
                "            Nie odpowiadaj na tego e-maila.</small>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }
}
