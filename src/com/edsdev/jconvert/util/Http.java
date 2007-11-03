package com.edsdev.jconvert.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Http {
    private final static boolean do_uber_debug = false;

    private static void setConnectionProxyInfo(URLConnection huc) {
        // if (JConfig.queryConfiguration("proxyfirewall", "none").equals("proxy")) {
        // String proxyHost = JConfig.queryConfiguration("proxy.host", null);
        //
        // if (proxyHost != null) {
        // String user = JConfig.queryConfiguration("proxy.user", null);
        // String pass = JConfig.queryConfiguration("proxy.pass", null);
        //
        // System.setProperty("http.proxyUser", user);
        // System.setProperty("http.proxyPassword", pass);
        // if (user != null && pass != null) {
        // String str = user + ':' + pass;
        // String encoded = "Basic " + Base64.encodeString(str);
        // huc.setRequestProperty("Proxy-Authorization", encoded);
        // }
        // }
        // }
    }

    public static URLConnection postFormPage(String urlToPost, String cgiData, String cookie, String referer,
            boolean follow_redirects) {
        URLConnection huc;
        PrintStream obw;
        URL authURL;

        try {
            authURL = new URL(urlToPost);

            huc = authURL.openConnection();
            setConnectionProxyInfo(huc);
            huc.setDoOutput(true);

            if (huc instanceof HttpURLConnection) {
                ((HttpURLConnection) huc).setRequestMethod("POST");
                if (!follow_redirects)
                    ((HttpURLConnection) huc).setInstanceFollowRedirects(false);
            }
            // if (do_uber_debug && JConfig.debugging) {
            // if (cgiData != null) {
            // System.err.println("Content-Type: application/x-www-form-urlencoded");
            // System.err.println("Content-Length: " + Integer.toString(cgiData.length()));
            // System.err
            // .println("User-Agent: "
            // + "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
            // System.err.println("Cookie: " + cookie);
            // } else {
            // System.err.println("CGI Data is null!");
            // }
            // }

            huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            huc.setRequestProperty("Content-Length", Integer.toString(cgiData.length()));
            huc.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
            if (referer != null)
                huc.setRequestProperty("Referer", referer);
            if (cookie != null) {
                huc.setRequestProperty("Cookie", cookie);
            }
            obw = new PrintStream(huc.getOutputStream());
            obw.println(cgiData);
            obw.close();
        } catch (ConnectException ce) {
            ce.printStackTrace();
            huc = null;
        } catch (Exception e) {
            e.printStackTrace();
            huc = null;
        }
        return (huc);
    }

    public static URLConnection makeRequest(URL source, String cookie) throws java.io.IOException {
        URLConnection uc;

        uc = source.openConnection();
        // if (JConfig.queryConfiguration("proxyfirewall", "none").equals("proxy")) {
        // String proxyHost = JConfig.queryConfiguration("proxy.host", null);
        // if (proxyHost != null) {
        // String user = JConfig.queryConfiguration("proxy.user", null);
        // String pass = JConfig.queryConfiguration("proxy.pass", null);
        //
        // if (user != null && pass != null) {
        // if (!user.equals("")) {
        // String str = user + ':' + pass;
        // String encoded = "Basic " + Base64.encodeString(str);
        // uc.setRequestProperty("Proxy-Authorization", encoded);
        // }
        // }
        // }
        // }
        if (cookie != null) {
            uc.setRequestProperty("Cookie", cookie);
        }

        // We fake our user-agent, since some auction servers only let
        // you bid/read if we are a 'supported' browser.
        uc.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");

        return uc;
    }

    public static ByteBuffer getURL(URL dataURL) {
        return getURL(dataURL, null);
    }

    /**
     * @brief Retrieve data from HTTP in raw byte form.
     * @param dataURL - The URL of the raw data to retrieve.
     * @param inCookie - Any cookie needed to be passed along.
     * @return - A result with raw data and the length.
     */
    public static ByteBuffer getURL(URL dataURL, String inCookie) {
        ByteBuffer rval;

        try {
            rval = receiveData(makeRequest(dataURL, inCookie));
        } catch (FileNotFoundException fnfe) {
            // It'd be great if we could pass along something that said, 'not here, never will be'.
            rval = null;
        } catch (IOException e) {
            // Mostly ignore HTTP 504 error, it's just a temporary 'gateway down' error.
            if (e.getMessage().indexOf("HTTP response code: 504") == -1) {
                System.out.println("Error loading data URL (" + dataURL.toString() + ')');
                e.printStackTrace();
            } else {
                System.out.println("HTTP 504 error loading URL (" + dataURL.toString() + ')');
            }
            rval = null;
        }
        return rval;
    }

    /**
     * @brief Retrieve raw data from an already existing URL connection.
     * @param uc - The URLConnection to pull the data from.
     * @return - A structure containing the raw data and the length.
     */
    public static ByteBuffer receiveData(URLConnection uc) throws IOException {
        InputStream is = uc.getInputStream();

        int curMax = 16384;
        byte[] mainBuf = new byte[curMax];
        int offset = 0;
        int count;

        count = is.read(mainBuf, 0, curMax);

        while (count != -1) {
            if (offset + count == curMax) {
                curMax *= 3;
                byte[] tmp = new byte[curMax];
                System.arraycopy(mainBuf, 0, tmp, 0, offset + count);
                mainBuf = tmp;
            }
            offset += count;
            count = is.read(mainBuf, offset, curMax - offset);
        }
        is.close();
        return new ByteBuffer(mainBuf, offset);
    }

    public static StringBuffer receivePage(URLConnection uc, String charEncoding) throws IOException {
        InputStreamReader is = new InputStreamReader(uc.getInputStream(), charEncoding);

        int curMax = 16384;
        char[] mainBuf = new char[curMax];
        int offset = 0;
        int count;
        StringBuffer rv = new StringBuffer();

        count = is.read(mainBuf, 0, curMax);

        while (count != -1) {
            for (int i = 0; i < count; i++) {
                rv.append(mainBuf[i]);
            }
            mainBuf = new char[curMax];
            count = is.read(mainBuf, 0, curMax);
        }
        is.close();

        return rv;
    }

    public static StringBuffer receivePage(URLConnection uc) throws IOException {
        ByteBuffer buff;

        buff = receiveData(uc);

        if (buff == null)
            return null;

        return new StringBuffer(new String(buff.getData(), 0, buff.getLength()));
    }

    public static StringBuffer receivePage_old(URLConnection uc) throws IOException {
        StringBuffer loadUp = new StringBuffer();

        BufferedReader br = null;
        String readData;
        int retry = 0;

        while (br == null && retry < 3) {
            try {
                // ErrorManagement.logMessage(Thread.currentThread().getName() + ": RcvPage.o");
                br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                // ErrorManagement.logMessage(Thread.currentThread().getName() + ": RcvPage.c");
            } catch (java.net.ConnectException jnce) {
                br = null;
                retry++;
                System.out.println("Failed to connect via URLConnection (retry: " + retry + ")");
                jnce.printStackTrace();
            } catch (java.net.NoRouteToHostException cantGetThere) {
                br = null;
                retry++;
                System.out.println("Failed to find a route to receive the page (retry: " + retry + ")");
                cantGetThere.printStackTrace();
            } catch (java.net.SocketException jnse) {
                br = null;
                retry++;
                System.out.println("Failed to load from URLConnection (retry: " + retry + ")");
                jnse.printStackTrace();
            }
        }

        if (br == null)
            return null;

        do {
            readData = br.readLine();
            if (readData != null) {
                loadUp.append(readData);
                loadUp.append("\n");
            }
        } while (readData != null);
        br.close();

        return (loadUp);
    }

    /**
     * Simplest request, load a URL, no cookie, no referer, follow redirects blindly.
     * 
     * @param urlToGet - The URL to load.
     * @return - A URLConnection usable to retrieve the page requested.
     */
    public static URLConnection getPage(String urlToGet) {
        return (getPage(urlToGet, null, null, true));
    }

    public static URLConnection getPage(String urlToGet, String cookie, String referer, boolean redirect) {
        HttpURLConnection huc;

        try {
            URL authURL = new URL(urlToGet);
            URLConnection uc = authURL.openConnection();
            if (!(uc instanceof HttpURLConnection)) {
                return uc;
            }
            huc = (HttpURLConnection) uc;
            huc.setInstanceFollowRedirects(redirect);
            setConnectionProxyInfo(huc);

            huc.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
            if (referer != null)
                huc.setRequestProperty("Referer", referer);
            if (cookie != null)
                huc.setRequestProperty("Cookie", cookie);
        } catch (Exception e) {
            e.printStackTrace();
            huc = null;
        }
        return (huc);
    }
}
