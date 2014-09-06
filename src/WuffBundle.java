import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;


public class WuffBundle extends AbstractBundle {

    public static String message(@NotNull @PropertyKey(resourceBundle = PATH_TO_BUNDLE) String key, @NotNull Object... params) {
        return BUNDLE.getMessage(key, params);
    }

    public static final String PATH_TO_BUNDLE = "i18n.WuffBundle";
    private static final WuffBundle BUNDLE = new WuffBundle();

    public WuffBundle() {
        super(PATH_TO_BUNDLE);
    }
}
