package com.pinyougou.common.util;

import org.csource.fastdfs.*;

public class FastDFSClient {

    private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageClient storageClient = null;

    /**
     * @param trackerConfFilePath 追踪服务器的配置信息文件
     * @throws Exception
     */
    public FastDFSClient(String trackerConfFilePath) throws Exception {
        if (trackerConfFilePath.contains("classpath:")) {
            trackerConfFilePath = trackerConfFilePath.replace("classpath:", this.getClass().getResource("/").getPath());
        }
        //设置全局的配置
        ClientGlobal.init(trackerConfFilePath);

        //创建trackerClient
        trackerClient = new TrackerClient();

        //创建trackerServer
        trackerServer = trackerClient.getConnection();

        //创建存储服务器客户端StorageClient
        storageClient = new StorageClient(trackerServer, null);
    }

    /**
     * 上传文件
     * @param file_buff 文件字节流
     * @param file_ext_name 文件拓展名（后缀）；如：jpg
     * @return 完整可访问路径
     * @throws Exception
     */
    public String uploadFile(byte[] file_buff, String file_ext_name) throws Exception {
        String url = "";
        String[] upload_file = storageClient.upload_file(file_buff, file_ext_name, null);
        if (upload_file != null && upload_file.length > 1) {
            //获取存储服务器信息
            String groupName = upload_file[0];
            String filename = upload_file[1];
            ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, groupName, filename);

            //组合可以访问的路径
            url = "http://" + serverInfos[0].getIpAddr() + "/" + groupName + "/" + filename;
        }
        return url;
    }
}
