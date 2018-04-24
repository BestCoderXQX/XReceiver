package acffo.xqx.xreceiver;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Object2FileUtils {

    private String fileUrl;

    public Object2FileUtils(Context context, String fileName) {
        fileName = Environment.getExternalStorageDirectory().getPath() + "/zzMagikare/error.txt";
        fileUrl = fileName ;
        File file = new File(fileName);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<?> readListFromFile() {
        ArrayList<?> tempList = null;
        File file = new File(fileUrl);
        InputStream in;
        try {
            in = new FileInputStream(file);
            if (in != null) {
                ObjectInputStream objIn = new ObjectInputStream(in);
                tempList = (ArrayList<Object>) objIn.readObject();
                objIn.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e("e:" , e.toString());
        }
        return tempList;
    }

    public Object readObjectFromFile() {
        Object temp = null;
        File file = new File(fileUrl);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("e:" , e.toString());
        }
        return temp;
    }

    public void writeObjectToFile(Object obj) {
        File file = new File(fileUrl);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file, false);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            Log.e("e:" , e.toString());
        }
    }


    public void writeListToFile(List<?> list) {
        File file = new File(fileUrl);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("e:" , e.toString());
            }
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(list);
            objOut.flush();
            objOut.close();

        } catch (IOException e) {
            Log.e("e:" , e.toString());
        }
    }

    public void writeListToFileOverlap(List<?> list) {
        File file = new File(fileUrl);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("e:" , e.toString());
            }
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file, false);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(list);
            objOut.flush();
            objOut.close();

        } catch (IOException e) {
            Log.e("e:" , e.toString());
        }
    }

    public void writeString2FileOverlap(String string) {
        File file = new File(fileUrl);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("e:" , e.toString());            }
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file, false);
            out.write(string.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e("e:" , e.toString());
        }
    }

    public void readStringFromFile() {

    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @return 删除成功返回 true，否则返回 false。
     */
    public boolean deleteFolder() {
        boolean flag = false;
        File file = new File(fileUrl);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(fileUrl);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(fileUrl);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
