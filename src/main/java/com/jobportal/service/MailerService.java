package com.jobportal.service;

import com.jobportal.model.MailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class MailerService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    public void send(MailInfo mail) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = getMimeMessageHelper(mail, mimeMessage);
//        for(Map.Entry<String, byte[]> entry : mail.getFiles().entrySet()) {
//            String mimeType = Files.probeContentType(Paths.get(entry.getKey()));
//            System.out.println(mimeType);
//            if (mimeType == null) mimeType = "application/octet-stream";
//            helper.addAttachment(entry.getKey(),new ByteArrayResource(mail.getFiles().get(entry.getKey()),mimeType));
//        }
        for(String name : mail.getFiles().keySet()){
            String mimeType = Files.probeContentType(Paths.get(name));
            System.out.println(mimeType);
            if (mimeType == null) mimeType = "application/octet-stream";
            helper.addAttachment(name,new ByteArrayResource(mail.getFiles().get(name),mimeType));
        }
        mailSender.send(mimeMessage);
    }

    private MimeMessageHelper getMimeMessageHelper(MailInfo mail, MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"utf-8");

        helper.setFrom(new InternetAddress(from, mail.getFrom()));
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getBody(),true);
        helper.setReplyTo(mail.getFrom());
        String[] cc = mail.getCc();
        if (cc != null && cc.length > 0) {
            helper.setCc(cc);
        }
        String[] bcc = mail.getBcc();
        if (bcc != null && bcc.length > 0) {
            helper.setBcc(bcc);
        }
        return helper;
    }

    public void send(String to, String subject, String body , Map<String, byte[]> map)
            throws MessagingException, IOException {
        this.send(MailInfo.builder()
                .to(to)
                .subject(subject)
                .files(map)
                .build());
    }


    public void queue(String to, String subject, String body , Map<String, byte[]> map) {
        this.queue(MailInfo.builder()
                .to(to)
                .subject(subject)
                .files(map)
                .build());
    }
    private final Queue<MailInfo> queue = new ConcurrentLinkedQueue<>();
    public void queue (MailInfo mailInfo){
        queue.offer(mailInfo);
    }
    @Scheduled(fixedDelay = 1000)
    public void run(){
        while(!queue.isEmpty()){
            MailInfo mail = queue.poll();
            try{
                this.send(mail);
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}