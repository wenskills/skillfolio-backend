package app.service;

import app.model.Person;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(Person p, String tempPassword, String resetToken) {

        String loginLink = "http://localhost:5173/frontend#login";
        String resetLink = "http://localhost:5173/frontend#reset-password?token"
                +"="+ resetToken;
        System.out.println(resetToken);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(p.getEmail());
            helper.setSubject("Bienvenue sur CVamu !");

            String text =
                    "Bonjour " + p.getFirstName() + ", "  +
                            "Un compte a ete cree pour vous." +
                            "Voici vos identifiants provisoires." +
                            "Email : " + p.getEmail() +
                            "Mot de passe :" + tempPassword +
                            "Vous pouvez vous connecter des maintenant en cliquant sur le lien " + loginLink +". "+
                            "Pour des raisons de securites, il est recommandé de reinitialiser ce mot de passe en cliquant sur le lien " + resetLink + ". " +
                            "Cordialement,";

            helper.setText(text, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi du mail", e);
        }
    }
}
