package com.example.myapplication2;

import android.util.Log;

import com.sun.mail.imap.IMAPBodyPart;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;

public class GetEmails implements Function<JSONObject, String> {

    private final Session session;

    public GetEmails() {
        Properties p = new Properties();
        p.setProperty("mail.imap.ssl.enable", "true");
        session = Session.getDefaultInstance(p);
    }

    private static class MessageInfo implements JSONAware {
        public String from = "";
        public String subject = "";
        public String text = "";
        public String html = "";

        @Override
        public String toJSONString() {
            JSONObject o = new JSONObject();
            o.put("from", from);
            o.put("subject", subject);
            o.put("text", text);
            o.put("html", html);

            return o.toJSONString();
        }
    }
    private List<JSONAware> getData(String user, String password) throws MessagingException, IOException {

        URLName url = new URLName("imap", "imap.gmail.com", 993, "INBOX", user, password);
        Store store = session.getStore(url);
        store.connect();

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        int messagesToGet = Math.min(folder.getMessageCount(), 10);
        Message[] msgs = folder.getMessages(1, messagesToGet);

        List<JSONAware> out = new ArrayList<>();

        for (Message msg : msgs) {
            MessageInfo info = new MessageInfo();
            info.from = msg.getFrom().toString();
            info.subject = msg.getSubject();
            if (msg.getContent() instanceof MimeMultipart) {
                MimeMultipart content = (MimeMultipart) msg.getContent();
                for (int i = 0; i < content.getCount(); i++) {
                    BodyPart bodyPart = content.getBodyPart(i);
                    if (bodyPart instanceof IMAPBodyPart) {
                        IMAPBodyPart part = (IMAPBodyPart) bodyPart;
                        String contentType = part.getContentType();
                        if (contentType.startsWith("TEXT/HTML")) {
                            info.html = (String) part.getContent();
                        }
                        if (contentType.startsWith("TEXT/PLAIN")) {
                            info.text = (String) part.getContent();
                        }
                    }
                }
            } else {
                info.text = msg.getContentType();
            }
            out.add(info);
        }

        return out;
    }

    public String apply(JSONObject o) {
        try {
            String user = (String) o.get("username");
            String password = (String) o.get("password");
            return JSONUtil.stringifyList(getData(user, password));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
