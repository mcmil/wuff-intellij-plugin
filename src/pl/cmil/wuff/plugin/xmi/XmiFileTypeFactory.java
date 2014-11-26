package pl.cmil.wuff.plugin.xmi;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

public class XmiFileTypeFactory extends FileTypeFactory
{

    @Override
    public void createFileTypes( FileTypeConsumer fileTypeConsumer )
    {
        fileTypeConsumer.consume( XmiFileType.INSTANCE, "e4xmi" );

    }
}
