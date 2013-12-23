package k.core.test;

import java.util.HashMap;

import k.core.translate.Translate;
import k.core.translate.Translate.Language;
import k.core.translate.Translate.TranslatableString;

public class TranslationTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
	HashMap<String, String> langKeys = new HashMap<String, String>();
	langKeys.put(Language.ENGLISH_US, "SUCCESS");
	langKeys.put(Language.JAVA, "das java");
	TranslatableString ts = new TranslatableString(
		"translation.test.tsconstruct", langKeys), ts2 = Translate
		.addTranslation("das_java", langKeys);
	System.out.println(ts.translate(Language.ENGLISH_US));
	System.out.println(ts2.translate(Language.ENGLISH_US));
	System.out.println(Translate.translate("das_java", Language.JAVA));
    }

}
