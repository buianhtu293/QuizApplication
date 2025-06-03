package bussiness;

import android.content.Context;

import com.example.quizapplication.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import model.Account;

public class Buss_Account {

    public ArrayList<Account> getAllAccounts(Context context) {
        ArrayList<Account> accounts = new ArrayList<>();

        accounts.addAll(XmlParser.parseAccounts(context, R.raw.account));

        File file = new File(context.getFilesDir(), "account.xml");
        if (file.exists()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(file);
                NodeList accountNodes = doc.getElementsByTagName("account");
                for (int i = 0; i < accountNodes.getLength(); i++) {
                    Element el = (Element) accountNodes.item(i);
                    String username = el.getAttribute("username");
                    String password = el.getAttribute("password");
                    accounts.add(new Account(username, password));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return accounts;
    }

    public boolean addAccount(Context context, String username, String password) {
        try {
            File file = new File(context.getFilesDir(), "account.xml");

            if (!file.exists()) {

                InputStream inputStream = context.getResources().openRawResource(R.raw.account);
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();
            }

            Document doc;
            Element root;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(file);
            root = doc.getDocumentElement();

            NodeList accounts = root.getElementsByTagName("account");
            for (int i = 0; i < accounts.getLength(); i++) {
                Element el = (Element) accounts.item(i);
                if (el.getAttribute("username").equals(username)) {
                    return false;
                }
            }

            Element newAccount = doc.createElement("account");
            newAccount.setAttribute("username", username);
            newAccount.setAttribute("password", password);
            root.appendChild(newAccount);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Lấy account theo username + password
    public Account getAcount(Context context, String username, String password, int reid) {

        ArrayList<Account> accounts = getAllAccounts(context);

        for (Account acc : accounts) {
            if (acc.getUsername().equals(username)) {
                if (acc.getPassword().equals(password))
                    return acc;
                else
                    return null;
            }
        }
        return null;
    }

    public boolean isExisted(Context context, String username, int reid) {

        ArrayList<Account> accounts = XmlParser.parseAccounts(context, reid);

        for (Account acc : accounts) {
            if (acc.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
