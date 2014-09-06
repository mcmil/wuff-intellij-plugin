package pl.cmil.wuff.plugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class WuffIcons {
    private static Icon load(String path) {
        return IconLoader.getIcon(path, WuffIcons.class);
    }

    public static Icon WUFF_SMALL = load("/icons/wuff.png");

    private WuffIcons() {
        /* Hidden constructor */
    }
}
