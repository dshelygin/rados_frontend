package s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import utils.Configuration;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dshelygin on 12.09.2017.
 * Осуществляет подключение к хранилищу S3 через radosgw
 * Выполняет функции по получению/модификации объектов и их метаданных в хранилище
 * singleton
 */
public enum S3Connection {

    INSTANCE;

    private String accessKey = Configuration.INSTANCE.getAccessKey();
    private String secretKey = Configuration.INSTANCE.getSecretKey();

    private AmazonS3 conn = null;

    public void connect() {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        clientConfig.setSignerOverride("S3SignerType");
        AwsClientBuilder.EndpointConfiguration endpoint =
                                                                //todo вынести в конфиг
                    new AwsClientBuilder.EndpointConfiguration("10.200.0.161:7480","local");

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        builder.setClientConfiguration(clientConfig);
        builder.withCredentials(new AWSStaticCredentialsProvider(creds));
        builder.withEndpointConfiguration(endpoint);
        conn = builder.build();
    }


    /**
     * Возвращает объект из корзины
     */


    public S3Object getS3Object(String bucket, String key) {
        try {
            GetObjectRequest req = new GetObjectRequest(bucket,key);
            return conn.getObject(req);
        } catch (AmazonS3Exception e) {
            Logger.getLogger(Configuration.class.getName()).log(Level.FINE,e.toString());
            return null;
        }
    }


    /**
     * Возвращает объект из корзины в виде потока
     */

    public S3ObjectInputStream getS3ObjectAsStream(String bucket, String key) {
        S3Object s3object = getS3Object(bucket, key);
        if (s3object == null) {
            return null;
        } else {
            return s3object.getObjectContent();
        }
    }


    /**
     * Возвращает метаданные объекта
     */

    private ObjectMetadata getS3ObjectMeta(String bucket, String key) {
        try {
            GetObjectMetadataRequest req = new GetObjectMetadataRequest(bucket,key);
            return conn.getObjectMetadata(req);
        } catch (AmazonS3Exception e) {
            Logger.getLogger(Configuration.class.getName()).log(Level.FINE, e.toString());
            return null;
        }
    }

    /**
     * Возвращает значение заданного поля пользовательских метаданных объекта
     */

    public String getS3ObjectMetaParameter(String bucket, String key, String paramName) {
        ObjectMetadata metadata = getS3ObjectMeta(bucket,key);
        if (metadata != null) {
            Map<String, String> mData = metadata.getUserMetadata();
            return mData.get(paramName);
        } else {
            return null;
        }

    }

    /**
     *  сохраняет значение в заданном поле пользовательских метаданных объекта
     */
    public void setS3ObjectMetaParameter(String bucket, String key, String paramName, String paramValue) {
        ObjectMetadata metadata = getS3ObjectMeta(bucket,key);
        if (metadata !=null) {
            metadata.addUserMetadata(paramName, paramValue);

            CopyObjectRequest request = new CopyObjectRequest(bucket, key, bucket, key)
                    .withNewObjectMetadata(metadata);

            conn.copyObject(request);
        }

    }



}

