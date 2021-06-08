package Spring.Controller;

import Server.Client;
import Spring.models.RequestImages;
import com.google.gson.Gson;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@Controller
public class RoomController {



    @RequestMapping(value="/event_count", method=RequestMethod.GET)
    public String getEventCount(Model map) {
        System.out.println("ROOM_CONTROLLER");

        // TODO: retrieve the new value here so you can add it to model map
        map.addAttribute("numDeviceEventsWithAlarm", 2);
        // change "myview" to the name of your view
        return "main/try :: #eventCount";
    }

    @GetMapping("/createRoom")
    public String createRoom(@ModelAttribute("personPhoto") RequestImages image){
        return "authorization/room";
    }

    @PostMapping("/myCompare")
    public String myCompare(Model model/*, @ModelAttribute("personPhoto") RequestImages personPhoto,*/,
                            @RequestParam("personPhoto") String personPhoto,
                            @RequestParam("passport") MultipartFile passportPhoto

                            ) throws IOException {

        System.out.println("Обработка формы");

        var client = new Client("localhost",8081,0);

        if(passportPhoto.isEmpty())
            System.out.println("passport");
        if(personPhoto==null)
            System.out.println("person");
        var sb = StringUtils.newStringUtf8(Base64.getEncoder().encode(passportPhoto.getBytes()));

/*
        System.out.println(sb);
*/
        System.out.println(personPhoto);

        String[] strings = personPhoto.split(",");

        var resPersonPhoto=strings[1].replace("\\", "/");
        var resPassportPhoto = sb.replace("\\", "/");

        var gson = new Gson();

        var req = new HashMap<String,String>();
        req.put("first", resPassportPhoto);
        req.put("second", resPersonPhoto);



        client.writeMessage(gson.toJson(req));

        var res =gson.fromJson(client.readMessage(),HashMap.class);

        var probability = Double.parseDouble(res.get("result").toString());
        String probabilityMessage;
        if(probability<0.4)
            probabilityMessage="Разница минимальна, это точно один и тот же человек";
        else if(probability<0.5)
            probabilityMessage="Это один и тот же человек, но это не точно";
        else if(probability<0.6)
            probabilityMessage="Есть различия, но скорее всего это один и тот же человек";
        else
            probabilityMessage="Совпадение меньше 40 процентов, скорее всего это не тот же человек";

        /*System.out.println(res.get("first"));*/
        model.addAttribute("first", "data:image/png;base64,"+ res.get("first"));
        model.addAttribute("second", "data:image/png;base64,"+ res.get("second"));
        model.addAttribute("status",probabilityMessage);

        return  "main/try :: #eventCount";
    }

    private String saveRequestImage(String name, String imageCode) throws IOException {
        var decoder = Base64.getDecoder();
        var newName = Thread.currentThread().getId()+name+".png";
        var path ="D:\\java\\springWEB\\naumen\\src\\main\\webapp\\uploads\\resultN\\"+newName;
        var file = new File(path);
        ImageIO.write(ImageIO.read(new ByteArrayInputStream(decoder.decode(imageCode))),
                "png", file);
        return newName;

    }

    private String encodeImage(String path) throws IOException {
        BufferedImage bImage = ImageIO.read(new File(path));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, path.split("\\.")[1], bos );
        byte [] data = bos.toByteArray();
        return StringUtils.newStringUtf8(Base64.getEncoder().encode(data)).replace("\\", "/");
    }

    @RequestMapping(value="/compare_photos", method = RequestMethod.POST)
    public String comparePhotos(@RequestParam("personPhoto") String personPhoto,
                                @RequestParam("passportPhoto") String passportPhoto,
                                      Model model) throws IOException {

        System.out.println("Обработка формы");

        if(personPhoto.isEmpty()||passportPhoto.isEmpty())
            return "main/try :: #reload";

        var client = new Client("localhost",8081,0);

        String[] strings = personPhoto.split(",");

        var resPersonPhoto=strings[1].replace("\\", "/");
        System.out.println(passportPhoto);
        var path = "src/main/webapp/uploads"+passportPhoto.split("images")[1].replace("\\", "/");
        System.out.println(path);
        var resPassportPhoto= encodeImage(path);

        var gson = new Gson();

        var req = new HashMap<String,String>();
        req.put("first", resPassportPhoto);
        req.put("second", resPersonPhoto);


        client.writeMessage(gson.toJson(req));

        var res =gson.fromJson(client.readMessage(),HashMap.class);

        var probability = Double.parseDouble(res.get("result").toString());
        String probabilityMessage;
        if(probability<0.4)
            probabilityMessage="Разница минимальна, это точно один и тот же человек";
        else if(probability<0.5)
            probabilityMessage="Это один и тот же человек, но это не точно";
        else if(probability<0.6)
            probabilityMessage="Есть различия, но скорее всего это один и тот же человек";
        else
            probabilityMessage="Совпадение меньше 40 процентов, скорее всего это не тот же человек";

        model.addAttribute("first", saveRequestImage("first",(String) res.get("first")));
        model.addAttribute("second", saveRequestImage("second",(String) res.get("second")));
        model.addAttribute("status",probabilityMessage);

        return  "main/try :: #eventCount";
    }


    @PostMapping("/naumen_compare_photos")
    public void naumenComparePhotos(@ModelAttribute("personPhoto") RequestImages personPhoto,
                                Model model,
                                @RequestParam("passport") MultipartFile passportPhoto) throws IOException {

        var client = new Client("localhost",8081,0);

        if(passportPhoto.isEmpty())
            System.out.println("passport");
        if(personPhoto==null)
            System.out.println("person");
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.newStringUtf8(Base64.getEncoder().encode(passportPhoto.getBytes())));

        String[] strings = personPhoto.getPath().split(",");

        var resPersonPhoto=strings[1].replace("\\", "/");
        var resPassportPhoto = sb.toString().replace("\\", "/");

        var gson = new Gson();

        var req = new HashMap<String,String>();
        req.put("first", resPassportPhoto);
        req.put("second", resPersonPhoto);

        client.writeMessage(gson.toJson(req));

        client.readMessage();
    }


}

