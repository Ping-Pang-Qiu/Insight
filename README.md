帮助开发者查看目标应用所有数据
[Github](https://github.com/Ping-Pang-Qiu/Insight)

## 优势
使用独立App查看目标应用所有数据。

只在目标应用添加一行代码：
AndroidManifest.xml中配置相同android:sharedUserId

## 原理
android:sharedUserId
两个应用共用一个userid，可以互相读取所有文件，共享权限。

## 功能
此app可读取目标应用所有数据：
1. 进程信息：内存占用、文件描述符、线程
2. 内部存储： data/包名/
3. 外部存储
4. SharedPreference
5. 数据库

## 源码配置
源码修改配置，然后打包
1. AndroidManifest.xml中配置与目标应用相同android:sharedUserId（**目标应用的唯一修改点**）
2. TargetApp.TARGET_PACKAGE_NAME 常量设置为目标应用包名
3. 使用目标应用签名进行打包


## 示例
https://img-blog.csdnimg.cn/20210707231824927.jpg

https://img-blog.csdnimg.cn/20210705234423667.jpg



