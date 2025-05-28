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

    // Hàm đọc tất cả tài khoản từ res/raw + internal storage
    public ArrayList<Account> getAllAccounts(Context context) {
        ArrayList<Account> accounts = new ArrayList<>();

        // 1. Đọc tài khoản gốc từ res/raw/account.xml
        accounts.addAll(XmlParser.parseAccounts(context, R.raw.account));

        // 2. Đọc tài khoản bổ sung từ internal storage (nếu có)
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

    // Thêm account mới vào internal storage
    public boolean addAccount(Context context, String username, String password) {
        try {
            File file = new File(context.getFilesDir(), "account.xml");

            if (!file.exists()) {
                // Sao chép từ res/raw/account.xml sang internal storage
                InputStream inputStream = context.getResources().openRawResource(R.raw.account); // Đảm bảo tên file raw là account.xml
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

            // Đọc file hiện có từ internal storage
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(file);
            root = doc.getDocumentElement();

            // Kiểm tra trùng username
            NodeList accounts = root.getElementsByTagName("account");
            for (int i = 0; i < accounts.getLength(); i++) {
                Element el = (Element) accounts.item(i);
                if (el.getAttribute("username").equals(username)) {
                    return false; // trùng username
                }
            }

            // Thêm tài khoản mới
            Element newAccount = doc.createElement("account");
            newAccount.setAttribute("username", username);
            newAccount.setAttribute("password", password);
            root.appendChild(newAccount);

            // Lưu lại
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
