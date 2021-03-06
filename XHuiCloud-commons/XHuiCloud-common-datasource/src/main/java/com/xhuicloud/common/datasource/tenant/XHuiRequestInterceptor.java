package com.xhuicloud.common.datasource.tenant;

import com.xhuicloud.common.core.constant.CommonConstants;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

/**
 * @program: XHuiCloud
 * @description: feign 请求头传递
 * @author: Sinda
 * @create: 2020-05-12 18:31
 */
public class XHuiRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (XHuiTenantHolder.getTenant() != null) {
            request.getHeaders().set(CommonConstants.TENANT_ID, String.valueOf(XHuiTenantHolder.getTenant()));
        }
        return execution.execute(request, body);
    }
}
