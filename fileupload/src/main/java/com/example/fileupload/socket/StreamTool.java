package com.example.fileupload.socket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * large file upload by socket.
 * https://www.xuebuyuan.com/912507.html
 * @author xzy
 * */
@SuppressWarnings("unused")
public class StreamTool {

    /**
     * 保存文件
     *
     * @param file
     *            文件路径
     * @param data
     *            字节码数据
     * @throws Exception
     */
    public static void save(File file, byte[] data) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

    /**
     * 读取一行
     * @param in  输入流
     * @return 字符窜
     * @throws IOException
     */
    public static String readLine(PushbackInputStream in) throws IOException {
        char[] buf = new char[128];
        int room = buf.length;
        int offset = 0;
        int c;
        loop: while (true) {
            switch (c = in.read()) {
                case -1:
                case '\n':

                    break loop;
                case '\r':
                    int c2 = in.read();
                    if (c2 != '\n' && c2 != -1)
                        in.unread(c2);
                    break loop;
                default:
                    if (--room < 0) {
                        char[] lineBuffer = buf;
                        buf = new char[offset + 128];
                        room = buf.length - offset - 1;
                        System.arraycopy(lineBuffer, 0, buf, 0, offset);
                    }
                    buf[offset++] = (char) c;
                    break;

            }
        }

        if (c == -1 && offset == 0)
            return null;
        return String.copyValueOf(buf,0,offset);
    }

    /**
     * 读取流
     * @param inStream
     * @return 字节数
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        byte [] buf=new byte[1024];
        int len=-1;
        while((len=inStream.read(buf))!=1){
            outputStream.write(buf,0,len);
        }
        outputStream.close();
        inStream.close();
        return outputStream.toByteArray();

    }
}
