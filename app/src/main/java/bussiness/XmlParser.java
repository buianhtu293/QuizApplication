package bussiness;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.Account;
import model.Question;

public class XmlParser {

    //ReadData từ Account vào ArrayList
    //1. Create Class Acount{usename, password; Constructor, get/set}
    public static ArrayList<Account> parseAccounts(Context context, int resourceId) {
        ArrayList<Account> accountList = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "account".equals(parser.getName())) {
                    String username = parser.getAttributeValue(null, "username");
                    String password = parser.getAttributeValue(null, "password");
                    accountList.add(new Account(username, password));
                }
                eventType = parser.next();
            }

            inputStream.close();

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return accountList;
    }

    public static ArrayList<Question> parseQuestion(Context context, int rawResId) {
        ArrayList<Question> questions = new ArrayList<>();

        try {
            InputStream is = context.getResources().openRawResource(rawResId);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("question");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    String title = e.getElementsByTagName("title").item(0).getTextContent();
                    String a = e.getElementsByTagName("a").item(0).getTextContent();
                    String b = e.getElementsByTagName("b").item(0).getTextContent();
                    String c = e.getElementsByTagName("c").item(0).getTextContent();
                    String d = e.getElementsByTagName("d").item(0).getTextContent();
                    String answer = e.getElementsByTagName("answer").item(0).getTextContent();

//                    questions.add(new Question(title, answer, a, b, c, d));
                }
            }

            is.close();
        } catch (Exception e) {
            Log.e("ReadXML", "Lỗi đọc file XML từ raw: ", e);
        }

        return questions;
    }
}

