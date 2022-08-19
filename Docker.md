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

### 构建应用的容器镜像
&emsp;&emsp; 构建应用程序需要使用Dockerfile，Dockerfile是一个基于文本的指令脚本，用于创建容器镜像。

        docker build -t getting-started .
        -t标记镜像的名称，"." 表示在当前目录下查找Dockerfile文件构建镜像。
        docker run -dp 3000:3000 getting-started

### 更换旧容器
&emsp;&emsp; 使用命令行移除旧容器，需要先停止容器，一旦停止后，就可以将其移除。

        docker ps                   获取容器ID列表
        docker stop <containerId>   停止容器
        docker rm <containerId>     删除容器

### 共享应用程序
&emsp;&emsp; 要共享docker镜像，必须使用Docker注册表。默认的注册表是Docker Hub。如果没有为镜像添加标签，Docker将使用latest。

        docker image ls     查看镜像列表
        docker login -u <userName>      登录Docker Hub
        docker tag getting-started YOUR-USER-NAME/getting-started       为镜像指定一个新的名称
        docker push YOUR-USER-NAME/getting-started      推送镜像到Docker Hub，如果要从Docker Hub拷贝值，可以删除tagname部分

### 持久化数据库
&emsp;&emsp; 每次启动容器都会清除待办事项列表。通过创建一个卷并将其挂载到存储数据的目录，可以持久化数据。
我们将卷视为简单的数据桶，Docker维护磁盘上的物理位置，只需要记住卷的名称，每次使用卷时，Docker都会确保提供正确的数据。

        docker run -d ubuntu bash -c "shuf -i 1-10000 -n 1 -o /data.txt && tail -f /dev/null" 启动一个ubuntu容器，并创建一个1到10000之间的随机数命名的文件
        docker exec <container-id> cat /data.txt    使用命令行来查看容器下的/data.txt文件
        docker rm -f <container-id>     删除容器
        docker volume create <volume-name>      创建卷
        docker run -dp 3000:3000 -v todo-db:/etc/todos getting-started  -v指定卷安装，启动容器，将命名卷挂载到/etc/todos
        docker volume inspect todo-db       查看命名卷的存储位置，MountPoint是磁盘位置

&emsp;&emsp; 命名卷(Named Volumes)和绑定挂载(Bind Mounts)是Docker两种主要的卷类型。

                                命名卷                     绑定挂载
        实例地址                Docker选择                  自己控制
        挂载示例                my-volume:/usr/local/data   /path/to/data:/usr/local/data
        用容器内容填充新卷        是                          否
        支持卷驱动程序           是                          否


        docker run -dp 3000:3000 \
        -w /app -v "$(pwd):/app" \
        node:12-alpine \
        sh -c "yarn install && yarn run dev"

        -w /app 表示设置工作目录或者命令将运行的当前目录
        -v "$(pwd):/app"    将容器中主机的当前目录绑定挂载到/app目录中
        sh -c "yarn install && yarn run dev"    使用sh启动一个shell并运行脚本。

### 多容器应用
&emsp;&emsp; 默认情况下，容器是独立运行的，对同一台机器上的其他进程或容器一无所知。我们需要通过忘了来让容器之间通信。
        
        docker network create todo-app      创建网络

        docker run -d \
        --network todo-app --network-alias mysql \
        --platform "linux/amd64" \
        -v todo-mysql-data:/var/lib/mysql \         我们这里使用了一个todo-mysql-data卷，Docker会自动帮我们创建一个命名卷。
        -e MYSQL_ROOT_PASSWORD=secret \
        -e MYSQL_DATABASE=todos \
        mysql:5.7

        docker exec -it <mysql-container-id> mysql -u root -p       连接到数据库

### Docker compose
&emsp;&emsp; Docker compose是一种用于帮助定义和共享多容器应用程序的工具。