package indi.butterfly.initializer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.IndexOptions;
import indi.butterfly.autoconfigure.ButterflyProperties;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 初始化mongo log的设置
 * 主要用于创建自动清理日志的索引
 * 当且仅当 {@link ButterflyProperties#isAutoClearEnabled()} 为true时可用
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.26
 * @since 1.0.0
 * @see ButterflyProperties
 * @see ApplicationStartedEvent
 */
@Component
public class MongoLogInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final ButterflyProperties butterflyProperties;

    private final MongoProperties mongoProperties;

    private final String INDEX_NAME = "expiredIndex";

    private final Logger logger = LoggerFactory.getLogger(MongoLogInitializer.class);

    @Autowired
    public MongoLogInitializer(ButterflyProperties butterflyProperties, MongoProperties mongoProperties) {
        this.butterflyProperties = butterflyProperties;
        this.mongoProperties = mongoProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (butterflyProperties.isAutoClearEnabled() && butterflyProperties.getLogDefinitions().size() > 0) {
            //开启自动清理日志
            logger.info("开始配置清理日志索引, 默认索引名称: {}", INDEX_NAME);
            MongoClient mongoClient = getMongoClient();
            String database = this.mongoProperties.getDatabase();

            final MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

            this.butterflyProperties.getLogDefinitions().forEach(
                    (name, logDefinition)->{
                        String collection = logDefinition.getLogCollectionName();
                        logger.info("开始为collection: {} 配置清理日志索引", collection);
                        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);

                        //先删除原来
                        mongoCollection.dropIndex(INDEX_NAME);

                        Bson keys = BsonDocument.parse("{ 'created': 1 }");
                        IndexOptions options = new IndexOptions();
                        options.expireAfter(logDefinition.getDaysToExpired(), TimeUnit.DAYS);
                        options.name(INDEX_NAME);
                        options.background(true);//后台创建
                        mongoCollection.createIndex(keys, options);
                        logger.info("collection: {} 配置清理日志索引完成", collection);
                    }
            );
        } else if (!butterflyProperties.isAutoClearEnabled()) {
            //删除索引
            MongoClient mongoClient = getMongoClient();
            String database = this.mongoProperties.getDatabase();
            final MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

            MongoIterable<String> collectionNames = mongoDatabase.listCollectionNames();
            MongoCursor<String> iterator = collectionNames.iterator();
            if (!iterator.hasNext())
                return;

            while (iterator.hasNext()) {
                String collectionName = iterator.next();
                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);
                mongoCollection.dropIndex(INDEX_NAME);
            }
        }
    }

    private MongoClient getMongoClient() {
        String host = this.mongoProperties.getHost();
        Integer port = this.mongoProperties.getPort();
        return new MongoClient(host, port);
    }
}
