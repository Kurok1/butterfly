package indi.butterfly.controller;

import indi.butterfly.util.TextUtil;
import org.springframework.data.repository.NoRepositoryBean;

import javax.servlet.http.HttpServletRequest;

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

    protected String getUser(HttpServletRequest request) {
        String user = request.getHeader(USER_HEADER);
        return TextUtil.decode(user.getBytes());
    }

}
