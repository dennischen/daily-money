package com.bottleworks.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import android.os.Environment;

/**
 * @author Dennis.Chen
 * 
 */
public class Files {

    static private SimpleDateFormat backupDateFmt = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    
    static public long copyFileTo(File src, File dest) throws IOException {
        if (!src.exists() || src.isDirectory()) {
            throw new IllegalArgumentException("not a file : " + src); //$NON-NLS-1$
        }

        if (dest.exists() && dest.isDirectory()) {
            throw new IllegalArgumentException("not a file : " + dest); //$NON-NLS-1$
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        long size = 0;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);

            byte[] buff = new byte[1024];
            int r;
            while ((r = fis.read(buff)) != -1) {
                fos.write(buff, 0, r);
                size += r;
            }
            return size;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
    }

    static public long flushTo(InputStream is, File dest) throws IOException {
        if (dest.exists() && dest.isDirectory()) {
            throw new IllegalArgumentException("not a file : " + dest); //$NON-NLS-1$
        }

        FileOutputStream fos = null;
        long size = 0;
        try {
            fos = new FileOutputStream(dest);

            byte[] buff = new byte[1024];
            int r;
            while ((r = is.read(buff)) != -1) {
                fos.write(buff, 0, r);
                size += r;
            }
            return size;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
    }

    public static File getJavaTempFolder() {
        return new File(getJavaTempFolderPath());
    }

    public static String getJavaTempFolderPath() {
        return System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
    }

    public static void deepClean(File folder) {
        File[] folders = folder.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        for (File f : folders) {
            deepClean(f);
            f.delete();
        }
        clean(folder);
    }

    public static void clean(File folder) {
        File[] files = folder.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });

        for (File file : files) {
            file.delete();
        }
    }

    public static String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1 && index < fileName.length()) {
            return fileName.substring(index + 1, fileName.length());
        } else {
            return null;
        }
    }

    public static String getMain(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(0, index);
        } else {
            return fileName;
        }
    }

    public static Properties loadProperties(File file) {
        java.util.Properties p = new java.util.Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            p.load(is);
            return p;
        } catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void saveString(String str, File file, String encoding) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            Streams.flush(new ByteArrayInputStream(str.getBytes(encoding)), os);
        } catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String loadString(File file, String encoding) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Streams.flush(is, os);
            return new String(os.toByteArray(), encoding);
        } catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void saveProperties(Properties p, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            p.store(os, "");
        } catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    /**
     * Copy database files which include dm_master.db, dm.db and dm_<i>bookid<i>.db
     * 
     * @param sourceFolder source folder
     * @param targetFolder target folder
     * @param date
     *            Copy date. If date is not null, it will make another copy named with '.yyyyMMdd_HHmmss' as suffix. 
     *            It is also used to identify copy from SD to DB when date is null 
     * @return Number of files copied.
     * @throws IOException
     */
    public static int copyDatabases(File sourceFolder, File targetFolder, Date date) throws IOException {
        int count = 0;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) && sourceFolder.exists() && targetFolder.exists()) {
            String[] filenames = sourceFolder.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    //dm db files only
                    if (filename.startsWith("dm") && filename.endsWith(".db")) {
                        return true;
                    }
                    return false;
                }
            });
            String bakDate = date == null? null:backupDateFmt.format(date)+".bak";
            if (filenames != null && filenames.length != 0) {
                List<String> dbs = Arrays.asList(filenames);
                //only when there are master and default book db.
                if (dbs.contains("dm_master.db") && dbs.contains("dm.db")) {
                    for (String db : dbs) {
                        Files.copyFileTo(new File(sourceFolder, db), new File(targetFolder, db));
                        count++;
                        if (bakDate != null) {
                            Files.copyFileTo(new File(sourceFolder, db),
                                    new File(targetFolder, db + "." + bakDate));
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Copy preference file.
     * 
     * @param sourceFolder source folder
     * @param targetFolder target folder
     * @param date
     *            Copy date. If date is not null, it will make another copy named with '.yyyyMMdd_HHmmss' as suffix.
     *            It is also used to identify copy from SD to DB when date is null 
     * @return Number of files copied.
     * @throws IOException
     */
    public static int copyPrefFile(File sourceFolder, File targetFolder, Date date) throws IOException {
        int count = 0;
        final String prefName = "com.bottleworks.dailymoney_preferences.xml";
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) && sourceFolder.exists() && targetFolder.exists()) {
            File pref = new File(sourceFolder, prefName);
            String bakDate = date == null? null:backupDateFmt.format(date)+".bak";
            if (pref.exists()) {
                count++;
                Files.copyFileTo(pref, new File(targetFolder, "com.bottleworks.dailymoney_preferences.xml"));
                if (date != null) {
                    Files.copyFileTo(pref, new File(targetFolder, prefName + "." + bakDate));
                }
            }
        }
        return count;
    }

}
