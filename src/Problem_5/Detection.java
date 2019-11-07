package Problem_5;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class Detection {
    private static final String key1 = "8d3e12ed7ffe417a876f2f53794122b5";
    private static final String key2 = "0e1bbf08e0b04d86bdea106e0110a766";

    private static ArrayList<ObjectForDetection> objectForDetections = new ArrayList<>();

    public Detection() {
    }

    public BufferedImage Detection(File file) {
        HttpClient httpclient = HttpClients.createDefault();
        try {
            URIBuilder builder = new URIBuilder("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze");
            builder.setParameter("visualFeatures", "Objects");
            builder.setParameter("details", "Celebrities,Landmarks");
            builder.setParameter("language", "en");
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", key1);

            FileEntity reqEntityF = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);

            request.setEntity(reqEntityF);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                Image image = new Image(file.toURI().toString());
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);


                //получаем теги по ОДНОЙ ФОТОГРАФИИ
                ArrayList<ArrayList<String>> array = getResults(EntityUtils.toString(entity));



                // ArrayList<Pair<String, Color>> tag_color = new ArrayList<>();
                ArrayList<String> tags = new ArrayList<>();
                ArrayList<Color> colors = new ArrayList<Color>(){{
                    add(Color.RED);
                    add(Color.GREEN);
                    add(Color.yellow);
                    add(Color.orange);
                    add(Color.magenta);
                }};
                ArrayList<ObjectForDetection> objectForDetectionsTemp = new ArrayList<>();

                for (ArrayList<String> candidateObj : array) {
                    //System.out.println(candidateObj);
                    String i = candidateObj.get(1);
                    int x, y, w, h;
                    w = Integer.valueOf(i.substring(5, i.indexOf(",\"x\"")));
                    x = Integer.valueOf(i.substring(i.indexOf(",\"x\"") + 5, i.indexOf(",\"h\"")));
                    h = Integer.valueOf(i.substring(i.indexOf(",\"h\"") + 5, i.indexOf(",\"y\"")));
                    y = Integer.valueOf(i.substring(i.indexOf(",\"y\"") + 5, i.length() - 1));


                   // System.out.println(candidateObj.get(0));
                    String s = "";
                    //TODO: сделать проверку на то, что объект с таким именем и координатами существует
                    //TODO: в противном случае назначаем его новым объектом, если новый обект и пропал другой обект - назначаем его старым но движущимся

                    int index = findByName(candidateObj.get(0), objectForDetections);
                    if (index != -1){
                        int index2 = findByNameAndCords(candidateObj.get(0), x , y, objectForDetections);
                        if (index2 == -1){
                            s = " movement";
                            // objectForDetections.remove(index2);
                            remove(index2);
                        }else{
                           // objectForDetections.remove(index);
                            remove(index);
                        }
                    }else{
                        s = "new obj";
                    }
                    objectForDetectionsTemp.add(new ObjectForDetection(candidateObj.get(0), x , y, w , h));

                    //Рисование
                    Graphics graphics = bufferedImage.getGraphics();
                    if (tags.indexOf(candidateObj.get(0)) == -1){
                        graphics.setColor(colors.get(tags.size() % colors.size()));
                        tags.add(candidateObj.get(0));
                    }else{
                        graphics.setColor(colors.get(tags.indexOf(candidateObj.get(0))));
                    }
                    graphics.drawRect(x, y, 1, h);
                    graphics.drawRect(x, y, w, 1);
                    graphics.drawRect(x + w - 1, y, 1, h);
                    graphics.drawRect(x, y + h - 1, w, 1);
                    graphics.setFont(new Font("TimesRoman", Font.PLAIN, bufferedImage.getWidth()/30));
                    if (graphics instanceof Graphics2D) {
                        Graphics2D g2 = (Graphics2D) graphics;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        String tempName = candidateObj.get(0) + " " + s;
                        g2.setColor(Color.WHITE);
                        g2.fillRect(x , y + h - 20, tempName.length()*bufferedImage.getWidth()/61 , 20);
                        g2.setColor(new Color(38, 0 , 225));
                        g2.drawString(tempName , x, y + h);// + graphics.getFont().getSize());
                    }
                }
                objectForDetections.clear();
                objectForDetections.addAll(objectForDetectionsTemp);
                return bufferedImage;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void remove(int index){
        ArrayList<ObjectForDetection> temp = new ArrayList<>();
        for (int i = 0; i < objectForDetections.size(); i++) {
            if (i != index) temp.add(objectForDetections.get(i));
        }
        objectForDetections.clear();
        objectForDetections.addAll(temp);
    }

    public ArrayList<ArrayList<String>> getResults(String result) throws JSONException {
             System.out.println(result);
        JSONObject jsonObj = new JSONObject(result);
        if (!jsonObj.has("objects")) {
            return new ArrayList<>(new ArrayList<>());
        } else {
            ArrayList<ArrayList<String>> mainResult = new ArrayList<>(new ArrayList<>());
            JSONArray objectsArr = (JSONArray) jsonObj.get("objects");
            for (int i = 0; i < objectsArr.length(); i++) {
                JSONObject object = objectsArr.getJSONObject(i);
                //TODO проверки
                if (object.has("rectangle") && object.has("object")) {
                    mainResult.add(new ArrayList<String>() {{
                        add(object.get("object").toString());
                        add(object.get("rectangle").toString());
                       /* JSONArray arr = (JSONArray) object.get("rectangle");
                        add(arr.getJSONObject(0).toString());
                        add(arr.getJSONObject(1).toString());
                        add(arr.getJSONObject(2).toString());
                        add(arr.getJSONObject(3).toString());*/
                    }});
                }
            }
            return mainResult;
        }
    }

    private int findByName(String name, ArrayList<ObjectForDetection> objectForDetections){
        for (int i = 0; i < objectForDetections.size(); ++i){
            if (objectForDetections.get(i).getName().equals(name)) return i;
        }
        return -1;
    }


    private int findByNameAndCords(String name, int x, int y, ArrayList<ObjectForDetection> objectForDetections){
        for (int i = 0; i < objectForDetections.size(); ++i){
            if (objectForDetections.get(i).getName().equals(name) && x == objectForDetections.get(i).x && y == objectForDetections.get(i).y) return i;
        }
        return -1;
    }
}
