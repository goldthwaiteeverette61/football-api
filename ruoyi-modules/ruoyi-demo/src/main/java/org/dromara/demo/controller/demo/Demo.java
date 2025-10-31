//package org.dromara.demo.controller;
//
//import com.aliyun.oss.*;
//import com.aliyun.oss.common.auth.*;
//import com.aliyun.oss.common.comm.SignVersion;
//import com.aliyun.oss.model.PutObjectRequest;
//import com.aliyun.oss.model.PutObjectResult;
//import java.io.InputStream;
//import java.net.URL;
//
//public class Demo {
//
//    public static void main(String[] args) throws Exception {
//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-shenzhen.aliyuncs.com";
//        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
////        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
//        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider("LTAI5tGfrCwzmrrqSEuBvM2C","msEnVeJQqMLwKH2xHXtI85emyjYfgd");
////        credentialsProvider.setCredentials();
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "ruoyi-images";
//        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//        String objectName = "a/a.jpg";
//        // 填写网络流地址。
//        String url = "https://pics.17qcc.com/imgextra/product/202502/23/5734699643702.jpg";
//        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
//        String region = "cn-shenzhen";
//
//        // 创建OSSClient实例。
//        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
//        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
//        OSS ossClient = OSSClientBuilder.create()
//            .endpoint(endpoint)
//            .credentialsProvider(credentialsProvider)
//            .clientConfiguration(clientBuilderConfiguration)
//            .region(region)
//            .build();
//
//        try {
//            InputStream inputStream = new URL(url).openStream();
//            // 创建PutObjectRequest对象。
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
//            // 创建PutObject请求。
//            PutObjectResult result = ossClient.putObject(putObjectRequest);
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                + "a serious internal problem while trying to communicate with OSS, "
//                + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }
//}
