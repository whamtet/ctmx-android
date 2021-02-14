package com.example.myapplication2;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;

public class ImapTask extends Thread {
    public void run() {
        try {
            URLName url = new URLName("imap", "imap.gmail.com", 993, "INBOX", "", "");
            Properties p = new Properties();
            p.setProperty("mail.imap.ssl.enable", "true");
            Session session = Session.getDefaultInstance(p);
            Store store = session.getStore(url);
            store.connect();

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            Message[] msgs = folder.getMessages();
            for (Message msg : msgs) {
                if (msg.getContentType().startsWith("multipart/ALTERNATIVE")) {
                    MimeMultipart content = (MimeMultipart) msg.getContent();
                    ByteArrayOutputStream o = new ByteArrayOutputStream();
                    content.writeTo(o);
                    String stringContent = new String(o.toByteArray());
                    Log.i("fuck", stringContent);
                } else {
                    Log.i("fuck", msg.getContentType());
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
