package pl.cmil.wuff.plugin.xmi;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.cmil.wuff.plugin.WuffIcons;

import javax.swing.*;

public class XmiFileType  extends XmlLikeFileType
{
    public static final XmiFileType INSTANCE = new XmiFileType();
    @NonNls
    public static final String DEFAULT_EXTENSION = "e4xmi";
    @NonNls
    public static final String DOT_DEFAULT_EXTENSION = ".e4xmi";

    public XmiFileType(  )
    {
        super( XMLLanguage.INSTANCE );
    }

    @NotNull @Override public String getName()
    {
        return "e4xmi";
    }

    @NotNull @Override public String getDescription()
    {
        return "Eclipse model file";
    }

    @NotNull @Override public String getDefaultExtension()
    {
        return DEFAULT_EXTENSION;
    }

    @Nullable @Override public Icon getIcon()
    {
        return WuffIcons.E4XMI_SMALL;
    }
}
