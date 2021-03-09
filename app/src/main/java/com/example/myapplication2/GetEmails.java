package com.example.myapplication2;

import android.util.Log;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

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

    private static final int PAGE_SIZE = 3;

    private IMAPFolder folder;

    private void setFolder(String user, String password) throws MessagingException {
        setFolder(user, password, "STARRED");
    }

    private void setFolder(String user, String password, String folder) throws MessagingException {

        Properties p = new Properties();
        p.setProperty("mail.imap.ssl.enable", "true");
        Session session = Session.getDefaultInstance(p);

        URLName url = new URLName("imap", "imap.fastmail.com", 993, folder, user, password);
        Store store = session.getStore(url);
        store.connect();

        this.folder = (IMAPFolder) store.getFolder(folder);
        this.folder.open(Folder.READ_ONLY);

    }

    private static class MessageInfo implements JSONAware {
        public String from = "";
        public String subject = "";
        public String html = "";

        @Override
        public String toJSONString() {
            JSONObject o = new JSONObject();
            o.put("from", from);
            o.put("subject", subject);
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
    private List<JSONAware> getData(int index) throws MessagingException, IOException {

        List<JSONAware> out = new ArrayList<>();

        final int totalMessages = folder.getMessageCount();
        if (index >= totalMessages) {
            // we've gotten it all
            return out;
        }

        int messagesToGet = Math.min(totalMessages, index + PAGE_SIZE);
        Message[] msgs = folder.getMessages(index + 1, messagesToGet);

        for (Message msg : msgs) {
            MessageInfo info = new MessageInfo();
            info.from = addressText(msg.getFrom());
            info.subject = msg.getSubject();
            if (msg.getContent() instanceof MimeMultipart) {
                MimeMultipart content = (MimeMultipart) msg.getContent();
                for (int i = 0; i < content.getCount(); i++) {
                    BodyPart bodyPart = content.getBodyPart(i);
                    String contentType = bodyPart.getContentType();

                    if (contentType.startsWith("TEXT/HTML")) {
                        info.html = (String) bodyPart.getContent();
                    }
                    if (contentType.startsWith("multipart/RELATED")) {
                        MimeMultipart related = (MimeMultipart) bodyPart.getContent();
                        for (int j = 0; j < related.getCount(); j++) {
                            BodyPart subpart = related.getBodyPart(j);
                            if (subpart.getContentType().startsWith("multipart/ALTERNATIVE")) {
                                MimeMultipart alternative = (MimeMultipart) subpart.getContent();
                                for (int k = 0; k < alternative.getCount(); k++) {
                                    BodyPart subsubpart = alternative.getBodyPart(k);
                                    if (subsubpart.getContentType().startsWith("TEXT/HTML")) {
                                        info.html = (String) subsubpart.getContent();
                                    }
                                }
                            }
                            // TODO: stitch in inline images
                        }
                    }
                }
            } else {
                info.html = msg.getContentType();
            }
            out.add(info);
        }

        return out;
    }

    public String apply(String s) {
        try {
            JSONObject o = (JSONObject) (new JSONParser()).parse(s);
            if (folder == null) {
                setFolder((String) o.get("username"), (String) o.get("password"));
            }
            Long index = o.containsKey("start") ? (Long) o.get("start") : 0;
            return JSONUtil.stringifyList(getData(index.intValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
