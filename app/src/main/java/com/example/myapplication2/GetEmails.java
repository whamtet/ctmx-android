package com.example.myapplication2;

import com.sun.mail.imap.IMAPBodyPart;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

public class GetEmails implements Function<String, String> {

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
    private String addressText(Address[] addresses) {
        StringBuilder sb = new StringBuilder();
        for (Address address : addresses) {
            if (address instanceof InternetAddress) {
                InternetAddress address2 = (InternetAddress) address;
                sb.append(address2.toString() + "; ");
            }
        }
        return sb.toString();
    }
    private List<JSONAware> getData(String user, String password) throws MessagingException, IOException {

        URLName url = new URLName("imap", "imap.gmail.com", 993, "INBOX", user, password);
        Store store = session.getStore(url);
        store.connect();

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        int messagesToGet = Math.min(folder.getMessageCount(), 1);
        Message[] msgs = folder.getMessages(1, messagesToGet);

        List<JSONAware> out = new ArrayList<>();

        for (Message msg : msgs) {
            MessageInfo info = new MessageInfo();
            info.from = addressText(msg.getFrom());
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

    public String apply(String s) {
        try {
            JSONObject o = (JSONObject) (new JSONParser()).parse(s);
            String user = (String) o.get("username");
            String password = (String) o.get("password");
            return JSONUtil.stringifyList(getData(user, password));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
