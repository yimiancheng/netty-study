### netty Websocket

#### netty-websocket-server
   - channel 15s 未读操作，close channel
   - channel 5s 未写操作，发送 ping 消息
   - 服务端广播消息
#### netty-websocket-client
   - 客户端 (5s 未读 || 10s 未写) 发送pin消息 
   - 客户端发送消息到服务端
   - 客户端线程—自动重连
   - 客户端线程—发送自定义心跳包

### netty http
   - 可解析 customer 的 body 参数
   - 可解析 post put delete 的 form-data 参数
   - 可解析 get 的 uri 参数
   - 业务处理流程在线程池中处理，可以控制最大并发

- [ ] 完善文档
- [ ] 完善项目
- [ ] 测试
   
`参考git@github.com:looly/loServer.git`

