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

    static enum DataTransferMethod {
        GET, POST;
    }

    static HashMap<String, Long> lastModsForUrls = new HashMap<String, Long>();

    static HashMap<String, GData> lastDataForUrls = new HashMap<String, GData>();

    static GAuth authorization = null;

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

    private static void addData(HttpURLConnection urlc, String postContent)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                urlc.getOutputStream()));
        writer.write(postContent);
        writer.close();
    }

    private static HttpURLConnection createConnection(String end, Auth auth)
            throws IOException {
        // add leading slash if there is one
        URL url = createGAPIUrl(end);
        // if this throws a ClassCastException, something is wrong.
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (authorization != null && auth != Auth.OFF) {
            conn.setRequestProperty("Authorization",
                    authorization.getAuthValue());
        } else if (auth == Auth.ON) {
            throw new IllegalStateException(end + " requires auth");
        }
        return conn;
    }

    private static URL createGAPIUrl(String end) throws MalformedURLException {
        return new URL("https", "api.github.com", end);
    }

    private static InputStream handleCodeError(int code,
            HttpURLConnection urlc, Map<String, String> headers) {
        if (code == HttpStatus.SC_BAD_REQUEST) {
            System.err.println("Bad request!");
            return urlc.getErrorStream();
        }
        return null;
    }

    private static GData handleCodeRetry(int code, HttpURLConnection urlc,
            Map<String, String> headers, DataTransferMethod method,
            String endOfUrl, String postContent, Auth auth) {
        if (code == HttpStatus.SC_UNAUTHORIZED) {
            System.err.println("Retrying with re-auth...");
            GitHub.authWithVars();
            if (method == DataTransferMethod.GET) {
                return getData(endOfUrl, headers, auth);
            } else if (method == DataTransferMethod.POST) {
                return postData(endOfUrl, headers, postContent, auth);
            }
        } else if (code == HttpStatus.SC_NOT_MODIFIED
                && lastDataForUrls.containsKey(endOfUrl)) {
            System.err
                    .println("GitHub says unmodified, returning stored data...");
            return lastDataForUrls.get(endOfUrl);
        }
        return null;
    }

    public static GData getData(String endOfUrl, Map<String, String> headers,
            Auth auth) {
        // normalize
        endOfUrl = (endOfUrl.startsWith("/") ? "" : "/") + endOfUrl;
        try {
            HttpURLConnection urlc = createConnection(endOfUrl, auth);
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
            GData data = data(urlc, headers, DataTransferMethod.GET, endOfUrl,
                    "", auth);
            lastModsForUrls.put(endOfUrl, urlc.getLastModified());
            lastDataForUrls.put(endOfUrl, data);
            GitHub.sync();
            return data;
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

    private static GData data(HttpURLConnection urlc,
            Map<String, String> headers, DataTransferMethod method,
            String endOfUrl, String postContent, Auth auth) throws IOException {
        GData data = new GData();
        int code = urlc.getResponseCode();
        InputStream is = null;
        if (200 <= code && code < 300) {
            // success, continue
        } else if ((data = handleCodeRetry(code, urlc, headers, method,
                endOfUrl, postContent, auth)) != null) {
            return data;
        } else if ((is = handleCodeError(code, urlc, headers)) != null) {
            System.err.println(headers);
            dumpIS(is);
            return GData.IOERRORS;
        } else {
            System.err.println("Error Code " + code + " ("
                    + HttpStatus.getStatusText(code)
                    + ") received, returning IOERRORS");
            return GData.IOERRORS;
        }
        try {
            data.content(urlc, urlc.getContent());
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception with reponse code 200?");
            return GData.IOERRORS;
        }
    }

    public static GData postData(String endOfUrl, Map<String, String> headers,
            String postContent, Auth auth) {
        // normalize
        endOfUrl = (endOfUrl.startsWith("/") ? "" : "/") + endOfUrl;
        try {
            HttpURLConnection urlc = createConnection(endOfUrl, auth);
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
            return data(urlc, headers, DataTransferMethod.POST, endOfUrl,
                    postContent, auth);
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

    private static void dumpIS(InputStream in) {
        System.err.println("DUMPING INPUT STREAM DATA");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                System.err.println(line);
            }
        } catch (IOException e1) {
        }
        try {
            br.close();
        } catch (IOException e) {
        }
        System.err.println("DONE");
    }
}
