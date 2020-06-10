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
 * @see AuthService
 */
public class AuthFilter extends OncePerRequestFilter {

    private final static String USER_HEADER = "B-USER";

    private final static String TOKEN_HEADER = "B_TOKEN";

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //读取请求体
        String body = readInputStream(request);
        String token = request.getHeader(TOKEN_HEADER);
        String requestUser = request.getHeader(USER_HEADER);
        UserAuthBody authBody = TextUtil.readJson(body, UserAuthBody.class);
        //认证逻辑
        if (StringUtils.isEmpty(token) && authBody != null && !authBody.isNotAuth()) {
            //说明这是第一次登录请求
            filterChain.doFilter(request, response);//放行,密码的校验留给controller
        } else if (StringUtils.hasLength(token) && StringUtils.hasLength(requestUser)) {
            //校验redis认证是否过期
            if (this.authService.auth(requestUser, token).isError()) {
                //过期了
                response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            } else {
                //延长认证时间
                this.authService.login(requestUser);
                filterChain.doFilter(request, response);
            }
        } else {
            //其余情况一律禁止
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

    }

    /**
     * 读取输入流
     * @param request http 请求
     * @return 请求体数据
     */
    private String readInputStream(HttpServletRequest request) throws IOException {
        StringBuilder sb;
        BufferedReader br;

        br = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
        sb = new StringBuilder();
        char[] tempChars = new char[30];// 使用readLine会有回车换行的问题
        int charread;
        try {
            while ((charread = br.read(tempChars)) != -1) {
                if (charread != tempChars.length) {
                    sb.append(String.valueOf(tempChars, 0, charread));
                } else {
                    sb.append(tempChars);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            sb.setLength(0);
        } finally {
            br.close();
        }
        return sb.toString();
    }
}
