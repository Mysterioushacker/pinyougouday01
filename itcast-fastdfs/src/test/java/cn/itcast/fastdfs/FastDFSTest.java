package cn.itcast.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;

public class FastDFSTest {
    @Test
    public void test() throws Exception{
        //追踪服务器文件的路径
        String conf_filename = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();

        //设置全局的配置
        ClientGlobal.init(conf_filename);

        //创建trackerClient
        TrackerClient trackerClient = new TrackerClient();

        //创建trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();

        //创建storageServer
        StorageServer storageServer = null;

        //创建存储服务器客户端StorageClient
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);

        //上传文件
        /**
         * 参数 1：文件
         * 参数 2：文件的后缀
         * 参数 3：文件的属性信息
         * 返回结果：形如：
         * group1
         M00/00/00/wKgMqFmfUHiAcpaMAABw0se6LsY441.jpg
         */



    }
}
