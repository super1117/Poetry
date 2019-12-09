package com.zero.poetry;

import com.google.gson.Gson;
import com.zero.poetry.bean.AboutBean;
import com.zero.poetry.bean.AuthorBean;
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

    private static AuthorBean authorBean;

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
        poetryBean = new PoetryBean();
        String title = element.select("h1").text();
        String source = element.select(".source").text();
        String contentHtml = element.select(".contson").outerHtml();
        String tag = element.select(".tag").text();
        poetryBean.setTitle(title);
        poetryBean.setAuthor(source.split("：")[1]);
        poetryBean.setDynasty(source.split("：")[0]);
        poetryBean.setContent(formatContent(contentHtml));
        poetryBean.setTag(tag);
        System.out.println(poetryBean.toString());
    }

    private static void getTransContent(String id) throws  Exception{
        System.out.println("翻译 ： " + id);
        String cookie = "login=true; Hm_lvt_04660099568f561a75456483228a9516=1575789883; Hm_lpvt_04660099568f561a75456483228a9516=1575789898";
        Document document = Jsoup.connect("https://so.gushiwen.org/nocdn/ajaxfanyi.aspx?id=" + id).header("cookie", cookie).get();
        String title = document.select(".contyishang h2").text();
        String content = formatContent(document.select(".contyishang").outerHtml());
        content = content.substring(title.length());
        AboutBean bean = new AboutBean();
        bean.setTitle(title);
        bean.setContent(content);
        list.add(bean);
        System.out.println(bean.toString());
    }

    private static void getAppreciationContent(String id) throws Exception{
        System.out.println("赏析 ： " + id);
        String cookie = "login=true; Hm_lvt_04660099568f561a75456483228a9516=1575789883; Hm_lpvt_04660099568f561a75456483228a9516=1575789898";
        Document document = Jsoup.connect("https://so.gushiwen.org/nocdn/ajaxshangxi.aspx?id=" + id).header("cookie", cookie).get();
        String title = document.select(".contyishang h2").text();
        String content = formatContent(document.select(".contyishang").outerHtml());
        content = content.substring(title.length());
        AboutBean bean = new AboutBean();
        bean.setTitle(title);
        bean.setContent(content);
        list.add(bean);
        System.out.println(bean);

    }

    private static void getAuthorInfo(Element element){
        authorBean = new AuthorBean();
        Elements image = element.select("img");
        if(image != null && image.size() > 0){
            String pic = image.get(0).attr("src");
            authorBean.setPic(pic);
        }
        String content = element.text();
        if(poetryBean != null){
            content = content.substring(poetryBean.getAuthor().length());
        }
        authorBean.setAbout(content);
        System.out.println(authorBean.toString());
    }

    private static void getPoetry(String url, int grade, int semester, int expand, int catgory, int obligatory) throws Exception{
        Document document = Jsoup.connect(url).get();
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
                    String content = formatContent(element.select(".contyishang").outerHtml());
                    content = content.substring(title.length());
                    AboutBean bean = new AboutBean();
                    bean.setTitle(title);
                    bean.setContent(content);
                    list.add(bean);
                    System.out.println(bean.toString());
                }
            }
        }
//        db.insert(poetryBean, toJson(list), toJson(authorBean), grade, semester, expand, catgory, obligatory);
        poetryBean = null;
        authorBean = null;
        list.clear();
        Thread.sleep(500);
    }

    private static String toJson(Object object){
        return new Gson().toJson(object);
    }

    /**
     * 小学古诗词
     * @throws Exception
     */
    private static void getPrimaryPoetry() throws Exception{
        Document document = Jsoup.connect("https://so.gushiwen.org/gushi/xiaoxue.aspx").get();
        Elements main3 = document.select(".main3 .left .sons");
        Elements typecont = main3.select(".typecont");
        int grade = 1;
        int semester;
        int count = 0;
        for (Element element : typecont){
            String bookMl = element.select(".bookMl").text();
            if(bookMl.startsWith("一")){
                grade = 1;
            }else if (bookMl.startsWith("二")){
                grade = 2;
            }else if (bookMl.startsWith("三")){
                grade = 3;
            }else if (bookMl.startsWith("四")){
                grade = 4;
            }else if (bookMl.startsWith("五")){
                grade = 5;
            }else if (bookMl.startsWith("六")){
                grade = 6;
            }
            semester = bookMl.contains("上册") ? 1 : 0;
            Elements tagA = element.select("a");
            for(Element a : tagA){
                count++;
                String url = a.attr("href");
                System.out.println(grade + " " + semester + " " + url);
                getPoetry(url, grade, semester, 0, 0, 1);
            }
        }
        System.out.println("共计： " + count + " 首");
    }

    /**
     * 获取文言文
     * @param path
     * @throws Exception
     */
    private  static void getClassical(String path) throws Exception {
        Document document = Jsoup.connect(path).get();
        Elements elements = document.select(".main3 .left .sons .typecont a");
        int count = 0;
        for(Element element : elements){
            String host = "https://so.gushiwen.org";
            String url = element.attr("href");
            getPoetry(host + url, 1, 1, 0, 0, 1);
            count ++;
        }
        System.out.println("共计： " + count + "篇");
    }

    /**
     * 初中古诗词
     */
    private static void getMiddlePoetry() throws Exception{
        Document document = Jsoup.connect("https://so.gushiwen.org/gushi/chuzhong.aspx").get();
        Elements main3 = document.select(".main3 .left .sons");
        Elements typecont = main3.select(".typecont");
        int grade = 1;
        int semester;
        int count = 0;
        int expand = 0;
        for (Element element : typecont){
            String bookMl = element.select(".bookMl").text();
            if(bookMl.startsWith("七")){
                grade = 7;
            }else if (bookMl.startsWith("八")){
                grade = 8;
            }else if (bookMl.startsWith("九")){
                grade = 9;
            }
            semester = bookMl.contains("上册") ? 1 : 0;
            expand = bookMl.contains("课内") ? 0 : 1;
            Elements tagA = element.select("a");
            for(Element a : tagA){
                count++;
                String url = a.attr("href");
                System.out.println(grade + " " + semester + " " + url);
                getPoetry(url, grade, semester, expand, 0, 1);
            }
        }
        System.out.println("共计： " + count + " 首");
    }

    /**
     * 高中古诗词
     */
    private static void getHighPoetry() throws Exception{
        Document document = Jsoup.connect("https://so.gushiwen.org/gushi/gaozhong.aspx").get();
        Elements main3 = document.select(".main3 .left .sons");
        Elements typecont = main3.select(".typecont");
        int grade = 12;
        int count = 0;
        int category = 0;
        int obligatory = 1;
        for (Element element : typecont){
            String bookMl = element.select(".bookMl").text();
            if(bookMl.contains("文言文必修")){
                obligatory = 1;
                category = 1;
            }else if (bookMl.contains("文言文选择性必修")){
                obligatory = 2;
                category = 1;
            }else if (bookMl.contains("文言文选修")){
                obligatory = 3;
                category = 1;
            }
            Elements tagA = element.select("a");
            for(Element a : tagA){
                count++;
                String url = a.attr("href");
                getPoetry(url, grade, 0, 0, category, obligatory);
            }
        }
        System.out.println("共计： " + count + " 首");
    }

    private static DBOperation db;

    public static void main(String[] args) throws Exception{
//        db = new DBOperation();
//        getPrimaryPoetry();
        String primary = "https://so.gushiwen.org/wenyan/xiaowen.aspx";
        String middle = "https://so.gushiwen.org/wenyan/chuwen.aspx";
        String high = "https://so.gushiwen.org/wenyan/gaowen.aspx";
//        getClassical(primary);
//        getMiddlePoetry();
        getHighPoetry();
    }

}
