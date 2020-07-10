package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshotter {
    public static BufferedImage TakeScreenshot(Rectangle screenshotScreenRect) {
        if (screenshotScreenRect.width <= 0 || screenshotScreenRect.height <= 0) {
            return null;
        }

        try {
            Robot robot = new Robot();
            return robot.createScreenCapture(screenshotScreenRect);
        } catch (AWTException ex) {
            System.err.println("Screenshot exception");
            ex.printStackTrace();
        }

        return null;
    }
}
