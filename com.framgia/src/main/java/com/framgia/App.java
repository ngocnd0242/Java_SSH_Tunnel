package com.framgia;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkHttp;
import com.chilkatsoft.CkSshTunnel;

/**
 * Hello world!
 *
 */
public class App 
{
    static {
        try {
            System.loadLibrary("chilkat");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public static void main( String[] args )
    {
        //  Starting in v9.5.0.49, all Chilkat classes can be unlocked at once at the beginning of a program
        //  by calling UnlockBundle.  It requires a Bundle unlock code.
        CkGlobal chilkatGlob = new CkGlobal();
        boolean success = chilkatGlob.UnlockBundle("Anything for 30-day trial.");
        if (success != true) {
            System.out.println(chilkatGlob.lastErrorText());
            return;
        }

        String hostname = "10.0.0";
        String username = "";
        String password = "";


        CkSshTunnel tunnel = new CkSshTunnel();

        //  Connect to an SSH server and establish the SSH tunnel:
        success = tunnel.Connect(hostname,22);
        if (success != true) {
            System.out.println(tunnel.lastErrorText());
            return;
        }

        //  Authenticate with the SSH server via a login/password
        //  or with a public key.
        //  This example demonstrates SSH password authentication.
        success = tunnel.AuthenticatePw(username,password);
        if (success != true) {
            System.out.println(tunnel.lastErrorText());
            return;
        }

        //
        tunnel.put_DynamicPortForwarding(true);

        success = tunnel.BeginAccepting(1080);
        if (success != true) {
            System.out.println(tunnel.lastErrorText());
            return;
        }

        //  For this example, let's do a simple HTTPS request:
        String url = "https://www.ethereum.org/";

        CkHttp http = new CkHttp();

        //  Indicate that the HTTP object is to use our portable SOCKS proxy/SSH tunnel running in our background thread.
        http.put_SocksHostname("localhost");
        http.put_SocksPort(1080);
        http.put_SocksVersion(5);
        http.put_SocksUsername("chilkat123");
        http.put_SocksPassword("password123");

        http.put_SendCookies(true);
        http.put_SaveCookies(true);
        http.put_CookieDir("memory");

        //  Do the HTTPS page fetch (through the SSH tunnel)
        String html = http.quickGetStr(url);
        if (http.get_LastMethodSuccess() != true) {
            System.out.println(http.lastErrorText());
            return;
        }

        //  Stop the background listen/accept thread:
        boolean waitForThreadExit = true;
        success = tunnel.StopAccepting(waitForThreadExit);
        if (success != true) {
            System.out.println(tunnel.lastErrorText());
            return;
        }

        //  Close the SSH tunnel (would also kick any remaining connected clients).
        success = tunnel.CloseTunnel(waitForThreadExit);
        if (success != true) {
            System.out.println(tunnel.lastErrorText());
            return;
        }

    }
}
