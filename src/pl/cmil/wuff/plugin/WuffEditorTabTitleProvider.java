package pl.cmil.wuff.plugin;

import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

public class WuffEditorTabTitleProvider implements EditorTabTitleProvider {

    public String getEditorTabTitle(Project project, VirtualFile file) {

        if (WuffFileConstants.E4XMI_EXTENSION.equals(file.getExtension())) {

            for (Module module : ModuleManager.getInstance(project).getModules()) {
                if (module.isDisposed()) {
                    return null;
                }
                final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

                for (VirtualFile virtualFile : moduleRootManager.getSourceRoots()) {
                    if (virtualFile.equals(file.getParent())) {
                        return module.getName() + "/" + file.getName();
                    }
                }
            }
        }
        return null;
    }
}
