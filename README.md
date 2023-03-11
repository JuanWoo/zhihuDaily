[知乎日报Web版][ZhihuDaily url]（网站已下线，具体原因见代码页面说明）
=================
本地化运行
-----------------
1. [点我下载][jdk url]JAVA运行环境并安装。如已有JAVA 8或以上环境可跳过此步
2. [点我下载][exe url]已经打包好的zip包
3. 解压到任意目录，得到ZhihuDaily文件夹
4. windows系统双击运行 ZhihuDaily\start.bat； linux系统，在ZhihuDaily目录执行命令 ./jfinal.sh start
5. 访问 [localhost][local url] 即可

自主编译
-----------------
1. 配置好Maven、JAVA环境变量
2. 按需修改代码
3. 在项目根目录执行cmd命令：mvn clean package
4. 项目根目录\target\ZhihuDaily-release.zip 即修改后的程序

声明
-----------------
本项目所有文字图片等稿件内容均由 [知乎][zhihu url] 提供，获取与共享之行为或有侵犯知乎权益的嫌疑。若被告知需停止共享与使用，本人会及时删除整个项目。
<br>请您了解相关情况，并遵守知乎协议。

感谢
-----------------
  - [JFinal][JFinal url]
  - [Bootstrap][Bootstrap url]
  - [FreeMarker][FreeMarker url]
  

[JFinal url]: <http://www.jfinal.com/>
[FreeMarker url]: <http://freemarker.incubator.apache.org/>
[Bootstrap url]: <http://www.bootcss.com/>
[ZhihuDaily url]: <http://zhihudaily.me/>
[zhihu url]: <https://www.zhihu.com/>
[exe url]: <https://github.com/JuanWoo/zhihuDaily/blob/master/exe/ZhihuDaily-release.zip>
[jdk url]: <https://www.oracle.com/cn/java/technologies/javase/javase8-archive-downloads.html>
[local url]: <http://localhost/>
