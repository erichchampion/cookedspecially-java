/**
 * 
 */
package com.cookedspecially.utility;

import com.cookedspecially.domain.Restaurant;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

//import org.thymeleaf.spring3.SpringTemplateEngine;

/**
 * @author sagarwal
 *
 */
public class MailerUtility {

	public final static String username = "support@cookedspecially.com";
	public final static String password = "9b*VMdr*";
	final static MailSender mailSender = new JavaMailSenderImpl();
	final static Properties props = new Properties();
    private static JavaMailSenderImpl mailSenderNew = new JavaMailSenderImpl();

	static {
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

	}

    private SpringTemplateEngine templateEngine = new SpringTemplateEngine();

    public static void sendMail(String toAddress, String subject, String messageStr) {
		 
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,	InternetAddress.parse(toAddress));
			message.setSubject(subject);
			message.setText(messageStr);
 
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void sendHTMLMail(String toAddress, String subject, String messageStr,final String mailUsername , final String mailpassword) {
		
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUsername,mailpassword);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(mailUsername));
			message.setRecipients(Message.RecipientType.TO,	InternetAddress.parse(toAddress));
			message.setSubject(subject);
			message.setContent(messageStr, "text/html");
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void sendMailWithInline(JavaMailSenderImpl mailSender,
			SpringTemplateEngine templateEngine,
	        final String recipientName, final String recipientEmail, final Locale locale)
	        throws MessagingException {
	  
	    // Prepare the evaluation context
	    final Context ctx = new Context(locale);
	    ctx.setVariable("name", recipientName);
	    ctx.setVariable("subscriptionDate", new Date());
	    ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
	    //ctx.setVariable("imageResourceName", imageResourceName); // so that we can reference it from HTML
	 
	    // Prepare message using a Spring helper
	    final MimeMessage mimeMessage = mailSender.createMimeMessage();
	    final MimeMessageHelper message =
	        new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

	    message.setSubject("Example HTML email with inline image");
	    message.setFrom("thymeleaf@example.com");
	    message.setTo(recipientEmail);
	 
	    // Create the HTML body using Thymeleaf
	    final String htmlContent = templateEngine.process("email-inlineimage.html", ctx);
	    message.setText(htmlContent, true); // true = isHtml
	 
	    // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
	    //final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
	    //message.addInline(imageResourceName, imageSource, imageContentType);
	 
	    // Send mail
	    mailSender.send(mimeMessage);
	 
	}

    public static void emailToCustomer(SpringTemplateEngine templateEngine, Restaurant org, String emailMessage, String subject, List<String> customerList) throws MessagingException, UnsupportedEncodingException {

		    final Context ctx = new Context();
		    ctx.setVariable("exceptionLogs", emailMessage);
			for(String email : customerList){
			    final MimeMessage mimeMessage = mailSenderNew.createMimeMessage();
			    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart
	
			        message.setSubject(subject);
			    	message.setFrom(org.getMailUsername());
			    	message.setReplyTo(org.getMailUsername());
			    	message.setTo(email);
			    final String htmlContent = templateEngine.process("exceptionExpress", ctx);
			    	message.setText(htmlContent, true);
			    	mailSenderNew.setUsername(org.getMailUsername());
			    	mailSenderNew.setPassword(org.getMailPassword());
			    	mailSenderNew.setHost(org.getMailHost());
			    	mailSenderNew.setProtocol(org.getMailProtocol());
			    	mailSenderNew.setPort(org.getMailPort());
			    	Properties prop =  new Properties();
			    	prop.setProperty("mail.smtp.starttls.enable","true");
			    	mailSenderNew.setJavaMailProperties(prop);
	
			    mailSenderNew.send(mimeMessage);
	    }
	   }

    public static void emailToCustomer(Restaurant org, String emailMessage, String subject, List<String> customerList) throws MessagingException, UnsupportedEncodingException {
        for (String email : customerList) {
            final MimeMessage mimeMessage = mailSenderNew.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8"); // true = multipart

            message.setSubject(subject);
            message.setFrom(org.getMailUsername());
            message.setReplyTo(org.getMailUsername());
            message.setTo(email);
            mailSenderNew.setUsername(org.getMailUsername());
            mailSenderNew.setPassword(org.getMailPassword());
            mailSenderNew.setHost(org.getMailHost());
            mailSenderNew.setProtocol(org.getMailProtocol());
            mailSenderNew.setPort(org.getMailPort());
            Properties prop = new Properties();
            prop.setProperty("mail.smtp.starttls.enable", "true");
            mailSenderNew.setJavaMailProperties(prop);
            mailSenderNew.send(mimeMessage);
        }
    }
}
