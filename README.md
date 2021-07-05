## 功能

此app可读取目标应用所有数据：

1. 进程信息：内存占用、文件描述符、线程
2. 内部存储： data/包名/
3. 外部存储
4. SharedPreference
5. 数据库

## 原理

android:sharedUserId



## 配置

1. AndroidManifest.xml中配置与目标应用相同android:sharedUserId
2. TargetApp.TARGET_PACKAGE_NAME 常量设置为目标应用包名
3. 使用目标应用签名进行打包