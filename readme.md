# hadoop-pan
基于Hadoop的网盘

程序会自动在Hbase创建表
src\main\java\com\example\d\dao\Init.java
```
hbase建表语句
create 'user','user_info','user_file','user_dir','user_share','user_space'
create 'mail','mail_info'
create 'mail_user','mail_user_info'	
create 'file','file_info'
create 'file_MD5','MD5_info'
create 'dir','dir_info','dir_file'
create 'ticket','ticket_info'
create 'share','share_info'
create 'analysis','analysis_info'
create 'Admin','info'
```

修改src\main\java\com\example\d\dao\conn\目录下的HdfsConn.java与HbaseConn.java文件的配置信息。
填写自己的Hadoop和Hbase的服务地址、端口号。
src\main\java\com\example\d\dao\conn\HdfsConn.java
```
private HdfsConn() {
    try {
        configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://node:8020/");//Hadoop地址
        System.setProperty("HADOOP_USER_NAME", "root");//用户名
        configuration.set("dfs.permissions", "false");
        configuration.set("dfs.support.append", "true");
        fileSystem = FileSystem.get(configuration);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```
src\main\java\com\example\d\dao\conn\HbaseConn.java
```
private HbaseConn() {
  try {
      Configuration hconf = new Configuration();
      Configuration conf = HBaseConfiguration.create(hconf);
      conf.set("hbase.zookeeper.quorum","node");  //hbase 服务地址
      conf.set("hbase.zookeeper.property.clientPort","2181"); //端口号
      conn = ConnectionFactory.createConnection(conf);
      admin = conn.getAdmin();
  } catch (Exception e) {
      e.printStackTrace();
  }
}
```
