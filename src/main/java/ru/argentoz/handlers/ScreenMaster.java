package ru.argentoz.handlers;

import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import ru.argentoz.imgur.Uploader;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScreenMaster {

    public static final ExecutorService EXECUTOR_SERVICE;

    public static final DateFormat DATE_FORMAT;
    public static final Font FONT;

    public static final ImageWriter IMAGE_WRITER;
    public static final ImageWriteParam IMAGE_WRITE_PARAM;

    public static IntBuffer pixelBuffer;
    public static int[] pixelValues;

    static {
        EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        FONT = new Font("Arial Black", 1, 20);

        IMAGE_WRITER = ImageIO.getImageWritersByFormatName("jpeg").next();

        IMAGE_WRITE_PARAM = IMAGE_WRITER.getDefaultWriteParam();
        IMAGE_WRITE_PARAM.setCompressionMode(2);
        IMAGE_WRITE_PARAM.setCompressionQuality(0.7f);
    }

    public static void saveScreenshot(String playerName, int width, int height) {
        try {
            BufferedImage bufferedImage;
            Minecraft minecraft = Minecraft.getMinecraft();
            if(minecraft.isFullScreen()) {
                int size = width * height;
                if(pixelBuffer == null || pixelBuffer.capacity() < size) {
                    pixelBuffer = BufferUtils.createIntBuffer(size);
                    pixelValues = new int[size];
                }

                GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

                pixelBuffer.clear();
                GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

                pixelBuffer.get(pixelValues);
                func_74289_a(pixelValues, width, height);

                bufferedImage = new BufferedImage(width, height, 1);
                bufferedImage.setRGB(0, 0, width, height, pixelValues, 0, width);

                Graphics graphics = bufferedImage.getGraphics();
                graphics.setFont(FONT);

                graphics.setColor(Color.orange);
                graphics.drawString(minecraft.thePlayer.getDisplayName(), 5, 40);
                graphics.drawString(DATE_FORMAT.format(new Date()), 5, 65);

                graphics.dispose();
            } else {
                bufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

                Graphics graphics = bufferedImage.getGraphics();
                graphics.setFont(FONT);

                graphics.setColor(Color.orange);
                graphics.drawString(minecraft.thePlayer.getDisplayName(), 5, 40);
                graphics.drawString(DATE_FORMAT.format(new Date()), 5, 65);

                graphics.dispose();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try(ImageOutputStream os = new MemoryCacheImageOutputStream(bos)) {
                    IMAGE_WRITER.setOutput(os);

                    IMAGE_WRITER.write(null, new IIOImage(bufferedImage,
                            null, null), IMAGE_WRITE_PARAM);
                    IMAGE_WRITER.dispose();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            EXECUTOR_SERVICE.execute(new Uploader(bufferedImage, playerName));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    protected static void func_74289_a(int[] ints, int width, int height) {
        int[] newIntsArray = new int[width];

        int size = height / 2;
        for(int i = 0; i < size; ++i) {
            System.arraycopy(ints, i * width, newIntsArray, 0, width);
            System.arraycopy(ints, (height - 1 - i) * width, ints, i * width, width);
            System.arraycopy(newIntsArray, 0, ints, (height - 1 - i) * width, width);
        }
    }

}