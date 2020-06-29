package indi.butterfly.controller;

import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.controller.body.DatasourceBody;
import indi.butterfly.domain.DatasourceConfig;
import indi.butterfly.repository.DatasourceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 数据源配置访问controller
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.20
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/datasource")
public class DatasourceConfigController extends BaseController {

    private DatasourceConfigRepository datasourceConfigRepository;


    @Autowired
    public void setDatasourceConfigRepository(DatasourceConfigRepository datasourceConfigRepository) {
        this.datasourceConfigRepository = datasourceConfigRepository;
    }

    @PostMapping("/")
    public ResponseMessage<DatasourceConfig> saveOrUpdate(HttpServletRequest request, @RequestBody DatasourceConfig datasourceConfig) {
        if (datasourceConfig.getId() == null)
            datasourceConfig.setCreated(LocalDateTime.now());
        datasourceConfig.setLastUpdated(LocalDateTime.now());
        datasourceConfig.setCreatedBy(getUser(request));
        datasourceConfig.setLastUpdatedBy(getUser(request));
        datasourceConfig = datasourceConfigRepository.save(datasourceConfig);
        return MessageFactory.successResponse("success", datasourceConfig);
    }

    @GetMapping("/{id}")
    public ResponseMessage<DatasourceConfig> getById(@PathVariable("id") long id) {
        return MessageFactory.successResponse("success", datasourceConfigRepository.findById(id).orElse(null));
    }

    @GetMapping("/code/{code}")
    public ResponseMessage<DatasourceConfig> getByCode(@PathVariable("code") String code) {
        return MessageFactory.successResponse("success", datasourceConfigRepository.findFirstByCode(code).orElse(null));
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<DatasourceConfig> deleteById(@PathVariable("id") long id) {
        if (!datasourceConfigRepository.existsById(id))
            return MessageFactory.errorResponse("数据源配置不存在");
        else {
            datasourceConfigRepository.deleteById(id);
            return MessageFactory.successResponse("success", null);
        }
    }

    @PostMapping("/testConnection")
    public ResponseMessage<Object> testDatasource(@RequestBody DatasourceBody body) {
        //检查driverClass是否存在
        try {
            Connection connection = DriverManager.getConnection(body.getUrl(), body.getUser(), body.getPassword());
            if (connection == null)
                return MessageFactory.errorResponse("数据库异常,连接失败");

            connection.close();
            connection = null;
            return MessageFactory.successResponse("success", null);
        }  catch (SQLException e) {
            e.printStackTrace();
            return MessageFactory.errorResponse("数据库异常,连接失败");
        }
    }
}
