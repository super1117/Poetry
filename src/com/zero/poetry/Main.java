package com.zero.poetry;

import com.zero.poetry.bean.AboutBean;
import com.zero.poetry.bean.PoetryBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<AboutBean> list = new ArrayList<>();

    private static PoetryBean poetryBean;

    private static String formatContent(String content){
        String baseContent = Jsoup.clean(content, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        String newText = baseContent.replaceAll("\\s{2,}", "\n");
        String trueContent = newText.replaceFirst("\n", "").trim();
        return trueContent;
    }

    private static String checkMoreId(Element element){
        Elements tagA = element.select("a");
        String id = null;
        String type = null;
        for(Element a : tagA){
            String textA = a.text();
            if(textA.contains("展开阅读")){
                String href = a.attr("href");
                String[] hrefGroup = href.split(",");
                if(hrefGroup.length > 1){
                    id = hrefGroup[1].replace("'", "").replace(")", "");
                    if(href.contains("javascript:shangxiShow")){
                        type = "SX";
                    }else if(href.contains("javascript:fanyiShow")){
                        type = "FY";
                    }
                }
                break;
            }
        }
        if(id == null || type == null){
            return null;
        }
        return type + "_" + id;
    }

    private static void getPoetryContent(Element element){
        System.out.println(formatContent(element.outerHtml()));
    }

    private static void getTransContent(String id){
        System.out.println("翻译 ： " + id);
    }

    private static void getAppreciationContent(String id){
        System.out.println("赏析 ： " + id);
    }

    private static void getAuthorInfo(Element element){
        System.out.println("作者 ： " + element.text().substring(0, element.text().indexOf(" ")));
    }

    public static void main(String[] args) throws Exception{
        Document document = Jsoup.connect("https://so.gushiwen.org/shiwenv_62802abab937.aspx").get();
        Elements elements = document.select(".main3 .left");
        Elements elementDiv = elements.first().children();
        for(Element element : elementDiv){
            if(element.attr("class").equals("title")){
                break;
            }
            if(element.select("h1").size() > 0) {
                getPoetryContent(element);
            }
            else if(element.attr("class").equals("sonspic")){
                getAuthorInfo(element);
            }else{
                String id = checkMoreId(element);
                if(id != null){
                    String type = id.split("_")[0];
                    String nId = id.split("_")[1];
                    if(type.equals("FY")){
                        getTransContent(nId);
                    }else if(type.equals("SX")){
                        getAppreciationContent(nId);
                    }
                }else if(element.select("h2").size() > 0){
                    String title = element.select("h2").text();
                    System.out.println(title);
                    String content = element.outerHtml();
                    System.out.println(formatContent(content));
                }
            }
        }
    }

}
