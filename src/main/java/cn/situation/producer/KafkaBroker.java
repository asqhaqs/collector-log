package cn.situation.producer;

import cn.situation.cons.SystemConstant;
import cn.situation.model.DataPacket;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;

public class KafkaBroker {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaBroker.class);

    private static Properties kafkaProducerProperties = new Properties();
    private static String topic = SystemConstant.TOPIC;

    private static KafkaProducer<String, byte[]> producer;

    private static KafkaBroker instance = new KafkaBroker();

    static {
        if (SystemConstant.IS_KERBEROS.equals("true")) {
            System.setProperty("java.security.auth.login.config",
                    SystemConstant.KAFKA_KERBEROS_PATH + File.separator + "kafka_server_jaas.conf");
            System.setProperty("java.security.krb5.conf",
                    SystemConstant.KAFKA_KERBEROS_PATH + File.separator + "krb5.conf");
        }
    }

    static {
        try {
            LOG.info("init kafkaProducerProperties");
            kafkaProducerProperties.put("bootstrap.servers", SystemConstant.BROKER_URL);
            kafkaProducerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProducerProperties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            // kafkaProducerProperties.put("batch.size", 16384);
            kafkaProducerProperties.put("batch.size", 1);// 16384
            kafkaProducerProperties.put("linger.ms", 1);
            kafkaProducerProperties.put("buffer.memory", 33554432);
            kafkaProducerProperties.put("acks", "0");
            kafkaProducerProperties.put("compression.type", "snappy");
            kafkaProducerProperties.put("topic.properties.fetch.enable", "true");
            if (SystemConstant.IS_KERBEROS.equals("true")) {
                kafkaProducerProperties.put("security.protocol", "SASL_PLAINTEXT");
                kafkaProducerProperties.put("sasl.kerberos.service.name", "kafka");
            }

            producer = new KafkaProducer<>(kafkaProducerProperties);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    public void deliver(DataPacket dp) throws Exception {
        try {
            /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream;
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(dp);
            byte[] bytes = byteArrayOutputStream.toByteArray();*/
            // 直接发送原始数据
            byte[] bytes = dp.getBody();
            producer.send(new ProducerRecord<>(topic, null, bytes));
        } catch (Exception e) {
            LOG.error(String.format("[%s]: message<%s>", "deliver", e.getMessage()));
            throw e;
        }
    }

    public static KafkaBroker getInstance() {
        return KafkaBroker.instance;
    }
}
