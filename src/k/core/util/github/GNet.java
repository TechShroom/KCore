package k.core.util.github;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Special network impl for github that uses the last modified headers to lower
 * rate usage.
 * 
 * @author Kenzie Togami
 */
final class GNet {

    private static HashMap<String, Long> lastModsForUrls = new HashMap<String, Long>();

    private static final class DefaultHeader implements Entry<String, String> {
        private String key, value;

        DefaultHeader(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        /**
         * Invalid, default headers may not be changed.
         */
        @Override
        public String setValue(String value) {
            throw new UnsupportedOperationException(
                    "default headers cannot be changed");
        }
    }

    /**
     * The value for the default accept header.
     */
    public static final String ACCEPT_VALUE = "application/vnd.github.v3+json";

    /**
     * Default header for accept
     */
    private static final DefaultHeader accepts = new DefaultHeader("Accept",
            ACCEPT_VALUE);

    /**
     * An unmodifiable map that is empty, so a new one is not created. Use where
     * you do not want to mark any headers.
     */
    public static final Map<String, String> NO_HEADERS_SPECIFIED = Collections
            .unmodifiableMap(new HashMap<String, String>(0));

    private static URL createGAPIUrl(String end) throws MalformedURLException {
        return new URL("https", "api.github.com", end);
    }

    private static HttpURLConnection createConnection(String end)
            throws IOException {
        // add leading slash if there is one
        URL url = createGAPIUrl(end);
        // if this throws a ClassCastException, something is wrong.
        return (HttpURLConnection) url.openConnection();
    }

    public static GData getData(String endOfUrl, Map<String, String> headers) {
        // normalize
        endOfUrl = (endOfUrl.startsWith("/") ? "" : "/") + endOfUrl;
        try {
            HttpURLConnection urlc = createConnection(endOfUrl);
            // add accept header
            urlc.addRequestProperty(accepts.getKey(), accepts.getValue());
            for (Entry<String, String> head : headers.entrySet()) {
                urlc.addRequestProperty(head.getKey(), head.getValue());
            }
            Long lastMod = lastModsForUrls.get(endOfUrl);
            if (lastMod != null) {
                urlc.setIfModifiedSince(lastMod);
            }
            // cache is not good
            urlc.setDefaultUseCaches(false);
            urlc.setUseCaches(false);
            urlc.setDoInput(true);
            // give it 1s for response
            urlc.setConnectTimeout(1000);
            try {
                urlc.connect();
            } catch (SocketException se) {
                // connect timeout
                return GData.TIMEOUT;
            }
            lastModsForUrls.put(endOfUrl, urlc.getLastModified());
            try {
                GData data = new GData();
                data.content(urlc, urlc.getContent());
                return data;
            } catch (IOException e) {
                e.printStackTrace();
                int code = urlc.getResponseCode();
                if (code == 200) {
                    System.err
                            .println("OK 200 received, but IOException thrown!");
                    return GData.BADURL; // assume bad URL
                } else {
                    System.err.println("Error Code " + code + " ("
                            + HttpStatus.getStatusText(code)
                            + ") received, returning IOERRORS");
                    return GData.IOERRORS;
                }
            }
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
            return GData.BADURL;
        } catch (IOException e) {
            e.printStackTrace();
            return GData.IOERRORS;
        } catch (Exception e) {
            throw new RuntimeException("unexpected exception", e);
        }
    }

    public static GData postData(String endOfUrl, Map<String, String> headers,
            String postContent) {
        // normalize
        endOfUrl = (endOfUrl.startsWith("/") ? "" : "/") + endOfUrl;
        try {
            HttpURLConnection urlc = createConnection(endOfUrl);
            // add accept header
            urlc.addRequestProperty(accepts.getKey(), accepts.getValue());
            for (Entry<String, String> head : headers.entrySet()) {
                urlc.addRequestProperty(head.getKey(), head.getValue());
            }
            // cache is not good
            urlc.setDefaultUseCaches(false);
            urlc.setUseCaches(false);
            urlc.setDoOutput(true);
            // give it 1s for response
            urlc.setConnectTimeout(1000);
            // using post
            urlc.setRequestMethod("POST");
            addData(urlc, postContent);
            try {
                urlc.connect();
            } catch (SocketException se) {
                // connect timeout
                return GData.TIMEOUT;
            }
            try {
                GData data = new GData();
                data.content(urlc, urlc.getContent());
                return data;
            } catch (IOException e) {
                e.printStackTrace();
                int code = urlc.getResponseCode();
                if (code == 200) {
                    System.err
                            .println("OK 200 received, but IOException thrown!");
                    return GData.BADURL; // assume bad URL
                } else {
                    System.err.println("Error Code " + code + " ("
                            + HttpStatus.getStatusText(code)
                            + ") received, returning IOERRORS");
                    return GData.IOERRORS;
                }
            }
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
            return GData.BADURL;
        } catch (IOException e) {
            e.printStackTrace();
            return GData.IOERRORS;
        } catch (Exception e) {
            throw new RuntimeException("unexpected exception", e);
        }
    }

    private static void addData(HttpURLConnection urlc, String postContent)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                urlc.getOutputStream()));
        writer.write(postContent);
        writer.close();
    }
}
