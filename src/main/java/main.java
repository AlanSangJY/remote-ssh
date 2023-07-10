import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class main {

    @Test
    public void runtimeTest() throws IOException {
        Process process = Runtime.getRuntime().exec("ls -l");
        stream(process);
    }

    @Test
    public void runtimeTest2() throws IOException, InterruptedException {
        /*
        Process process = Runtime.getRuntime().exec("ls -l > src/main/resources/command-template/runtimeTest2.txt");
        报错：
        ls: >: No such file or directory
        ls: src/main/resources/command-template/runtimeTest2.txt: No such file or directory
        原因： exec方法不可以用重定向符（>, >>）或者管道符(|)
         */
        //“sh -c” 命令，它可以让 bash 将一个字串作为完整的命令来执行
        String[] commands = {"sh", "-c", "ls > src/main/resources/command-template/runtimeTest2.txt"};
        Process process = Runtime.getRuntime().exec(commands);
        stream(process);
    }

    @Test
    public void runtimeTest3() throws InterruptedException, IOException {
        //前面是cmd文件路径，在文件中可以用$1, $2占位符，后面可以用参数填充
        Process process = Runtime.getRuntime().exec("src/main/resources/command-template/shellForRuntime.sh -l");
        stream(process);
        //加入waitfor方法可以获得process线程的返回值，但是会阻塞等待，如果没有waitfor会异步执行
        int i = process.waitFor();
        System.out.println(i);
    }

    @Test
    public void processBuilderTest() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File("src/main/resources/command-template"));
        processBuilder.command("ls", "-l");
        Process process = processBuilder.start();
        stream(process);
        processBuilder.command("ifconfig");
        Process process2 = processBuilder.start();
        stream(process2);
    }

    @Test
    public void processBuilderTest2() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File("src/main/resources/command-template"));
        ProcessBuilder command = processBuilder.command("./shellForProcessBuilder.sh", "-l");
        Process start = command.start();
        stream(start);
        int i = start.waitFor();
        System.out.println(i);
    }

    /**
     * process 会有输入，输出，错误三个流
     *
     * @param process
     * @throws IOException
     */
    private void stream(Process process) throws IOException {
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
    }

    /**
     * 如果没有权限可以执行这个语句
     */
    @Test
    public void chmodFile() throws InterruptedException, IOException {
        ProcessBuilder processBuilder1 = new ProcessBuilder("/bin/chmod", "775", "/Users/alansang/Documents/GitHub/alansangjy.github.io/resources/java/remote-ssh/remote-ssh/src/main/resources/command-template/shellForRuntime.sh");
        Process process = processBuilder1.start();
        stream(process);
        int i = process.waitFor();
        System.out.println(i);
    }
}
