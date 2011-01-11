package com.bottleworks.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author dennis
 *
 */
public class Streams {

    public static void flush(InputStream in, File out) throws IOException{
        FileOutputStream os = new FileOutputStream(out);
        try{
            flush(in, os, 1024);
        }finally{
            os.close();
        }
    }
    
    public static void flush(File in, OutputStream out) throws IOException{
        FileInputStream is = new FileInputStream(in);
        try{
            flush(is, out, 1024);
        }finally{
            is.close();
        }
    }
    
    public static void flush(InputStream in, OutputStream out) throws IOException{
        flush(in, out, 1024);
    }
    
	public static void flush(InputStream in, OutputStream out, int chunkSize) throws IOException{
		byte[] b = new byte[chunkSize];
		flush(in, out, b);
	}

	public static void flush(InputStream in, OutputStream out, byte[] chunk) throws IOException{
		int readLen = -1;
		while( (readLen = in.read(chunk)) != -1){
			out.write(chunk, 0, readLen);
		}
		out.flush();
	}
	
	
	public static byte[] toByteArray(InputStream is) throws IOException{
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    flush(is,os,512);
	    return os.toByteArray();
	}
	
	public static byte[] toByteArray(File file) throws IOException{
	    InputStream is = null;
	    try{
	        is = new FileInputStream(file);
	        return toByteArray(is);
	    }finally{
	        if(is!=null){
	            is.close();
	        }
	    }
    }
}
