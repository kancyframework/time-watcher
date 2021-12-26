package com.github.kancyframework.timewatcher.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.function.Consumer;

/**
 * ImageUtils
 *
 * @author huangchengkang
 * @date 2021/12/25 8:20
 */
@Slf4j
public class ImageUtils {

    public static void drawImage(int width, int height, Consumer<Graphics2D> consumer, String filePath) {
        //获得一个image对象
        final BufferedImage targetImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获得一个图形类
        final Graphics2D g2d = targetImg.createGraphics();
        // 绘制内容
        consumer.accept(g2d);
        // 释放资源
        g2d.dispose();
        //保存成图片
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            ImageIO.write(targetImg, "PNG", outputStream);
        } catch (Exception e) {
            log.info("ImageIO.write fail : {} , filePath : {}", e.getMessage(), filePath);
        }
    }

    public static byte[] drawImage(int width, int height, Consumer<Graphics2D> consumer) {
        //获得一个image对象
        final BufferedImage targetImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获得一个图形类
        final Graphics2D g2d = targetImg.createGraphics();
        // 绘制内容
        consumer.accept(g2d);
        // 释放资源
        g2d.dispose();
        //保存成图片
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(targetImg, "PNG", byteArrayOutputStream);
           return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    /**
     * 复制图片到剪切板。
     */
    public static void setClipboardImage(final byte[] imageBytes) {
        setClipboardImage(new ImageIcon(imageBytes).getImage());
    }

    /**
     * 复制图片到剪切板。
     */
    public static void setClipboardImage(final Image image) {
        Transferable trans = new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (isDataFlavorSupported(flavor))
                    return image;
                throw new UnsupportedFlavorException(flavor);
            }

        };
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
    }

}
