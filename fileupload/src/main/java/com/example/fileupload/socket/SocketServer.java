package com.example.fileupload.socket;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * large file upload by socket.
 * https://www.xuebuyuan.com/912507.html
 * @author xzy
 * */
@SuppressWarnings("unused")
public class SocketServer {
    private ExecutorService executorService;// 线程池
    private ServerSocket server = null;
    private int port;// 监听
    private boolean quit;// 是否推出退出
    @SuppressLint("UseSparseArrays")
    private Map<Long, FileLog> datas = new HashMap<>();// 存放断点数据

    public SocketServer(int port) {
        this.port = port;
        // 初始化线程池
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * 50);// 获取cpu目，cpu越多获得的线程越多

    }

    public void start() throws IOException {
        server = new ServerSocket(port);
        while (!quit) {
            Socket socket = server.accept();//接受到客户端的请求
            executorService.execute(new SocketTask(socket));//使用线程池管理用户并发

        }
    }

    public void quit(){
        this.quit=true;
        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class SocketTask implements Runnable {

        private Socket socket;

        public SocketTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("accepted connection from"
                        + socket.getInetAddress() + "@" + socket.getPort());

                PushbackInputStream inputStream = new PushbackInputStream(
                        socket.getInputStream());

                // 得到客戶端發來的第一行數據：Content-Length=143253434;filename=xxx.3gp;sourceid=
                // 如果第一次请求sourceid为空
                String head = StreamTool.readLine(inputStream);
                System.out.println(head);

                if (head != null) {
                    String[] items = head.split(";");
                    String fileLength = items[0].substring(items[0]
                            .indexOf("=") + 1);
                    String fileName = items[1]
                            .substring(items[1].indexOf("=") + 1);
                    String sourceId = items[2]
                            .substring(items[2].indexOf("=") + 1);
                    long id = System.currentTimeMillis();// 文件id
                    FileLog log = null;
                    if (!"".equals(sourceId)) {
                        id = Long.valueOf(sourceId);
                        log = find(id);// 查找文件是否存在上传记录
                    }

                    File file = null;// 保存的文件
                    int position = 0;// 断点位置

                    // 如果上传的文件不存在上传记录，为文件添加跟踪记录
                    if (log == null) {
                        @SuppressLint("SimpleDateFormat") String path = new SimpleDateFormat(
                                "yyyy/MM/dd/HH/mm/ss").format(new Date());
                        File dir = new File("file/" + path);
                        if (!dir.exists())
                            dir.mkdirs();

                        file = new File(dir, fileName);// 创建上传文件
                        if (file.exists()) {// 如果文件存在则改名
                            fileName = fileName.substring(0,
                                    fileName.indexOf("."))
                                    + Objects.requireNonNull(dir.listFiles()).length
                                    + fileName.substring(fileName.indexOf("."));
                            file = new File(dir, fileName);
                        }
                        save(id, file);
                    } else {// 如果存在文件断点，读取上次的文件
                        file = new File(log.getPath());
                        if (file.exists()) {
                            File logFile = new File(file.getParentFile(),
                                    file.getName() + ".log");
                            if (logFile.exists()) {
                                Properties properties = new Properties();
                                properties.load(new FileInputStream(logFile));
                                position = Integer.valueOf(properties
                                        .getProperty("length"));// 读取断点的位置
                            }

                        }

                    }
                    // 服务器接受到客户端的请求，给客户端发送相应信息：sourceid,和position位置信息
                    OutputStream outputStream = socket.getOutputStream();
                    String respons = "sourceid=" + id + "position" + position
                            + "\r\n";
                    outputStream.write(respons.getBytes());

                    RandomAccessFile accessFile = new RandomAccessFile(file,
                            "rwd");

                    if(position==0) accessFile.setLength(Integer.valueOf(fileLength));//设置文件长度
                    accessFile.seek(position);//移动到指定位置写入数据
                    byte [] buf=new byte[1024];
                    int len;
                    int length=position;
                    while((len=inputStream.read(buf))!=-1){
                        accessFile.write(buf,0,len);
                        length+=len;
                        Properties properties=new Properties();
                        properties.put("length", String.valueOf(length));
                        FileOutputStream fileOutputStream=new FileOutputStream(new File(file.getParent(),file.getName()+".log"));
                        properties.store(fileOutputStream, null);//实时记录文件的最后保存位置
                        fileOutputStream.close();
                    }
                    if(length==accessFile.length()) delete(id);
                    accessFile.close();
                    inputStream.close();
                    outputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(socket!=null&&!socket.isClosed())
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        }

    }

    public static void main(String[] args) {
        try {
            new SocketServer(8080).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找文件
     *
     * @param sourceId
     *            文件Id
     * @return
     */
    public FileLog find(long sourceId) {
        return datas.get(sourceId);
    }

    /**
     * 保存文件断点
     *
     * @param id
     *            文件id
     * @param file
     *            文件
     */
    public void save(long id, File file) {
        datas.put(id, new FileLog(id, file.getAbsolutePath()));
    }

    /**
     * 文件上床完毕删除文件记录的断点
     *
     * @param sourceId
     *            文件Id
     */
    public void delete(long sourceId) {
        if (datas.containsKey(sourceId)) {
            datas.remove(sourceId);
        }
    }

    private class FileLog {
        private long id;
        private String path;

        public FileLog(long id, String path) {
            this.id = id;
            this.path = path;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

    }
}