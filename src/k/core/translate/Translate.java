package k.core.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Translate {

    static HashMap<String, TranslatableString> keyToObj = new HashMap<String, TranslatableString>();

    /**
     * A class for holding supported/avaliable languages
     * 
     * @author Kenzie Togami
     * 
     */
    public static class Language {
	/**
	 * All properly registered languages
	 */
	public static final ArrayList<String> avaliableLanguages = new ArrayList<String>();

	/**
	 * A supported language
	 */
	public static final String ENGLISH_US = "en_us", JAVA = "java";
    }

    private static HashMap<String, String> e_no_t_map = new HashMap<String, String>();
    static {
	e_no_t_map.put(Language.ENGLISH_US,
		"There is no translation for key %s!");
	e_no_t_map.put(Language.JAVA, "// There is no translation for key %s!");
    }

    /**
     * The error thrown when no translation was found
     */
    public static final TranslatableString E_NOTRANS = new TranslatableString(
	    "errors.notavaliable.translation", e_no_t_map);

    /**
     * An object that can translate a string
     * 
     * @author Kenzie Togami
     * 
     */
    public static class TranslatableString {
	public String string = "";
	private HashMap<String, String> lang_k = null;

	/**
	 * Create a {@link TranslatableString}
	 * 
	 * @param translatable
	 *            - the string that will act as the translatable key
	 * @param langKeys
	 *            - the {@link Language} to {@link String} map
	 */
	public TranslatableString(String translatable,
		HashMap<String, String> langKeys) {
	    string = translatable;
	    keyToObj.put(translatable, this);
	    lang_k = langKeys;
	}

	/**
	 * Translate this string into the language provided, and gives a
	 * translated error message if it fails
	 * 
	 * @param lang
	 *            - the {@link Language} to translate to
	 * @return - the translated string, or an error message on fail
	 */
	public String translate(String lang) {
	    if (lang_k.containsKey(lang)) {
		return lang_k.get(lang);
	    } else {
		return E_NOTRANS.translate(lang, string);
	    }
	}

	/**
	 * A translate method used for the raw translation of error messages and
	 * for object formatting. It will provide an English error message on
	 * fail.
	 * 
	 * @param lang
	 *            - the {@link Language} to translate to
	 * @param objects
	 *            - the objects to format the string with
	 * @return - the formatted, translated string, or an error message
	 *         written in English on fail
	 */
	public String translate(String lang, Object... objects) {
	    return lang_k.containsKey(lang) ? String.format(translate(lang),
		    objects)
		    : "No translation for this, and it might be an ERROR_MESSAGE string. Sorry!";
	}

	/**
	 * Add to the internal language key set
	 * 
	 * @param langKeys
	 *            - the new pairs to add to the key set
	 */
	public void addAll(HashMap<String, String> langKeys) {
	    for (Entry<String, String> e : langKeys.entrySet()) {
		add(e);
	    }
	}

	/**
	 * Adds a single pair to the key set
	 * 
	 * @param langToVal
	 *            - the new pair to add to the key set
	 */
	public void add(Entry<String, String> langToVal) {
	    lang_k.put(langToVal.getKey(), langToVal.getValue());
	}
    }

    /**
     * Add a new translation, or add on the new languages if it already exists
     * 
     * @param translatable
     *            - the string that will act as the translatable key
     * @param langKeys
     *            - the {@link Language} to {@link String} map
     * @return the {@link TranslatableString} that was created/found
     */
    public static TranslatableString addTranslation(String translatable,
	    HashMap<String, String> langKeys) {
	if (keyToObj.containsKey(translatable)) {
	    TranslatableString old = keyToObj.get(translatable);
	    old.addAll(langKeys);
	    return old;
	}
	return new TranslatableString(translatable, langKeys);
    }

    /**
     * Translate the given key into the given language, formatting it with the
     * objects.
     * 
     * @param key
     *            - the translatable key
     * @param lang
     *            - the {@link Language} to translate to
     * @param objects
     *            - the objects to format the result with
     * @return - the translated, formatted string
     */
    public static String translate(String key, String lang, Object... objects) {
	if (keyToObj.containsKey(key)) {
	    return String.format(keyToObj.get(key).translate(lang), objects);
	} else {
	    return E_NOTRANS.translate(lang, key);
	}
    }

    /**
     * Add a new language with the given language key and errors
     * 
     * @param language
     *            - the language key to be accessed by
     * @param languageErrorMessages
     *            - the error messages for this language
     */
    public static void registerNewLanguage(String language,
	    HashMap<String, String> languageErrorMessages) {
	Language.avaliableLanguages.add(language);
    }

    /**
     * Add multiple languages via
     * {@link Translate#registerNewLanguage(String, HashMap)}
     * 
     * @param errorMap
     *            - the language key to error message map for inserting
     */
    public static void registerNewLanguages(
	    HashMap<String, HashMap<String, String>> errorMap) {
	for (Entry<String, HashMap<String, String>> e : errorMap.entrySet()) {
	    registerNewLanguage(e.getKey(), e.getValue());
	}
    }
}
