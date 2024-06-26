* [1.shell概述](#1)
* [2.文件系统中跳转](#2)
* [3.操作文件和目录](#3)
* [4.使用命令](#4)
* [5.重定向](#5)
* [6.shell特性](#6)
* [7.键盘操作](#7)
* [8.权限](#8)
* [9.进程](#9)
* [10.shell环境](#10)

<h2 id="1">1.Shell概述</h2>
&emsp;&emsp; bash是Bourne Again SHell的首字母缩写，bash是最初Unix上由Steve Bourne写成
shell程序sh的增强版。如果terminal提示符最后一个字符是"#"，而不是"$"，那么这个终端会话拥有超级用户权限，
这意味着我们或者是以root用户的身份登录，或者是我们选择的终端提供管理员权限。

<h2 id="2">2.文件系统中跳转</h2>
&emsp;&emsp; 类Unix操作系统，总是只有一个单一的文件系统树，不管有多少个磁盘或存储设备连接到计算机上，
存储设备都是挂载到目录树的各个节点上。

### 绝对路径与相对路径
&emsp;&emsp; 绝对路径开始于根目录，一直到达期望的目录或文件，用开头的"/"表示，如/usr/bin。
相对路径开始于工作目录，我们在文件系统树中用一对特殊符号来表示相对位置，"."指工作目录，
".."指工作目录的父目录，在几乎所有的情况下，可以省略"./"，它是隐含的。

### 长格式输出
&emsp;&emsp; "-l"选项以长格式输出，
        
        drwxr-xr-x   20 wlx  staff   640 Dec 22 14:15 sunspot
        lrwxrwxrwx 1 root root 11 2007-08-11 07:34 libc.so.6 -> libc-2.6.so
        drwxr-xr-x 对于文件的访问权限。第一个字符指明文件类型，"-"指普通文件，"d"指目录，"l"表示符号链接。
            其后的3个字符是文件所有者的访问权限，再其后3个字符是文件所属组中成员的访问权限，
            最后3个自符是其它所有人的访问权限。w表示写，r表示读，r表示可执行，-表示没有权限。
        20      表示文件的硬链接数目。
        wlx     表示文件所有者的用户名。
        staff   表示文件所属用户组的名字。
        640     表示文件的子节数。
        640 Dec 22 14:15    表示上次修改文件的时间和日期。
        sunspot 表示文件名。

<br>
&emsp;&emsp; 以"."字符开头的文件名是隐藏文件，ls不能列出它们，用ls -a命令就可以了。
        
        Linux一些目录：
        /               根目录。
        /bin            包含系统启动和运行所必须的二进制程序。
        /boot           包含内核、初始RAM磁盘映像和启动加载程序。
        /dev            包含设备结点的特殊目录。在这个目录里，内核维护着所有设备的列表。
        /etc            包含所有系统层面的配置文件。这个目录中的任何目录都应该是可读的文本文件。
        /home           系统会在/home下给每个用户分配一个目录。普通用户只能在自己的目录下写文件。
        /lib            包含核心系统程序所使用的共享库文件。
        /lost+found     每个使用Linux文件系统的格式化分区或设备，都会有这个目录。当部分恢复一个损坏的文件系统时，会用到这个目录。
        /media          会包含可移动介质的挂载点。USB驱动器等介质连接到计算机后，会自动挂载到这个目录结点下。
        /mnt            在早些的Linux系统中，/mnt目录包含可移动介质的挂载点。
        /opt            用来安装可选的软件，主要用来存储可能安装在系统中的商业软件产品。
        /proc           不是真正的文件系统，是一个由Linux内核维护的虚拟文件系统。包含的文件是内核的窥视孔，文件是可读的。
        /root           root账户的home目录。
        /sbin           包含系统二进制文件。是完成重大系统任务的程序，通常为超级用户保留。
        /tmp            用来存储由各种程序创建的临时文件的地方。一些配置导致系统每次重启时，都会清空这个目录。
        /usr            Linux系统中可能最大的一个目录，包含普通用户所需要的所有程序和文件。
        /usr/bin        包含系统安装的可执行程序。
        /usr/lib        包含由/usr/bin目录中的程序所用的共享库。
        /usr/local      非系统发行版自带程序的安装目录。
        /usr/sbin       包含许多系统管理程序。
        /usr/share      包含许多由/usr/bin目录中的程序使用的共享数据。
        /usr/share/doc  大多数安装在系统中的软件会包含一些文档，按软件包分类的文档保存在这。
        /var            除了/tmp和/home之外，目前其它的文件都是静态的，/var存放的是动态文件。数据库、假脱机文件、用户邮件等。
        /var/log        包含日志文件、各种系统活动的记录。

<h2 id="3">3.操作文件和目录</h2>
&emsp;&emsp; shell频繁的使用文件名，shell提供了特殊字符来快速指定一组文件名，这些字符叫做通配符。

        通配符：
        *               匹配任意多个字符(包括零个或一个)。
        ？              匹配任意一个字符(不包括零个)。
        [characters]    匹配任意一个属于字符集中的字符。
        [!characters]   匹配任意一个不是字符集中的字符。
        [[:class:]]     匹配任意一个属于指定字符类中的字符。

        通配符范例：
        *               所有文件。
        g*              文件名以"g"开头的文件。
        b*.txt          以"b"开头，中间有零个或任意多个字符，并以".txt"结尾的文件。
        Data???         以"Data"开头，其后紧接着3个字符的文件。
        [abc]*          文件名以"a"，"b"，或者"c"开头的文件。
        BACKUP.[0-9][0-9][0-9]  以"BACKUP."开头，并紧跟着3个数字的文件。
        [[:upper:]]*    以大写字母开头的文件。
        [![:digit:]]*   不以数字开头的文件。
        *[[:lower:]123]   文件以小写字母结尾，或者以"1"，或"2"，或"3"结尾的文件。

        字符类：
        [:alnum:]       匹配任意一个字母或数字。
        [:alpha:]       匹配任意一个字母。
        [:digit:]       匹配任意一个数字。
        [:lower:]       匹配任意一个小写字母。
        [:upper:]       匹配任意一个大写字母。

### 硬链接和符号链接
&emsp;&emsp; 每个文件默认会有一个硬链接，这个硬链接给予文件名字。每创建一个硬链接，就为一个文件创建了一个额外的目录项。
硬链接两个局限性：1，一个硬链接不能关联它所在文件系统之外的文件，一个硬链接不能关联与硬链接本身不在同一个磁盘分区上的文件。
2，一个硬链接不能关联一个目录。

<br>
&emsp;&emsp; 符号链接生效，是通过创建一个特殊类型的文件，这个文件包含一个关联文件或目录的文本指针。

<h2 id="4">4.使用命令</h2>
&emsp;&emsp; 命令是以下四种形式之一：1，一个可执行程序。2，一个内建于shell自身的命令。3，一个shell函数。4，一个命令别名。

<h2 id="5">5.重定向</h2>
&emsp;&emsp; 许多程序都会产生某种输出，这种输出通常由两种类型组成：1，程序运行结果，标准输出(stdout)。2，状态和错误信息，标准错误(stderr)。
">"重定向符后接文件名，将标准输出重定向到除屏幕以外的另一个文件， > file 可以清空文件内容。">>" 重定向符将输出结果添加到文件内容之后，
如果文件不存在，文件会被创建。

        shell内部将标准输入、输出、错误，分别称为文件描述符：0、1、2。
        ls -l /bin/usr 2> ls-error.txt  表示将标准错误重定向到ls-error.txt文件中。
        ls -l /bin/usr > ls-output.txt 2>&1 表示将标准输出和标准错误都重定向到一个文件中。
        ls -l /bin/usr &> ls-output.txt 现在的bash版本，可以用这种更精简的方法，重定向标准输出和错误到文件中。
        ls -l /bin/usr 2> /dev/null 扔掉错误输出，"/dev/null"特殊文件是系统设备，叫位存储桶，可以接收输入，并对输入不做任何处理。

        cat < lazy_dog.txt 使用"<" 重定向操作符，将标准输入源从键盘改到文件。

### 管道线
&emsp;&emsp; 命令从标准输入读取数据，并输送到标准输出的能力，被一个称为管道线的shell特性所利用。
使用"|"管道操作符，一个命令的标准输出可以通过管道送至另一个命令的标准输入。
        
        command1 | command2 
        ls -l /usr/bin | less 用less来一页页地显示任何命令的输出，命令把它的运行结果输送到标准输出。

### 过滤器
&emsp;&emsp; 把几个命令放在一起组成一个管道线，以这种方式使用的命令称为过滤器。
过滤器接受输入，以某种方式改变它，然后输出它。

        ls /usr/bin /bin | sort | less
        ls /bin /usr/bin | sort | uniq | less 使用uniq删除重复的行。
        ls /bin /usr/bin | sort | uniq | grep zip 找到包含"zip"单词的所有文件。
        ls /usr/bin | tee ls.txt | grep zip tee从标准输入读取数据，并输出到标准输出和文件。

<h2 id="6">6.shell特性</h2>
### 字符展开
&emsp;&emsp; 当执行命令之前，bash会将字符转换为别的字符，这个过程叫(字符)展开。
    
        echo *      打印当前工作目录下的文件名字。

### 路径展开
&emsp;&emsp; 通配符依赖的工作机制叫路径名展开。
    
        echo D*     打印所有D开头的文件。
        echo [[:upper:]]*       打印大写字符开头的文件。
        
        ls -d .* | less
        ls -d .[!.]?*       打印以"."开头，且第二个字符不为"."，且至少包含一个字符的文件。
        ls -A       打印隐藏文件。

### 算术表达式展开
&emsp;&emsp; shell在展开中执行算术表达式。
    
        $((expression)) 支持：+、-、*、/、%、**(取幂)。
        echo $((2 + 2))

### 花括号展开
&emsp;&emsp; 通过花括号，可以从一个包含花括号的模式中创建多个文本字符串。

        echo Front-{A,B,C}-Back     会分别打印出3个字符串。
        echo Number_{1..5}      
        echo {Z..A}
        echo a{A{1,2},B{3,4}}b

### 命令替换
&emsp;&emsp; 命令替换允许我们把一个命令的输出作为一个展开模式来使用。
    
        echo $(ls)
        ls -l $(which cp)
        ls -l `which cp`    倒引号等同于$()

### 引号
&emsp;&emsp; 双引号中特殊字符都失去特殊含义，例外：$、\、`(倒引号)。这意味着参数展开、算术展开、命令替换仍然执行。
单引号禁止所有的展开。可以使用"\"来转义字符。
    
        \a      响铃。
        \b      退格符。
        \n      新的一行。
        \r      回车符。
        \t      制表符。

<h2 id="7">7.键盘操作</h2>
### 移动光标

        Ctrl-a      移动光标到行首。
        Ctrl-e      移动光标到行尾。
        Ctrl-f      光标前移一个字符。
        Ctrl-b      光标后移一个字符。
        Alt-f       光标前移一个字。
        Alt-b       光标后移一个字。
        Ctrl-l      清空屏幕，移动光标到左上角。clear命令完成同样功能。

### 修改文本
        
        Ctrl-d      删除光标位置的字符。
        Ctrl-t      光标位置的字符和光标前面的字符互换位置。
        Alt-t       光标位置的字和其前面的字互换位置。
        Alt-l       把从光标位置到字尾的字符转换为小写字母。
        Alt-u       把从光标位置到字尾的字符转换为大写字母。

### 剪切和粘贴文本

        Ctrl-k      剪切从光标到行尾的文本。
        Ctrl-u      剪切从光标到行首的文本。
        Alt-d       剪切从光标到词尾的文本。
        Alt-Backspace       剪切从光标位置到词头的文本。
        Ctrl-y      把剪切环中的文本粘贴到光标位置。

### 历史命令
&emsp;&emsp; home目录.bash_history文件中，保存一个已经执行过的命令的历史列表。

        history | less 
        history | grep /usr/bin

        Ctrl-p      移动到上一个历史条目。
        Ctrl-n      移动到下一个历史条目。
        Alt-<       移动到历史列表开头。
        Alt->       移动到历史列表结尾。
        Ctrl-r      反向增量搜索。
        Alt-p       反向搜索，非增量搜索。
        Alt-n       向前搜索，非增量。
        Ctrl-o      执行历史列表中的当前项，并移动到下一个。

<h2 id="8">8.权限</h2>
&emsp;&emsp; 对于文件和目录的访问权力，是根据读访问、写访问、执行来定义的。

        -       一个普通文件。
        d       一个目录。
        l       一个符号链接。
        c       一个字符设备文件。按照字节流来处理数据的设备。
        b       一个块设备文件。按照数据块来处理数据的设备。

### chmod更改文件模式
&emsp;&emsp; chmod能更改文件或目录的模式，只有文件的所有者或超级用户才能更改文件或目录的模式。
chmod支持两种不同的方法来改变文件模式：八进制数字表示法或符号表示法。

        0       000     ---
        1       001     --x
        2       010     -w-
        3       011     -wx
        4       100     r--
        5       101     r-x
        6       110     rw-
        7       111     rwx

        对象类型：
        u       user的简写，指文件或目录的所有者。
        g       用户组。
        o       others的简写，指其他所有的人。
        a       all的简写，是u、g、o三者的联合。

        符号表示法：
        u+x     为文件所有者添加可执行权限。
        u-x     删除文件所有者的可执行权限。
        +x      为文件所有者、用户组和其他所有人添加可执行权限。等价a+x。
        o-rw    除了文件所有者、用户组，删除其他人的读权限和写权限。
        go=rw   给文件组和文件组、所有者以外的人，读写权限，且如果有执行权限，则移除。
        u+x,go=rw   给文件拥有者执行权限，并给文件组和其他人读和执行的权限。多中设定可以用逗号分开。

### chown更改文件所有者和用户组
    
        chown [owner][:[group]] file    这个命令使用时需要超级用户权限。

<h2 id="9">9.进程</h2>
&emsp;&emsp; 当系统启动时，内核先把自己的活动初始化为进程，然后运行init程序。
再运行一系列称为init脚本的shell脚本(位于/etc)。一个程序发动另一个程序被称为一个父进程可以产生一个子进程。

### ps查看进程

        ps      查看进程(process status)    
            PID(进程ID) TTY(进程的控制终端) TIME(进程消耗的CPU时间数量) CDM
        ps x    展示所有进程，不管由什么终端控制。
            PID     TTY     STAT        TIME        COMMAND
        ps aux
            USER PID %CPU %MEM VSZ(虚拟内存大小) RSS(物理内存大小) TTY STAT START(进程启动的时间) TIME COMMAND

        STAT状态值：
            R       运行中，进程正在运行或准备运行。
            S       正在睡眠，进程在等待一个事件。
            D       不可中断睡眠，进程在等待I/O。
            T       已停止。
            Z       一个死进程或僵尸进程，这是一个已经终止的子进程，但它的父进程还没清空它。
            <       一个高优先级进程，可能会授予一个进程更多的资源。
            N       低优先级进程，只有高优先级进程被服务后，才会得到处理器时间。

### top动态查看进程
&emsp;&emsp; ps命令查看的是执行时刻的机器状态快照。top程序以进程活动顺序，显示连续更新的系统进程列表。
top的显示结果由两部分组成，最上面的是系统概要，下面是进程列表。

### 后台执行进程
&emsp;&emsp; 为了启动一个程序并让它立即在后台运行，我们在程序命令之后，加上"&"字符。
shell的任务控制功能给出了一种列出从我们终端中启动了的任务的方法，执行jobs可以看到这个输出列表。

        xlogo &

### 进程返回到前台
&emsp;&emsp; 一个在后台运行的进程对于键盘输入是免疫的，而且不能用快捷键来中断，可以使用fg命令来让进程返回前台。

        fg %1       fg跟随一个百分号和任务序号，如果只有一个后台任务，那么%1是可有可无的。

### 停止一个进程
&emsp;&emsp; 我们可以使用Ctrl-z来停止一个前台进程，这么做是为了允许前台进程被移动到后台，而不是终止它。
使用fg命令可以恢复程序到前台运行，bg命令可以把程序移到后台。

        bg %1       bg跟fg一样可以跟一个任务序号，如果只有一个任务的话，%1参数是可选的。

### 信号
&emsp;&emsp; kill命令用来给程序发送信号。killall命令可以给多个进程发送信号。
kill命令和killall命令一样，都必须拥有超级用户权限才能给不属于你的进程发送信号。

        kill [-signal] PID
        killall [-u user] [-signal] name...

        pstree      输出一个树型的进程列表，这个列表展示进程间的父子关系。
        vmstat      输出一个系统资源使用快照，包括内存、交换分区、磁盘I/O。 vmstat 5表示：每5秒更新一次。
        xload       一个图形界面程序，可以画出系统负载随时间变化的图形。
        tload       与xload相似，但是在终端中画出图形，使用Ctrl-c来终止输出。

<h2 id="10">10.shell环境</h2>
&emsp;&emsp; shell在shell会话中保存大量信息，这些信息被称为shell的环境。

        printenv        打印部分或所有的环境变量
        set             设置shell选项
        export          导出环境变量，让随后执行的程序知道
        alias           创建命令别名


        pwd (print working directory): 显示当前工作目录。
        ls: 列出一个目录包含的文件及子目录。ls [dirName] 查看多个对应目录下的文件列表和子目录列表。
        ls -a: 可以列出"."开头的隐藏文件。
        ls -l: 列出目录包含的文件及子目录，以长模式输出。
        ls -t: 按照文件修改时间的先后来排序。
        cd: 更改当前工作目录。cd 更改目录到home目录。  
        cd -: 更改目录到你先前的工作目录。
        cd ~user_name: 更改目录到该用户的home目录。
        file filename: 打印文件内容的简单描述。
        less filename: 浏览文本文件，允许前后滚动，如果内容多于一页，可以上下滚动文件，按q退出less程序。
        mkdir [directory]: 创建目录。可以一次创建多个目录。
        cp item1 item2: 复制单个文件或目录"item1"，到文件或目录"item2"。
        cp [item] directory: 复制多个项目到一个目录下。
        mv item1 item2: 把文件或目录"item1"移动或重命名为"item2"。
        mv [item] directory: 把一个或多个条目从一个目录移动到另一个目录中。
        rm [item]: 移除一个或多个文件或目录。
        ln file link: 创建硬链接。
        ln -s item link: 创建符号链接。
        type command: 显示命令的类别。
        which command: 显示一个可执行程序的位置。只对可执行程序有效，不包括内建命令和命令别名。
        help command: 得到shell内建命令的帮助文档。
        command --help: 显示用法信息，很多可执行程序支持一个--help选项，用来显示命令所支持的语法和选项说明。
        man command: 显示程序手册页。许多被命令行使用的可执行程序，提供了一个正式的文档，叫做手册或手册页。
        apropos command: 显示适当的命令。
        whatis: 显示匹配特定关键字的手册的名字和一行命令说明。
        info command: 显示程序Info条目。
        alias aliasCommand='command': 用别名创建自己的命令。alias foo='cd /usr; ls; cd -'，多个命令用";"分割。
        unalias aliasCommand: 删除别名。
        alias: 查看所有定义在系统环境中的别名。
        cat [file]: 读取一个或多个文件，然后复制它们到标准输出。cat movie.mpeg.0* > movie.mpeg 将多个文件连接起来。
        wc file: 打印行数、字数、字节数。
        grep pattern [file]: 打印匹配行。
        head -n number file: 打印文件开头前n行。默认打印10行。
        tail -n number file: 打印文件结尾后n行。默认打印10行。
        chmod mode file: 更改文件模式。
        chown owner:group file: 更改文件所有者和用户组。
        passwd user: 更改用户密码。
        ps: 查看进程。
        kill [-signal] PID: 用来给给程序发送kill信号。
        kill -u user -signal [name]: 给匹配特定程序或用户名的多个进程发送信号。



        ls: 列出目录包含的文件及子目录。
            选项：     长选项：        描述：
            -a        --all          列出所有文件，包含隐藏文件。
            -d        --directory    把这个选项与-l结合使用，可以看到指定目录的详细信息，而不是目录中的内容。
            -F        --classify     在每个所列出的名字后加上一个指示符，名字是目录，则会加上一个'/'字符。
            -h        --human-readable 当以长格式列出时，以人们可读的格式而不是字节数来显示文件的大小。
            -l                       以长格式显示结果。
            -r        --reverse      以相反的顺序来显示结果，通常ls输出结果按照字母生序排列。
            -S                       命令输出结果按照文件大小排序。
            -t                       按照修改时间来排序。

        less：浏览文件内容。less是more的改进版，less属于页面调度器类程序，more只能向前翻页，less允许前后翻页，还有其它很多特性。
            Page UP or b        向上翻滚一页。
            Page Down or space  向下翻滚一页。
            UP Arrow            向上翻滚一行。
            Down Arrow          向下翻滚一行。
            G                   移动到最后一行。
            1G or g             移动到开头一行。
            /characters         向前查找指定的字符串。
            n                   向前查找下一个出现的字符串，这个字符串是之前所指定查找的。
            h                   显示帮助屏幕。
            q                   退出less程序。

        cp：复制文件和目录。
            -a      --archive       复制文件和目录，以及它们的属性，包括所有权和权限。
            -i      --interactive   在重写已存在文件之前，提示用户确认。不指定cp会默认重写文件。
            -r      --recursive     递归地复制目录及目录中的内容。
            -u      --update        复制文件时，仅复制目标目录中不存在的文件，或者文件内容新于目标目录中已经存在的文件。
            -v      --verbose       显示翔实的命令操作信息。
            
        mv: 移动和重命名文件。
            -i      --interactive       在重写一个已经存在的文件时，提示用户确认信息。不指定这个选项，会默认重写文件内容。
            -u      --update            移动文件时，仅移动目标目录中不存在的文件，或者文件内容新于目标目录中已经存在的文件。
            -v      --verbose           操作mv时，显示翔实的操作信息。

        rm: 删除文件和目录。
            -i      --interactive       在删除已存在的文件前，提示用户确认信息。如不指定，rm会默默删除文件。
            -r      --recursive         递归的删除文件，如果目录包含子目录，那么子目录也会被删除。
            -f      --force             忽视不存在的文件，不显示提示信息。这选项覆盖了"--interactive"选项。
            -v      --verbose           在执行命令时，显示翔实的操作信息。

        uniq: 忽略重复行。
            -d      查看重复的行。

        wc: 打印行数、字数、字节数。
            -l      只打印行数。
        
        grep: 打印匹配行。
            -i      忽略大小写。
            -v      只打印不匹配的行。

        tail: 打印文件结尾后n行。
            -f      继续监测这个文件，当内容添加到文件后，会立即出现在屏幕上。
        
        kill: 杀死程序。
            1       HUP     挂起(hangup)，守护进程使用这个信号来重新初始化，收到这个信号后，进程会重新启动，并重新读取它的配置文件。
            2       INT     中断，跟Ctrl-c一样的功能，由终端发送。
            9       KILL    杀死，KILL信号不被发送到目标程序，而是内核立即终止这个进程。无法做清理或保存工作。
            15      TERM    终止，这个kill命令默认发送的信号，如果程序还活着，那么会终止。
            18      CONT    继续，在一个停止信号后，这个信号会恢复进程的运行。
            19      STOP    停止，这个信号导致进程停止而不是终止，这个信号不被发送到目标进程，因此不能被忽略。
            3       QUIT    退出。
            11      SEGV    段错误(segmentation violation)，如果程序非法使用内存就会发送这个信号。
            20      TSTP    终端停止(terminal stop)，当按下Ctrl-z，终端会发送这个信号。
            28      WINCH   改变窗口大小(window change)，改变窗口大小时，系统会发送这个信号。

### 内存分析
&emsp;&emsp; 我们通常使用jmap来查看内存详情。

    top命令的使用方式 top [-d number] | top [-bnp]
        -d number   表示top命令显示的页面更新一次的间隔，默认是5s
        -b          以批次的方式执行top
        -n          与-b配合使用，表示需要进行几次top命令的输出结果
        -p          指定特定的pid进程号进行观察


    top命令显示的页面还可以输入以下按键执行相应的功能
        ?           显示在top中可以输入的命令
        P           以CPU的使用资源排序显示
        M           以内存的使用资源排序显示
        N           以pid排序显示
        T           由进程使用的时间累计排序显示
        k           给某一个pid信号，可以用来杀死进程
        r           给某一个pid重新定制一个nice值，即优先级
        q           退出top，用ctrl + c 也可以退出

    拿到java进程的pid后，我们可以查看进程内存占用top n的对象
        /Library/Java/JavaVirtualMachines/jdk1.8.0_311.jdk/Contents/Home/bin/jmap -histo ${pid} | head -n 50

    dump内存信息
        ./jmap -dump:format=b,file=/tmp/mem.hprof ${pid}

    使用Eclipse Memory Analyzer 导入hprof文件，可分析内存占用情况

### GC分析

    采集到的gc log可以使用gcviewer来进行gc情况分析

### 火焰图分析
&emsp;&emsp; perf性能事件主要分三类：1，Hardware Event由PMU部件产生，在特定条件下探测性能事件是否发生以及发生的次数。
2，Software Event是内核产生的事件，分布在各个功能模块中，统计和操作系统相关性能事件。3，TracePoint Event是内核中静态
tracepoint所触发的事件，这些tracepoint用来判断程序运行期间内核的行为细节。

    火焰图的采集分三步：
        1，采集栈信息
        2，折叠栈信息
        3，flamegraph.pl

    栈信息采集工具：
        Linux:  perf、eBPF、SystemTap、ktap
        FreeBSD: DTrace
        Mac OS X: Instruments
        Windows: Xperf.exe、WPA、PerfView

    perf使用：
        1，先安装perf           apt install linux-tools-common
        2，测试perf是否可用      perf record -F 99 -a -g -- sleep 10 执行完会在执行目录产生perf.data

    perf命令:
        perf bench      perf内置的benchmark，包含两套针对调度器和内存管理子系统的benchmark
        perf list       查看当前软硬件环境支持的性能事件 主要三类：Hardware Event、Software Event、Tracepoint Event。
        perf stat       通过精简的方式提供被调试程序运行的整体情况和汇总数据
        perf top        实时显示当前系统的性能统计信息。
        perf record     记录单个函数级别的统计信息，并使用perf report来显示统计结果
        perf report     读取perf record生成的perf.data文件，并显示分析数据
        perf probe      能够动态地在想查看的地方插入动态监测点
        perf sched      提供了许多工具来分析内核CPU调度器的行为，可用来识别和量化调度器延迟的问题
        perf sched map  显示了所有CPU和上下文切换事件
        perf sched script   显示调度相关的事件
        perf sched replay   重放perf.data文件中记录的调度场景
        perf probe      用于定义动态检查点

    使用perf来抽取60S 99赫兹 栈样本，包括用户和内核栈，所有的进程：
        perf record -F 99 -a -g -- sleep 60

    perf record:
        -a          --all-cpus  收集全局的统计信息
        -p          --pid=      收集指定PID的统计信息
        -t          --tid=      收集指定线程ID的统计信息
        -u          --uid=      收集指定USER ID的统计信息
        -c          --count=    采样的事件数
        -F          --freq=     采样的频率
        -r          --realtime= 采集使用CPU实时调度策略的进程
        -g          --call-graph 收集调用栈
        -e          --event=    收集指定事件
        --sleep     采集时间多少秒

    折叠栈 ./stackcollapse-perf.pl out.perf > out.folded
        stackcollapse.pl                用于DTrace栈
        stackcollapse-perf.pl           用于Linux perf script的输出
        stackcollapse-pmc.pl            用于FreeBSD pmcstat -G 栈
        stackcollapse-stap.pl           用于SystemTap 栈
        stackcollapse-instrument.pl     用于XCode Instruments
        stackcollapse-vtune.pl          用于Intel VTune 
        stackcollapse-ljp.pl            用于Lightweight Java Profiler
        stackcollapse-jstack.pl         用于Java jstack(1) 输出
        stackcollapse-gdb.pl            用于gdb(1) 栈
        stackcollapse-go.pl             用于Golang pprof 栈
        stackcollapse-vsprof.pl         用于Microsoft Visual Studio
        stackcollapse-wcp.pl            用于wallClockProfiler 输出

    使用flamegraph.pl 渲染折叠栈信息为一个SVG
        ./flamegraph.pl out.kern_folded > kernel.svg
        grep cpuid out.kern_folded | ./flamegraph.pl > cpuid.svg

        gunzip -c example-perf-stacks.txt.gz | ./stackcollapse-perf.pl --all | ./flamegraph.pl --color=java --hash > example-perf.svg
    
    
### JVM参数
&emsp;&emsp; Java启动参数分为三类：1，标准参数(-)，所有的JVM都必须实现这些参数的功能，而且向后兼容。
2，非标准参数(-X)，默认JVM实现这些参数的功能，但不保证所有JVM实现都满足，且不保证向后兼容。
3，非Stable参数(-XX)，此类参数各个jvm实现会有所不同，将来可能会随时取消，需要谨慎使用。

    非稳态选项使用说明
        -XX:+<option>               启用选项
        -XX:-<option>               不启用选项
        -XX:<option>=<number>       给选项设置一个数字类型值，可跟单位，如32K，1024m，2g
        -XX:<option>=<string>       给选项设置一个字符串值，如-XX:HeapDumpPath=./dump.core

    行为选项：
        -XX:-AllowUserSignalHandlers        限于Linux和Solaris，默认不启用       允许Java进程安装信号处理器
        -XX:-DisableExplicitGC              默认不启用                           禁止在运行期显式地调用System.gc()
        -XX:-RelaxAccessControlCheck        默认你不启用                          在Class检验器中，放松对访问控制的检查
        -XX:-UseConcMarkSweepGC             默认不启用                           启用CMS低停顿垃圾收集器
        -XX:-UseParallelGC                  -server时启用，其他情况默认不启用       策略为新生代使用并行清除，老年代使用单线程Mark-Sweep-Compact垃圾收集器
        -XX:-UseParallelOldGC               默认不启用                           策略为老年代和新生代都使用并行清除的垃圾收集器
        -XX:-UseSerialGC                    -client时启用，其他默认不启用          使用串行垃圾收集器
        -XX:+UseSplitVerifier               java6默认启用                        使用新的Class类型校验器
        -XX:+FailOverToOldVerifier          java6默认启用                        如果新的Class校验器检查失败，则使用老的校验器
        -XX:+HandlePromotionFailure         java6默认启用                       关闭新生代收集担保
        -XX:+UseSpinning                    java6默认启用                       启用多线程自旋锁优化 关联选项 -XX:PreBlockSpin=10
        -XX:PreBlockSpin=10       UseSpinning需先启用，默认启用，默认自旋10次       控制多线程自旋锁优化的自旋次数
        -XX:+ScavengeBeforeFullGC           默认启用                            在Full GC前触发一次Minor GC
        -XX:+UseGCOverheadLimit             默认启用                            限制GC的运行时间，如果GC耗时过长，抛OOM
        -XX:+UseTLAB                  使用-client时，默认不启用，其他默认启用       启用线程本地缓存区
        -XX:+UseThreadPriorities            默认启用                            使用本地线程的优先级
        -XX:+UseAltSigs                  限于Solaris，默认启用                   允许使用候补信号替代SIGUSR1和SIGUSR2
        -XX:+UseBoundThreads             限于Solaris，默认启用                   绑定所有用户线程到内核线程，减少线程进入饥饿状态
        -XX:+UseLWPSynchronization       限于Solaris，默认启用                   使用轻量级进程(内核线程)替换线程同步
        -XX:+MaxFDLimit                  限于Solaris，默认启用                   设置Java进程可用文件描述符为操作系统允许的最大值
        -XX:+UseVMInterruptibleIO	     限于Solaris，默认启用                   在solaris中，允许运行时中断线程

    性能选项：
        -XX:+AggressiveOpts             JDK6默认启用                启用JVM团队最新的调优成果，如编译优化、偏向锁、并行老年代收集等
        -XX:CompileThreshold=10000      默认值1000                  通过JIT编译器，将方法编译成机器码的触发阈值，可以理解为调用方法的次数
        -XX:LargePageSizeInBytes=4m     默认4m                      设置堆内存的内存页大小
        -XX:MaxHeapFreeRatio=70         默认70                      GC后，如果发现空闲堆内存占整个预估上限值的70%，则收缩预估上限值
        -XX:MaxNewSize=size             Sparc: 32m x86: 2.5m       新生代占整个堆内存的最大值
        -XX:MaxPermSize=64m             默认值64m                   方法区占整个堆内存的最大值
        -XX:MinHeapFreeRatio            默认值40                    GC后，如果发现空闲堆内存占到整个预估上限值的40%，则增大上限值
        -XX:NewRatio=2                  默认2                       新生代和老年代的堆内存占用比例
        -XX:newSize=2.125m              默认2.125m                  新生代预估上限的默认值
        -XX:ReservedCodeCacheSize=32m   默认32m                     设置代码缓存的最大值，编译时用
        -XX:SurvivorRatio=8             默认8                       Eden与Survivor的占用比例，8表示一个Survivor占1/8 Eden内存
        -XX:TargetSurvivorRatio=50      默认50                      实际使用的Survivor空间大小占比，默认50%，最高90%
        -XX:ThreadStackSize=512         默认512                     线程堆栈大小
        -XX:+UseBiasedLocking           JDK6默认启用                 启用偏向锁
        -XX:+UseFastAccessorMethods     默认启用                     优化原始类型的getter方法性能
        -XX:-UseISM                     默认启用                     启用Solaris的ISM
        -XX:+UseLargePages              JDK6默认启用                 启用大内存分页
        -XX:+UseMPSS                    默认启用                     启用solaris的MPSS，不能与ISM同时使用
        -XX:+StringCache                默认启用                     启用字符串缓存
        -XX:AllocatePrefetchLines=1     1                           与机器码指令预读相关的一个选项
        -XX:AllocatePrefetchStyle       1                           与机器码指令预读相关的一个选项

    调试选项：
        -XX:-CITime                             默认启用                    打印JIT编译器编译耗时
        -XX:ErrorFile=./hs_err_pid<pid>.log     Java6引入                  如果JVM崩溃，将错误日志输出到指定文件路径
        -XX:-ExtendedDTraceProbes               默认不启用                  启用dtrace诊断
        -XX:HeapDumpPath=./java_pid<pid>.hprof  默认是Java进程启动位置       堆内存快照的存储文件路径，OOM或崩溃被OS终止后会生成文件
        -XX:-HeapDumpOnOutOfMemoryError         默认不启用                  在OOM时，输出一个dump.core文件，记录当时的堆内存快照
        -XX:OnError=”<cmd args>;<cmd args>”	    1.4.2引入                  每抛出一个ERROR时，运行指定命令行指令集
        -XX:OnOutOfMemoryError="<cmd args>;<cmd args>" java6引入           当第一次发生OOM时，运行指定命令行指令集
        -XX:-PrintClassHistogram                默认不启用                  Linux下执行kill -3时，打印class柱状图
        -XX:-PrintConcurrentLocks               默认不启用                  在thread dump的同时，打印concurrent的锁状态
        -XX:-PrintCommandLineFlags              默认不启用                  Java启动时，忘stdout打印当前启用的非稳态JVM options
        -XX:-PrintCompilation                   默认不启用                  往stdout打印方法被JIT编译时的信息
        -XX:-PrintGC                            默认不启用                  开启GC日志打印
        -XX:-PrintGCDetails                     默认不启用                  打印GC回收的细节
        -XX:-PrintGCTimeStamps                  默认不启用                  打印GC停顿耗时
        -XX:-PrintTenuringDistribution          默认不启用                  打印对象的存活期限信息
        -XX:-TraceClassLoading                  默认不启用                  打印class装载信息到stdout
        -XX:-TraceClassLoadingPreorder          默认不启用                  按class的引用/依赖顺序，打印类装载信息到stdout
        -XX:-TraceClassResolution               默认不启用                  打印所有的静态类，常量的代码引用位置
        -XX:-TraceClassUnloading                默认不启用                  打印class的卸载信息到stdout
        -XX:-TraceLoaderConstraints             默认不启用                  打印class的装载策略变化信息到stdout
        -XX:+PerfSaveDataToFile                 默认启用                    当Java进程因OOM或者崩溃被强制终止后，生成一个堆快照文件
    
        