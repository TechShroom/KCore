package k.core.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import sun.security.x509.AVA;

public class Translate {

	static HashMap<String, TranslatableString> keyToObj = new HashMap<String, TranslatableString>();

	public static class Language {
		public static final ArrayList<String> avaliableLanguages = new ArrayList<String>();
		public static final String ENGLISH_US = "en_us", JAVA = "java";
	}

	private static HashMap<String, String> e_no_t_map = new HashMap<String, String>();
	static {
		e_no_t_map.put(Language.ENGLISH_US,
				"There is no translation for key %s!");
		e_no_t_map.put(Language.JAVA, "// There is no translation for key %s!");
	}
	public static final TranslatableString E_NOTRANS = new TranslatableString(
			"errors.notavaliable.translation", e_no_t_map);

	public static class TranslatableString {
		public String string = "";
		private HashMap<String, String> lang_k = null;

		public TranslatableString(String translatable,
				HashMap<String, String> langKeys) {
			string = translatable;
			keyToObj.put(translatable, this);
			lang_k = langKeys;
		}

		public String translate(String lang) {
			if (lang_k.containsKey(lang)) {
				return lang_k.get(lang);
			} else {
				return E_NOTRANS.translate(lang, string);
			}
		}

		public String translate(String lang, Object... objects) {
			return lang_k.containsKey(lang) ? String.format(translate(lang),
					objects)
					: "No translation for this, and it might be an ERROR_MESSAGE string. Sorry!";
		}

		public void addAll(HashMap<String, String> langKeys) {
			for (Entry<String, String> e : langKeys.entrySet()) {
				add(e);
			}
		}

		public void add(Entry<String, String> langToVal) {
			lang_k.put(langToVal.getKey(), langToVal.getValue());
		}
	}

	public static TranslatableString addTranslation(String translatable,
			HashMap<String, String> langKeys) {
		if (keyToObj.containsKey(translatable)) {
			TranslatableString old = keyToObj.get(translatable);
			old.addAll(langKeys);
			return old;
		}
		return new TranslatableString(translatable, langKeys);
	}

	public static String translate(String key, String lang, Object... objects) {
		if (keyToObj.containsKey(key)) {
			return keyToObj.get(key).translate(lang, objects);
		} else {
			return E_NOTRANS.translate(lang, key);
		}
	}

	public static void registerNewLanguage(String language,
			HashMap<String, String> languageErrorMessages) {
		Language.avaliableLanguages.add(language);
	}

	public static void registerNewLanguages(
			HashMap<String, HashMap<String, String>> errorMap) {
		for (Entry<String, HashMap<String, String>> e : errorMap.entrySet()) {
			registerNewLanguage(e.getKey(), e.getValue());
		}
	}
}
