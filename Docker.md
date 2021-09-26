## Docker notes.

* [1.Docker简介](#1)


<h2 id="1">1.Docker简介</h2>
&emsp;&emsp; Docker能够将应用程序与基础设施分开，以便可以快速交付软件。Docker提供了在称为容器的隔离环境中打包和运行应用程序的能力，
隔离性和安全性允许在给定主机上同时运行多个容器。容器是轻量级的，包含运行应用程序所需的一切，无需依赖主机上当前安装的内容。

        Docker提供工具和平台来管理容器的生命周期：
            1，使用容器开发应用程序及其支持组件。
            2，容器成为分发和测试应用程序的单元。
            3，就绪后，将应用程序作为容器或编排服务部署到生产环境中。
            
        docker run -d -p 80:80 docker/getting-started 
        可以组合单个字符标志来缩短完整命令：docker run -dp 80:80 docker/getting-started
        
        -d  以分离模式在后台运行容器。
        -p 80:80  将主机的80端口映射到容器的80端口。
        docker/getting-started  要使用的镜像。
