package browse;

import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.Callback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class CustomProxyServlet extends ProxyServlet.Transparent {

    @Override
    protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length, Callback callback)
    {
        try
        {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(buffer, offset, length);
            outputStream.flush();
            callback.succeeded();
        }
        catch (Throwable x)
        {
            callback.failed(x);
        }
    }
}
