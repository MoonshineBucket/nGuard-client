package ru.argentoz.imgur;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.network.packet.Packet250CustomPayload;
import ru.argentoz.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class Uploader implements Runnable {

    protected BufferedImage bufferedImage;
    protected String playerName;

    protected Gson gson;

    public Uploader(BufferedImage image, String string) {
        bufferedImage = image;
        playerName = string;

        gson = new Gson();
    }

    @Override
    public void run() {
        String string = null;

        try {
            if((string = getImgurContent("85b9637793370a9")).equals("Not found")) return;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                dos.writeUTF(String.format("%s;%s", playerName == null ? "srvAuth" : playerName, getLink(((Map<String, Object>) gson.fromJson(string,
                        new TypeToken<Map<String, Object>>(){}.getType())).get("data").toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*Packet250CustomPayload packet = new Packet250CustomPayload("nGuard", bos.toByteArray());
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);*/
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("nGuard", bos.toByteArray()));
        } catch (Exception e) {
            System.err.println("Requested JSON: " + string);
            e.printStackTrace();
        }
    }

    protected String getImgurContent(String clientID) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://api.imgur.com/3/image").openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Authorization", String.format("Client-ID %s", clientID));
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            httpURLConnection.connect();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", bos);

            OutputStreamWriter streamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            streamWriter.write(String.format("%s=%s", URLEncoder.encode("image", "UTF-8"), URLEncoder.encode(Base64.encode(bos.toByteArray()), "UTF-8")));
            streamWriter.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();

            String string;
            while((string = bufferedReader.readLine()) != null)
                stringBuilder.append(string).append("\n");

            streamWriter.close();
            bufferedReader.close();

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Not found";
        }
    }

    protected String getLink(String string) {
        try {
            return string
                    .substring(string.indexOf("link="))
                    .replace("link=", "")
                    .replace("}", "")
                    .trim();
        } catch (Exception e) {
            return "Not found";
        }
    }

}