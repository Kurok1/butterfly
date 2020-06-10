package indi.butterfly.controller;

import indi.butterfly.core.Session;
import indi.butterfly.util.TextUtil;
import org.springframework.data.repository.NoRepositoryBean;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * controller 基本类
 * 提供获取用户方法...
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.26
 */
@NoRepositoryBean
public class BaseController {

    private final static String USER_HEADER = "B-USER";

    private final static String TOKEN_HEADER = "B-TOKEN";

    private final static String PARAM_HEADER = "B_PARAM";

    protected String getUser(HttpServletRequest request) {
        String user = request.getHeader(USER_HEADER);
        return TextUtil.decode(user.getBytes());
    }


    protected Session getSession(HttpServletRequest request) {
        String user = request.getHeader(USER_HEADER);
        String token = request.getHeader(TOKEN_HEADER);
        String paramsJson = request.getHeader(PARAM_HEADER);
        Map<String, Object> param = TextUtil.readJson(paramsJson, Map.class);
        return Session.of(user, token , param);
    }

}
