package pl.cmil.wuff.plugin;

import com.intellij.ide.FileIconProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class WuffFileIconProvider implements FileIconProvider {
    @Nullable
    @Override
    public Icon getIcon(VirtualFile virtualFile, int flag, Project project) {
        if (virtualFile.getExtension().equals("e4xmi")) {
            return WuffIcons.E4XMI_SMALL;
        }
        return null;
    }
}
