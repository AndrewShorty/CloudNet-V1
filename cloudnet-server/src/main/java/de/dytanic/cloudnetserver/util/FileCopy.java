package de.dytanic.cloudnetserver.util;

import de.dytanic.cloudnetserver.CloudNetServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FileCopy {
	
	public static final void copyFileToDirectory(File file, File to) throws IOException {
		if(!to.exists()) {
			to.mkdirs();	
		}
		File n = new File(to.getAbsolutePath() + "/" + file.getName());
		Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static final void copyFilesInDirectory(File from, File to) throws IOException {
		if(!to.exists()) {
			to.mkdirs();	
		}
		for (File file : from.listFiles()) {
			if (file.isDirectory()) {
				copyFilesInDirectory(file, new File(to.getAbsolutePath() + "/" + file.getName()));
			} else {
				File n = new File(to.getAbsolutePath() + "/" + file.getName());
				Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}
	
	  public static final void insertData(String paramString1, String paramString2)
	  {
	    InputStream localInputStream = CloudNetServer.class.getClassLoader().getResourceAsStream(paramString1);
	    FileOutputStream localFileOutputStream = null;
	    try{
	      localFileOutputStream = new FileOutputStream(paramString2);
	      byte[] arrayOfByte = new byte['?'];
	      int i = localInputStream.read(arrayOfByte);
	      while (i != -1)
	      {
	        localFileOutputStream.write(arrayOfByte, 0, i);
	        i = localInputStream.read(arrayOfByte);
	      }
	      if (localFileOutputStream != null)
	      {
	        localFileOutputStream.close();
	      }
	    }
	    catch (FileNotFoundException localFileNotFoundException)
	    {
	      localFileNotFoundException.printStackTrace();
	    }
	    catch (IOException localIOException)
	    {
	      localIOException.printStackTrace();
	    }
	  }
	  
		public static void rewriteFileUtils(File file, String host) throws Exception
		{
			file.setReadable(true);
			FileInputStream in = new FileInputStream(file);
			List<String> liste = new CopyOnWriteArrayList<>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String input;
			boolean value = false;
			while ((input = reader.readLine()) != null)
			{
				if(value)
				{
					liste.add("  host: " + host+"\n");
					value = false;
				}
				else
				{
					if(input.startsWith("  query_enabled"))
					{
						liste.add(input+"\n");
						value = true;
					}
					else
					{
						liste.add(input+"\n");
					}
				}
			}
			file.delete();
			file.createNewFile();
			file.setReadable(true);
			FileOutputStream out = new FileOutputStream(file);
			PrintWriter w = new PrintWriter(out);
			for(String wert : liste)
			{
				w.write(wert);
				w.flush();
			}
			reader.close();
			w.close();
		}
	
}
