package pl.cmil.wuff.plugin.xmi;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * Created by mcmil on 2014-11-27.
 */
public class XmiInspectionToolProvider implements InspectionToolProvider
{
    @Override
    public Class[] getInspectionClasses()
    {
        return new Class[]
        { XmiDuplicatedIdInspection.class };
    }
}
