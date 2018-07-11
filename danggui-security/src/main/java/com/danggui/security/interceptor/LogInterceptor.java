package com.danggui.security.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import com.danggui.common.Global;
import com.danggui.common.utils.RequestUtil;
import com.danggui.security.entity.SysUserEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 名称：LogInterceptor <br>
 * 描述：日志拦截器<br>
 *
 * @author 李鹏军
 * @version 1.0
 * @since 1.0.0
 */
public class LogInterceptor extends HandlerInterceptorAdapter {

    private static final Log log = LogFactory.getLog(LogInterceptor.class);

    /*
     * (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
     * preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("REQUEST_START_TIME", new Date());

        return true;

    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
     * postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
     * afterCompletion(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler,
                                Exception ex)
            throws Exception {

        Date start = (Date) request.getAttribute("REQUEST_START_TIME");
        Date end = new Date();

        log.info("本次请求耗时：" + (end.getTime() - start.getTime()) + "毫秒；" + getRequestInfo(request).toString());

    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request,
                                               HttpServletResponse response,
                                               Object handler)
            throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }

    /**
     * 主要功能:获取请求详细信息
     * 注意事项:无
     *
     * @param request 请求
     * @return 请求信息
     */
    private StringBuilder getRequestInfo(HttpServletRequest request) {
        StringBuilder reqInfo = new StringBuilder();
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        String urlPath = urlPathHelper.getLookupPathForRequest(request);
        reqInfo.append(" 请求路径=" + urlPath);
        reqInfo.append(" 来源IP=" + RequestUtil.getIpAddrByRequest(request));


        String userName = "";
        try {
            SysUserEntity sysUser = (SysUserEntity) SecurityUtils.getSubject().getSession().getAttribute(Global.CURRENT_USER);
            if (sysUser != null) {
                userName = (sysUser.getUsername());
            }
        } catch (Exception e) {

        }
        reqInfo.append(" 操作人=" + (userName));
        reqInfo.append(" 请求参数=" + RequestUtil.getParameters(request).toString());
        return reqInfo;
    }
}
