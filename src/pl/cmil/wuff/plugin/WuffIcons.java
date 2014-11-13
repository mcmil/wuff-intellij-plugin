package pl.cmil.wuff.plugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class WuffIcons {
    private static Icon load(String path) {
        return IconLoader.getIcon(path, WuffIcons.class);
    }

    public static Icon WUFF_SMALL = load("/icons/wuff.png");

    public static Icon E4XMI_SMALL = load("/icons/e4xmi.png");


    private WuffIcons() {
        /* Hidden constructor */
    }
}
