package com.xhuicloud.common.security.social;

import com.xhuicloud.common.core.constant.SecurityConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: XHuiCloud
 * @description: MobileCodeAuthenticationFilter
 * @author: Sinda
 * @create: 2019-12-26 22:05
 **/
@Slf4j
public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    //请求中 携带手机号的参数名字
    public static final String authCodeParameter = "auth_code";

    @Getter
    private boolean postOnly = true;

    @Getter
    @Setter
    private AuthenticationEventPublisher eventPublisher;

    @Getter
    @Setter
    private AuthenticationEntryPoint authenticationEntryPoint;

    //当前过滤器要处理的请求
    public SocialAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.TOKEN_SOCIAL, HttpMethod.POST.name()));
    }

    /**
     * 认证
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //判断当前请求是否为post
        if (postOnly && !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            //获取授权凭证
            String auth_code = obtainMobile(request);
            if (auth_code == null) {
                auth_code = "";
            }
            auth_code = auth_code.trim();
            //实例化token 实例化自己的
            SocialAuthenticationToken authRequest = new SocialAuthenticationToken(auth_code);
            //把请求信息设置到token
            setDetails(request, authRequest);
            //由AuthenticationManager 挑选一个provider 来处理MobileCodeAuthenticationToken校验逻辑
            // AuthenticationProvider中的supports来表明支持什么样的MobileCodeAuthenticationToken
            Authentication authenticate = null;
            try {
                authenticate = getAuthenticationManager().authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();

                if (log.isDebugEnabled()) {
                    logger.debug("Authentication request failed: " + e);
                }

                eventPublisher.publishAuthenticationFailure(new BadCredentialsException(e.getMessage(), e),
                        new PreAuthenticatedAuthenticationToken("access-token", "N/A"));

                try {
                    authenticationEntryPoint.commence(request, response,
                            new UsernameNotFoundException(e.getMessage(), e));
                } catch (Exception ex) {
                    logger.error("authenticationEntryPoint handle error:{}", ex);
                }
            }
            return authenticate;
        }
    }

    /**
     * 获取授权凭证
     *
     * @param request
     * @return
     */
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(authCodeParameter);
    }

    /**
     * 设置请求参数
     *
     * @param request
     * @param authRequest
     */
    protected void setDetails(HttpServletRequest request, SocialAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
