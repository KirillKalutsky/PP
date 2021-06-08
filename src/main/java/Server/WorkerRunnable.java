package Server;

import com.google.gson.Gson;
import org.apache.tomcat.util.codec.binary.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class WorkerRunnable implements Runnable {

    private Socket socket;

    public WorkerRunnable(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
        try(var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {

            var gson = new Gson();

            var req = gson.fromJson(reader.readLine(), HashMap.class);

            var firstKey = req.keySet().toArray()[0];
            var secondKey = req.keySet().toArray()[1];

            var firstPath = saveRequestImage((String)firstKey,(String)req.get(firstKey));
            var secondPath = saveRequestImage((String)secondKey,(String)req.get(secondKey));

            Process p = Runtime.getRuntime().exec(new String[]{"D:\\anaconda\\python.exe", "D:\\anaconda\\6\\venv\\script.py", firstPath,secondPath});

            InputStream stdout = p.getInputStream();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stdout);
            InputStreamReader isrerr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            BufferedReader brerr = new BufferedReader(isrerr);

            var res = new ArrayList<String>();

            String line = null;

            System.out.println("OUTPUT:");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                res.add(line);
            }
            System.out.println();

            System.out.println("ERROR:");
            while ((line = brerr.readLine()) != null)
                System.out.println(line);
            System.out.println();

            p.waitFor();

            var response = new HashMap<String,String>();


            response.put("first", encodeImage(res.get(0)));
            response.put("second", encodeImage(res.get(1)));
            response.put("result", res.get(2));

            writer.write(gson.toJson(response));

            writer.flush();

        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }

    }
    private String encodeImage(String path) throws IOException {
        BufferedImage bImage = ImageIO.read(new File(path));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, path.split("\\.")[1], bos );
        byte [] data = bos.toByteArray();
        return StringUtils.newStringUtf8(Base64.getEncoder().encode(data)).replace("\\", "/");
    }

    private synchronized static String saveRequestImage(String name, String imageCode) throws IOException {
        var decoder = Base64.getDecoder();
        var path ="src/main/webapp/NServer/images/"+Thread.currentThread().getId()+name+".png";
        var file = new File(path);
        ImageIO.write(ImageIO.read(new ByteArrayInputStream(decoder.decode(imageCode))),
                "png", file);
        return path;

    }
}
