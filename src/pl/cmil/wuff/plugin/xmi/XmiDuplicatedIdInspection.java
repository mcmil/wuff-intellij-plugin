package pl.cmil.wuff.plugin.xmi;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.XmlSuppressableInspectionTool;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.util.XmlRefCountHolder;

public class XmiDuplicatedIdInspection extends XmlSuppressableInspectionTool
{
    private Set< String > ids = new HashSet<>();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor( @NotNull final ProblemsHolder holder, final boolean isOnTheFly )
    {
        return new XmlElementVisitor()
        {
            @Override
            public void visitXmlAttributeValue( final XmlAttributeValue value )
            {
                if( value.getTextRange().isEmpty() )
                {
                    return;
                }
                final PsiFile file = value.getContainingFile();
                if( !(file instanceof XmlFile) )
                {
                    return;
                }
                PsiFile baseFile = PsiUtilCore.getTemplateLanguageFile( file );
                if( baseFile != file && !(baseFile instanceof XmlFile) )
                {
                    return;
                }
                final XmlRefCountHolder refHolder = XmlRefCountHolder.getRefCountHolder( (XmlFile)file );
                if( refHolder == null )
                    return;

                final PsiElement parent = value.getParent();
                if( !(parent instanceof XmlAttribute) )
                    return;

                final XmlTag tag = (XmlTag)parent.getParent();
                if( tag == null )
                    return;

                checkValue( value, (XmlFile)file, refHolder, tag, holder );
            }
        };
    }

    protected void checkValue( XmlAttributeValue value, XmlFile file, XmlRefCountHolder refHolder,
        XmlTag tag, ProblemsHolder holder )
    {
        if( refHolder.isValidatable( tag.getParent() )
            && ((XmlAttribute)(value).getParent()).getName().equals( "xmi:id" ) )
        {

            int matches =
                StringUtils.countMatches( file.getText(), ((XmlAttribute)(value).getParent()).getText() );

            if( matches > 1 )
            {
                holder.registerProblem( value, "XMI Duplicate id", ProblemHighlightType.GENERIC_ERROR,
                    ElementManipulators.getValueTextRange( value ), new DuplicateIdQuickFix() );
            }
        }
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return "XmiDuplicateId";
    }

    @NotNull
    @Override
    public String getShortName()
    {
        return "XmiDuplicateId";
    }

    public boolean isEnabledByDefault()
    {
        return true;
    }

    private static class DuplicateIdQuickFix implements LocalQuickFix
    {
        @NotNull
        public String getName()
        {
            return "Generate new id...";
        }

        public void applyFix( @NotNull Project project, @NotNull ProblemDescriptor descriptor )
        {
            ((XmlAttribute)descriptor.getPsiElement().getParent()).setValue( RandomStringUtils
                .randomAlphanumeric( 8 ) );
        }

        @NotNull
        public String getFamilyName()
        {
            return getName();
        }
    }

}
