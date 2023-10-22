package click.porito.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class ElasticSearchSinkConnectorConfig extends AbstractConfig {
    public static final String ES_CLUSTER_HOST = "es.host";
    private static final String ES_CLUSTER_HOST_DEFAULT = "localhost";
    private static final String ES_CLUSTER_HOST_DOC = System.getenv("ES_HOST");

    public static final String ES_CLUSTER_PORT = "es.port";
    private static final String ES_CLUSTER_PORT_DEFAULT = "9200";
    private static final String ES_CLUSTER_PORT_DOC = System.getenv("ES_PORT");

    public static final String ES_INDEX = "es.index";
    private static final String ES_INDEX_DEFAULT = "kafka-connector-index";
    private static final String ES_INDEX_DOC = System.getenv("ES_INDEX");

    public static ConfigDef CONFIG = new ConfigDef()
            .define(ES_CLUSTER_HOST, ConfigDef.Type.STRING, ES_CLUSTER_HOST_DEFAULT, ConfigDef.Importance.HIGH, ES_CLUSTER_HOST_DOC)
            .define(ES_CLUSTER_PORT, ConfigDef.Type.INT, ES_CLUSTER_PORT_DEFAULT, ConfigDef.Importance.HIGH, ES_CLUSTER_PORT_DOC)
            .define(ES_INDEX, ConfigDef.Type.STRING, ES_INDEX_DEFAULT, ConfigDef.Importance.HIGH, ES_INDEX_DOC);

    public ElasticSearchSinkConnectorConfig(Map<String, String> props) {
        super(CONFIG, props);
    }


}
