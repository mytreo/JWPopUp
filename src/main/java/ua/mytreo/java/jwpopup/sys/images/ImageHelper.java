package ua.mytreo.java.jwpopup.sys.images;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mytreo
 * @version 1.0
 *          18.02.2016
 */
public class ImageHelper {
    private static Map<String, Image> images = new HashMap<>();

    static {
        images.put("error", new Image(ImageHelper.class.getResourceAsStream("/images/system/error.png")));
    }

    public static Image getImageByName(String name) {
        if (!images.containsKey(name)) {
            InputStream is = ImageHelper.class.getResourceAsStream("/images/system/" + name + ".png");
            if (is != null) {
                images.put(name, new Image(is));
            } else {
                return images.get("error");
            }
        }
        return images.get(name);
    }

}
