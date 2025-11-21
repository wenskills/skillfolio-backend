package app.service;

import app.model.Person;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/************
 * SYSTEME D'ENVOI DE MAIL AUTOMATIQUE
 * principalement lors de la cooptation
 * => envoi des identifiants et liens pour se connecter et réinitialisation du mdp
 * ***********/
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(Person p, String tempPassword, String resetToken) {

        String loginLink = "http://localhost:5173/frontend#login";
        String resetLink = "http://localhost:5173/frontend#reset-password?token"
                +"="+ resetToken;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(p.getEmail());
            helper.setSubject("Bienvenue sur CVamu !");

            String text =
                    "Bonjour " + p.getFirstName() + ", \n\n " +
                            "Un compte a été créé pour vous.\n\n " +
                            "Voici vos identifiants provisoires :\n " +
                            "- Email : " + p.getEmail() + "\n " +
                            "- Mot de passe : " + tempPassword + "\n\n " +
                            "Connectez-vous dès maintenant : " + loginLink + "\n " +
                            "Pour des raisons de securités, nous vous conseillons de modifier votre mot de passe : " + resetLink + "\n\n" +
                            "Cordialement,\n" +
                            "L'équipe CVamu";


            helper.setText(text, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("L'email n'a pas pu être envoyé. La personne n'a pas pu être créée. Veuillez réessayer ultérieurement.");

        }
    }
}

