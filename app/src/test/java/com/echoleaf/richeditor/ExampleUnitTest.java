package com.echoleaf.richeditor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    static Map<String, ArrayList<Node>> parseMap = new HashMap<>();

    @Test
    public void addition_isCorrect() {

        String html = "<p>sdfsdfsdfsdf</p><p>efsfdsfs<video src='this is video'/></p><img src='this is img'/><img src='this is img'/><video src='this is video'/>";
        fromHtml(html);
        System.out.println(parseMap);
    }

    //TODO
    public static void fromHtml(String html) {
        Document doc = Jsoup.parse(html);
        Node root = doc.body();
        List<Node> nodes = root.childNodes();
        if (nodes != null && nodes.size() > 0)
            parseNode(nodes);
    }


    private static boolean parseNode(List<Node> nodes) {
        boolean hasMeadia = false;
        for (Node node : nodes) {
            switch (node.nodeName()) {
                case "img":
                case "video":
                    hasMeadia = true;
                    break;
                default:
                    if (node.childNodeSize() > 0) {
                        boolean b = parseNode(node.childNodes());
                        if (!hasMeadia)
                            hasMeadia = b;
                    } else {
                        put("other", node);
                    }
            }
        }
        return hasMeadia;
    }

    private static void put(String key, Node node) {
        ArrayList<Node> nodes = parseMap.get(key);
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        nodes.add(node);
        parseMap.put(key, nodes);
    }
}