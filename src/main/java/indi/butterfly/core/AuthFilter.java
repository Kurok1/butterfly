package indi.butterfly.core;

import indi.butterfly.controller.body.UserAuthBody;
import indi.butterfly.util.TextUtil;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 用户认证过滤,这里不校验用户名和密码
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.10
 * @since 1.0.0
 * @see AuthService
 */
public class AuthFilter extends OncePerRequestFilter {

    private final static String LOGIN_PATH = "/api/auth/login";

    private final static String BUTTERFLY_API_PATH = "/api/butterfly/in";

    private final static String USER_HEADER = "B-USER";

    private final static String TOKEN_HEADER = "B-TOKEN";

    private final static String DEVICE_HEADER = "B-DEVICE";

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //读取请求体
        String token = request.getHeader(TOKEN_HEADER);
        String requestUser = request.getHeader(USER_HEADER);
        //认证逻辑
        String requestPath = request.getServletPath();//获取请求路径
        if (LOGIN_PATH.equals(requestPath) || BUTTERFLY_API_PATH.equals(requestPath)) {
            //说明这是第一次登录请求  或者是外部调用api,外部调用api不需要校验用户
            filterChain.doFilter(request, response);//放行,密码的校验留给controller
        } else if (StringUtils.hasLength(token) && StringUtils.hasLength(requestUser)) {
            //校验redis认证是否过期
            if (this.authService.auth(requestUser, token).isError()) {
                //过期了
                if (fromBrowser(request))
                    response.sendRedirect("/login.html");
                else response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            } else {
                //延长认证时间
                this.authService.login(requestUser);
                filterChain.doFilter(request, response);
            }
        } else {
            //其余情况一律禁止
            if (fromBrowser(request))
                response.sendRedirect("/login.html");
            else response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

    }

    /**
     * 判断http请求是否来着浏览器,前端提供参数
     * @param request http请求
     * @return 如果请求头的B_DEVICE == 'browser'
     */
    private boolean fromBrowser(HttpServletRequest request) {

        String device = request.getHeader(DEVICE_HEADER);

        return StringUtils.hasLength(device) && "browser".equals(device.toLowerCase());

    }
}
