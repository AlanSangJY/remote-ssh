# remote-ssh

# Runtime

## Runtime直接执行command

```java
Process process = Runtime.getRuntime().exec("ls -l");

tips: 结果可以通过process流获取，见下面Process
```

## Runtime不能识别>和｜

````java
Process process = Runtime.getRuntime().exec("ls -l > src/main/resources/command-template/runtimeTest2.txt");
报错：
ls: >: No such file or directory
ls: src/main/resources/command-template/runtimeTest2.txt: No such file or directory
原因： exec方法不可以识别重定向符（>, >>）或者管道符(|)
解决：
String[] commands = {"sh", "-c", "ls > src/main/resources/command-template/runtimeTest2.txt"};
        Process process = Runtime.getRuntime().exec(commands);

底层原理没有研究，大概就是
  “sh -c” 命令，它可以让 bash 将一个字串作为完整的命令来执行
  这里执行命令为sh -c “ls > src/main/resources/command-template/runtimeTest2.txt”
````

## Runtime通过文件执行命令

### 核心代码

````java
Process process = Runtime.getRuntime().exec("src/main/resources/command-template/shellForRuntime.sh -l");

tips: 命令可以用空格隔开多段，前面是cmd文件路径，后面是填充参数。在文件中可以用$1, $2占位符
````

### Command 文件内容

````markdown
shellForRuntime.sh 文件内容:
ls $1 > src/main/resources/command-template/runtimeResult.txt && echo runtime exec ok
````

### 运行结果

````markdown
input stream:
	runtime exec ok
重定向文件（runtimeResult.txt）内容：
	total 16
-rw-r--r--  1 alansang  staff  839 Aug 10 22:22 pom.xml
-rw-r--r--  1 alansang  staff   80 Aug  5 22:33 remote-ssh.iml
drwxr-xr-x  5 alansang  staff  160 Aug  5 23:03 src
drwxr-xr-x  4 alansang  staff  128 Aug 19 21:23 target
````

# ProcessBuilder

## 直接执行command

````java
ProcessBuilder processBuilder = new ProcessBuilder();
//directory 方法设置当前工作目录
processBuilder.directory(new File("src/main/resources/command-template"));
processBuilder.command("ls", "-l");
Process process = processBuilder.start();

tips: processBuilder可以多次使用
````

通过文件执行命令

````java
ProcessBuilder processBuilder = new ProcessBuilder();
processBuilder.directory(new File("src/main/resources/command-template"));
processBuilder.command("./shellForProcessBuilder.sh", "-l");
Process process = processBuilder.start();

tips: 注意文件中的命令也是以directory方法设置的目录为基础的。
````

# Process

## 基本介绍

````markdown
生成process意味着要开始执行命令
````

## 方法

````markdown
1、destroy()						杀死这个子进程
2、exitValue()					得到进程运行结束后的返回状态
3、waitFor()						得到进程运行结束后的返回状态，如果进程未运行完毕则等待知道执行完毕
4、getInputStream()		得到进程的标准输出信息流
5、getErrorStream()		得到进程的错误输出信息流
6、getOutputStream()		得到进程的输入流
````

## waitFor返回结果

````markdown
OS error code   0:  正确
OS error code   1:  Operation not permitted
OS error code   2:  No such file or directory
OS error code   3:  No such process
OS error code   4:  Interrupted system call
OS error code   5:  Input/output error
OS error code   6:  No such device or address
OS error code   7:  Argument list too long
OS error code   8:  Exec format error
OS error code   9:  Bad file descriptor
OS error code  10:  No child processes
OS error code  11:  Resource temporarily unavailable
OS error code  12:  Cannot allocate memory
OS error code  13:  Permission denied
OS error code  14:  Bad address
````

## 流处理

````java
BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
String line = "";
System.out.println("---------input stream begin--------");
while (StringUtils.isNotEmpty(line = bufferedReader.readLine())) {
  System.out.println(line);
}
System.out.println("---------input stream end--------");
System.out.println("---------error stream begin--------");
BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
while (StringUtils.isNotEmpty(line = errorBufferedReader.readLine())) {
  System.out.println(line);
}
System.out.println("---------error stream end--------");
````

# 权限处理

````java
ProcessBuilder processBuilder = new ProcessBuilder("/bin/chmod", "775", "/Users/alansang/Documents/GitHub/alansangjy.github.io/resources/java/remote-ssh/remote-ssh/src/main/resources/command-template/shellForRuntime.sh");
Process process = processBuilder.start();
````

# ssh

````shell
/user/putty/putty.exe -ssh -pw <password> -m <shellFilePath> <username>@<server>
/user/putty/pscp -pw <password> <username>@<server>:<filePath> <copyDirPath>
````

