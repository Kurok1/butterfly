package indi.butterfly.core;

import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.autoconfigure.ButterflyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户认证服务
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.10
 * @since 1.0.0
 */
@Service
public class AuthService {


    private final static String USER_KEY_PREFIX = "TOKEN:USER:";

    private static String getRedisKey (String userCode) {
        return String.format("%s|%s", USER_KEY_PREFIX, userCode);
    }

    private final RedisService redisService;

    private long loginExpiredSecond;

    @Autowired
    public AuthService(RedisService redisService) {
        this.redisService = redisService;
    }

    public long getLoginExpiredSecond() {
        return loginExpiredSecond;
    }

    @Value("${butterfly.app.loginExpiredSecond}")
    public void setLoginExpiredSecond(long loginExpiredSecond) {
        this.loginExpiredSecond = loginExpiredSecond;
    }

    /**
     * 登录操作,根据用户编码和当前时间戳生成token,记录于redis并返回
     * redis设置超时时间,根据{@link ButterflyProperties#getLoginExpiredSecond()} 来指定,单位为{@code TimeUnit.SECONDS}
     * @param userCode 用户编码
     * @return 返回token
     */
    public synchronized String login(String userCode) {
        String redisKey = getRedisKey(userCode);
        Boolean hasKey = this.redisService.hasKey(redisKey);
        long currentTimeStamp = System.currentTimeMillis() / 1000;
        String token = UUID.randomUUID().toString();
        this.redisService.set(redisKey, token, this.loginExpiredSecond, TimeUnit.SECONDS);
        return token;
    }

    public synchronized Message auth(String userCode, String token) {
        String redisKey = getRedisKey(userCode);
        boolean flag = this.redisService.hasKey(redisKey);
        if (flag)
            return MessageFactory.success();
        else return MessageFactory.error("登录超时,请重新登录");
    }

    public synchronized Message logout(String userCode) {
        String redisKey = getRedisKey(userCode);
        if (this.redisService.hasKey(redisKey))
            this.redisService.delete(redisKey);
        return MessageFactory.success();
    }

}
