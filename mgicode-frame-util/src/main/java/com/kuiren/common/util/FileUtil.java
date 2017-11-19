package com.kuiren.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {

	/**
	 * 
	 * 功能描述：复制整个目录的内容，如果目标目录存在，则不覆盖
	 * 
	 * @param srcDirName
	 *            源目录名
	 * @param descDirName
	 *            目标目录名
	 * @return 返回： 如果复制成功返回true，否则返回false
	 */
	public static boolean copyDirectory(String srcDirName, String descDirName) {
		return FileUtil.copyDirectoryCover(srcDirName, descDirName, false);
	}

	/**
	 * 
	 * 功能描述：复制整个目录的内容
	 * 
	 * @param srcDirName
	 *            源目录名
	 * @param descDirName
	 *            目标目录名
	 * @param coverlay
	 *            如果目标目录存在，是否覆盖
	 * @return 返回： 如果复制成功返回true，否则返回false
	 */
	public static boolean copyDirectoryCover(String srcDirName,
			String descDirName, boolean coverlay) {

		File srcDir = new File(srcDirName);
		// 判断源目录是否存在
		if (!srcDir.exists()) {
			System.out.println("复制目录失败，源目录" + srcDirName + "不存在!");
			return false;
		}
		// 判断源目录是否是目录
		else if (!srcDir.isDirectory()) {
			System.out.println("复制目录失败，" + srcDirName + "不是一个目录!");
			return false;
		}
		// 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
		if (!descDirName.endsWith(File.separator)) {
			descDirName = descDirName + File.separator;
		}
		File descDir = new File(descDirName);
		// 如果目标文件夹存在
		if (descDir.exists()) {
			if (coverlay) {
				// 允许覆盖目标目录
				// System.out.println("目标目录已存在，准备删除!");
				if (!FileUtil.delFile(descDirName)) {
					// System.out.println("删除目录" + descDirName + "失败!");
					return false;
				}
			} else {
				// System.out.println("目标目录复制失败，目标目录" + descDirName + "已存在!");
				return false;
			}
		} else {
			// 创建目标目录
			// System.out.println("目标目录不存在，准备创建!");
			if (!descDir.mkdirs()) {
				// System.out.println("创建目标目录失败!");
				return false;
			}

		}

		boolean flag = true;
		// 列出源目录下的所有文件名和子目录名
		File[] files = srcDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 如果是一个单个文件，则直接复制
			if (files[i].isFile()) {
				flag = FileUtil.copyFile(files[i].getAbsolutePath(),
						descDirName + files[i].getName());
				// 如果拷贝文件失败，则退出循环
				if (!flag) {
					break;
				}
			}
			// 如果是子目录，则继续复制目录
			if (files[i].isDirectory()) {
				flag = FileUtil.copyDirectory(files[i].getAbsolutePath(),
						descDirName + files[i].getName());
				// 如果拷贝目录失败，则退出循环
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			System.out.println("复制目录" + srcDirName + "到" + descDirName + "失败!");
			return false;
		}
		System.out.println("复制目录" + srcDirName + "到" + descDirName + "成功!");
		return true;

	}

	/**
	 * 
	 * 功能描述：复制整个目录的内容
	 * 
	 * @param srcDirName
	 *            源目录名
	 * @param descDirName
	 *            目标目录名
	 * @param coverlay
	 *            如果目标目录存在，是否覆盖
	 * @return 返回： 如果复制成功返回true，否则返回false
	 */
	public static boolean copyDirectoryCover(String srcDirName,
			String descDirName, String[] excludeext, boolean coverlay) {

		File srcDir = new File(srcDirName);
		// 判断源目录是否存在
		if (!srcDir.exists()) {
			// System.out.println("复制目录失败，源目录" + srcDirName + "不存在!");
			return false;
		}
		// 判断源目录是否是目录
		else if (!srcDir.isDirectory()) {
			// System.out.println("复制目录失败，" + srcDirName + "不是一个目录!");
			return false;
		}
		// 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
		if (!descDirName.endsWith(File.separator)) {
			descDirName = descDirName + File.separator;
		}
		File descDir = new File(descDirName);
		// 如果目标文件夹存在
		if (descDir.exists()) {
			if (coverlay) {
				// 允许覆盖目标目录
				// System.out.println("目标目录已存在，准备删除!");
				if (!FileUtil.delFile(descDirName)) {
					// System.out.println("删除目录" + descDirName + "失败!");
					return false;
				}
			} else {
				// System.out.println("目标目录复制失败，目标目录" + descDirName + "已存在!");
				return false;
			}
		} else {
			// 创建目标目录
			System.out.println("目标目录不存在，准备创建!");
			if (!descDir.mkdirs()) {
				System.out.println("创建目标目录失败!");
				return false;
			}

		}

		boolean flag = true;
		// 列出源目录下的所有文件名和子目录名
		File[] files = srcDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 如果是一个单个文件，则直接复制
			boolean flag1 = true;
			String extq = StringUtil.getExt(files[i].getName());
			for (String ext : excludeext) {
				if (ext.equals(extq)) {
					flag1 = false;
					break;
				}
			}

			if (files[i].isFile() && flag1 == true) {
				flag = FileUtil.copyFile(files[i].getAbsolutePath(),
						descDirName + files[i].getName());
				// 如果拷贝文件失败，则退出循环
				if (!flag) {
					break;
				}
			}
			// 如果是子目录，则继续复制目录
			if (files[i].isDirectory()) {
				flag = FileUtil.copyDirectoryCover(files[i].getAbsolutePath(),
						descDirName + files[i].getName(), excludeext, false);
				// 如果拷贝目录失败，则退出循环
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			// System.out.println("复制目录" + srcDirName + "到" + descDirName +
			// "失败!");
			return false;
		}
		// System.out.println("复制目录" + srcDirName + "到" + descDirName + "成功!");
		return true;

	}

	/**
	 * 
	 * 功能描述：复制单个文件，如果目标文件存在，则不覆盖
	 * 
	 * @param srcFileName
	 *            待复制的文件名
	 * @param descFileName
	 *            目标文件名
	 * @return 返回： 如果复制成功，则返回true，否则返回false
	 */
	public static boolean copyFile(String srcFileName, String descFileName) {
		return FileUtil.copyFileCover(srcFileName, descFileName, false);
	}

	/**
	 * 
	 * 功能描述：复制单个文件
	 * 
	 * @param srcFileName
	 *            待复制的文件名
	 * @param descFileName
	 *            目标文件名
	 * @param coverlay
	 *            如果目标文件已存在，是否覆盖
	 * @return 返回： 如果复制成功，则返回true，否则返回false
	 */
	public static boolean copyFileCover(String srcFileName,
			String descFileName, boolean coverlay) {
		File srcFile = new File(srcFileName);
		// 判断源文件是否存在
		if (!srcFile.exists()) {
			// System.out.println("复制文件失败，源文件" + srcFileName + "不存在!");
			return false;
		}
		// 判断源文件是否是合法的文件
		else if (!srcFile.isFile()) {
			// System.out.println("复制文件失败，" + srcFileName + "不是一个文件!");
			return false;
		}
		File descFile = new File(descFileName);
		// 判断目标文件是否存在
		if (descFile.exists()) {
			// 如果目标文件存在，并且允许覆盖
			if (coverlay) {
				// System.out.println("目标文件已存在，准备删除!");
				if (!FileUtil.delFile(descFileName)) {
					// System.out.println("删除目标文件" + descFileName + "失败!");
					return false;
				}
			} else {
				// System.out.println("复制文件失败，目标文件" + descFileName + "已存在!");
				return false;
			}
		} else {
			if (!descFile.getParentFile().exists()) {
				// 如果目标文件所在的目录不存在，则创建目录
				// System.out.println("目标文件所在的目录不存在，创建目录!");
				// 创建目标文件所在的目录
				if (!descFile.getParentFile().mkdirs()) {
					// System.out.println("创建目标文件所在的目录失败!");
					return false;
				}
			}
		}

		// 准备复制文件
		// 读取的位数
		int readByte = 0;
		InputStream ins = null;
		OutputStream outs = null;
		try {
			// 打开源文件
			ins = new FileInputStream(srcFile);
			// 打开目标文件的输出流
			outs = new FileOutputStream(descFile);
			byte[] buf = new byte[1024];
			// 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
			while ((readByte = ins.read(buf)) != -1) {
				// 将读取的字节流写入到输出流
				outs.write(buf, 0, readByte);
			}
			String msg = "复制单个文件" + srcFileName + "到" + descFileName + "成功!";
			// System.out.println(msg);

			return true;
		} catch (Exception e) {
			// System.out.println("复制文件失败：" + e.getMessage());
			return false;
		} finally {
			// 关闭输入输出流，首先关闭输出流，然后再关闭输入流
			if (outs != null) {
				try {
					outs.close();
				} catch (IOException oute) {
					oute.printStackTrace();
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException ine) {
					ine.printStackTrace();
				}
			}
		}
	}

	/*
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath String 原文件路径 如：c:/old
	 * 
	 * @param newPath String 复制后路径 如：f:/new
	 * 
	 * @return boolean
	 */
	private static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 功能描述：创建目录
	 * 
	 * @param descDirName
	 *            目录名,包含路径
	 * @return 返回： 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createDirectory(String descDirName) {
		if (!descDirName.endsWith(File.separator)) {
			descDirName = descDirName + File.separator;
		}
		File descDir = new File(descDirName);
		if (descDir.exists()) {
			// System.out.println("目录" + descDirName + "已存在!");
			return false;
		}
		// 创建目录
		if (descDir.mkdirs()) {
			System.out.println("目录" + descDirName + "创建成功!");
			return true;
		} else {
			// System.out.println("目录" + descDirName + "创建失败!");
			return false;
		}

	}

	/**
	 * 创建 多级目录
	 * 
	 * @param path
	 * @return
	 */
	public static boolean createDirs(String path) {
		// String path="c:/aaa/bbb/ccc";
		StringTokenizer st = new StringTokenizer(path, "/");
		String path1 = st.nextToken() + "/";
		String path2 = path1;
		while (st.hasMoreTokens()) {
			path1 = st.nextToken() + "/";
			path2 += path1;
			File inbox = new File(path2);
			if (!inbox.exists() && inbox.isDirectory())
				inbox.mkdir();
		}
		return true;
	}

	/**
	 * 
	 * 功能描述：创建单个文件
	 * 
	 * @param descFileName
	 *            文件名，包含路径
	 * @return 返回： 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createFile(String descFileName) {
		File file = new File(descFileName);
		if (file.exists()) {
			// System.out.println("文件" + descFileName + "已存在!");
			return false;
		}
		if (descFileName.endsWith(File.separator)) {
			// System.out.println(descFileName + "为目录，不能创建目录!");
			return false;
		}
		if (!file.getParentFile().exists()) {
			// 如果文件所在的目录不存在，则创建目录
			if (!file.getParentFile().mkdirs()) {
				// System.out.println("创建文件所在的目录失败!");
				return false;
			}
		}

		// 创建文件
		try {
			if (file.createNewFile()) {
				// System.out.println(descFileName + "文件创建成功!");
				return true;
			} else {
				// System.out.println(descFileName + "文件创建失败!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(descFileName + "文件创建失败!");
			return false;
		}

	}

	// 将byte数组写入文件
	public static void createFile(String path, byte[] content) {

		try {
			FileOutputStream fos = new FileOutputStream(path);

			fos.write(content);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean createNoOverideFile(String filePathAndName,
			String fileContent, String encoding) {
		try {
			File f = new File(filePathAndName);
			if (f.exists()) {
				return false;
			}
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(f), encoding);
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** */
	/**
	 * 创建目录
	 * 
	 * @param destDirName
	 *            目标目录名
	 * @return 目录创建成功返回true，否则返回false
	 */
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			// System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建目录
		if (dir.mkdirs()) {
			// System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			// System.out.println("创建目录" + destDirName + "失败！");
			return false;
		}
	}

	public static void createFile(String destFileName, String content) {
		createFile(destFileName);
		try {
			FileUtil.writeByFileOutputStream(destFileName, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createFile(String destFileName, String content,
			String encoding) {
		createFile(destFileName);
		try {
			FileUtil.writeByFileOutputStream(destFileName, content, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** */
	/**
	 * 创建临时文件
	 * 
	 * @param prefix
	 *            临时文件名的前缀
	 * @param suffix
	 *            临时文件名的后缀
	 * @param dirName
	 *            临时文件所在的目录，如果输入null，则在用户的文档目录下创建临时文件
	 * @return 临时文件创建成功返回临时文件路径及文件名，否则返回null
	 */
	public static String createTempFile(String prefix, String suffix,
			String dirName) {
		File tempFile = null;
		if (dirName == null) {
			try {
				// 在默认文件夹下创建临时文件
				tempFile = File.createTempFile(prefix, suffix);
				// 返回临时文件的路径
				return tempFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("创建临时文件失败！" + e.getMessage());
				return null;
			}
		} else {
			File dir = new File(dirName);
			// 如果临时文件所在目录不存在，首先创建
			if (!dir.exists()) {
				if (!FileUtil.createDir(dirName)) {
					// System.out.println("创建临时文件失败，不能创建临时文件所在的目录！");
					return null;
				}
			}
			try {
				// 在指定目录下创建临时文件
				tempFile = File.createTempFile(prefix, suffix, dir);
				return tempFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				// System.out.println("创建临时文件失败！" + e.getMessage());
				return null;
			}
		}
	}

	public static File createOrGetFile(String descFileName) {
		File file = new File(descFileName);
		if (file.exists()) {
			return file;
		}
		if (descFileName.endsWith(File.separator)) {
			return null;
		}
		if (!file.getParentFile().exists()) {
			// 如果文件所在的目录不存在，则创建目录
			if (!file.getParentFile().mkdirs()) {
				return null;
			}
		}

		// 创建文件
		try {
			if (file.createNewFile()) {
				return file;
			}
		} catch (Exception e) {
			return null;
		}
		return file;
	}

	/**
	 * 
	 * 功能描述：删除目录及目录下的文件
	 * 
	 * @param dirName
	 *            被删除的目录所在的文件路径
	 * @return 返回： 如果目录删除成功，则返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dirName) {
		if (!dirName.endsWith(File.separator)) {
			dirName = dirName + File.separator;
		}
		File dirFile = new File(dirName);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			System.out.println("删除目录失败" + dirName + "目录不存在!");
			return false;
		}
		boolean flag = true;
		// 列出全部文件及子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = FileUtil.deleteFile(files[i].getAbsolutePath());
				// 如果删除文件失败，则退出循环
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = FileUtil.deleteDirectory(files[i].getAbsolutePath());
				// 如果删除子目录失败，则退出循环
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			System.out.println("删除目录失败!");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			System.out.println("删除目录" + dirName + "成功!");
			return true;
		} else {
			System.out.println("删除目录" + dirName + "失败!");
			return false;
		}

	}

	/**
	 * 
	 * 功能描述：删除单个文件
	 * 
	 * @param fileName
	 *            被删除的文件名
	 * @return 返回： 如果删除成功，则返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功!");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败!");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败，" + fileName + "文件不存在!");
			return false;
		}
	}

	/**
	 * 
	 * 功能描述：删除文件，可以删除单个文件或文件夹
	 * 
	 * @param fileName
	 *            被删除的文件名
	 * @return 返回： 如果删除成功，则返回true，否是返回false
	 */
	public static boolean delFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("删除文件失败，" + fileName + "文件不存在!");
			return false;
		} else {
			if (file.isFile()) {
				return FileUtil.deleteFile(fileName);
			} else {
				return FileUtil.deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 
	 * 功能描述：文件是否存在
	 * 
	 * @return 返回： 如果成功，则返回true，否则返回false
	 */
	public static boolean exists(String filepath) {
		if (StringUtil.IsNullOrEmpty(filepath))
			return false;
		try {
			File srcFile = new File(filepath);
			return srcFile.exists();
		} catch (Exception e) {
			return false;
		}

	}

	public static byte[] getContent(String filePath) throws IOException {
		File file = new File(filePath);

		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			System.out.println("file too big...");
			return null;
		}

		FileInputStream fi = new FileInputStream(file);

		byte[] buffer = new byte[(int) fileSize];

		int offset = 0;

		int numRead = 0;

		while (offset < buffer.length

		&& (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {

			offset += numRead;

		}

		// 确保所有数据均被读取

		if (offset != buffer.length) {

			throw new IOException("Could not completely read file "
					+ file.getName());

		}

		fi.close();

		return buffer;
	}

	// 第二种获取文件内容方式
	public static byte[] getContent2(String filePath) throws IOException {
		FileInputStream in = new FileInputStream(filePath);

		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

		System.out.println("bytes available:" + in.available());

		byte[] temp = new byte[1024];

		int size = 0;

		while ((size = in.read(temp)) != -1) {
			out.write(temp, 0, size);
		}

		in.close();

		byte[] bytes = out.toByteArray();
		System.out.println("bytes size got is:" + bytes.length);

		return bytes;
	}

	public static String getEncodingForSystem() {
		String encode = System.getProperty("file.encoding");
		return encode;
	}

	/**
	 * 
	 * 功能描述：获取待压缩文件在ZIP文件中entry的名字，即相对于跟目录的相对路径名
	 * 
	 * @param dirPath
	 *            目录名
	 * @param file
	 *            entry文件名
	 * @return
	 */
	private static String getEntryName(String dirPath, File file) {
		if (!dirPath.endsWith(File.separator)) {
			dirPath = dirPath + File.separator;
		}
		String filePath = file.getAbsolutePath();
		// 对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储
		if (file.isDirectory()) {
			filePath += "/";
		}
		int index = filePath.indexOf(dirPath);

		return filePath.substring(index + dirPath.length());
	}

	@SuppressWarnings("finally")
	public static int getFilesCount(String name) {
		try {
			File file = new File(name);
			List<File> files = FileUtil.search(file, null);
			if (files != null)
				return files.size();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return 0;
		}
	}

	public static InputStream getInputStreamForBytes(byte[] bytes) {
		InputStream is = new ByteArrayInputStream(bytes);
		return is;

	}

	// jad -o -r -sjava -dsrc tree/**/*.class
	public static void jadbatch(File file, String extendName) {

		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			// for(int i=0;i<fileList.length;i++)
			for (File newFile : fileList) {
				// 递归调用
				jadbatch(newFile, extendName);
			}
		} else {
			boolean bool = file.getPath().endsWith(extendName);
			if (bool) {
				// files.add(file);
				Runtime rn = Runtime.getRuntime();
				Process p = null;
				try {
					String nameString = "D:\\eclipse\\jad.exe -sjava "
							+ file.getName();
					p = rn.exec(nameString);

				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(file.getPath());
			}
		}

	}

	/*************************** 文件与byte之间操作 ***************************/

	public static void main(String[] args) {

		File file = new File("D:\\mindjet\\mindjet");
		jadbatch(file, ".class");
		// List<File> files = new ArrayList<File>();
		// search(file, "ftl", files, new String[] { "CodeGen", "Common",
		// "classes" });

	}

	public static void openExe() {
		Runtime rn = Runtime.getRuntime();
		Process p = null;
		try {
			p = rn.exec("jad.exe");

		} catch (Exception e) {
		}
		System.out.println("Error exec!");
	}

	public static String readFile(String filePathAndName) {
		// String fileContent = "";
		StringBuilder sBuilder = new StringBuilder();
		try {
			File f = new File(filePathAndName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f), "UTF-8");
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					// fileContent = fileContent + StringUtil.NEWLINE + line;
					sBuilder.append(line);
					sBuilder.append(StringUtil.NEWLINE);
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		// return fileContent;
		return sBuilder.toString();
	}

	public static String readFile(File f) {
		// String fileContent = "";
		StringBuilder sBuilder = new StringBuilder();
		try {
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f), "UTF-8");
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					// fileContent = fileContent + StringUtil.NEWLINE + line;
					sBuilder.append(line);
					sBuilder.append(StringUtil.NEWLINE);
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		// return fileContent;
		return sBuilder.toString();
	}

	public static String readFile(String filePathAndName, String encoding) {
		// String fileContent = "";
		StringBuilder sBuilder = new StringBuilder();
		try {
			File f = new File(filePathAndName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f), encoding);
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					// fileContent = fileContent + "\n" + line;
					sBuilder.append(line);
					sBuilder.append("\n");
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		// return fileContent;
		return sBuilder.toString();
	}

	public static String readFileContent(String filePathAndName) {
		// String fileContent = "";
		StringBuilder sBuilder = new StringBuilder();
		try {
			File f = new File(filePathAndName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f));
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					sBuilder.append(line);
					// fileContent = fileContent + line;
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取" + filePathAndName + "文件内容操作出错");
			e.printStackTrace();
		}
		// return fileContent;
		return sBuilder.toString();
	}

	public static String readFileContent(String filePathAndName, String encoding) {
		// String fileContent = "";
		StringBuilder sBuilder = new StringBuilder();
		try {
			File f = new File(filePathAndName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f), encoding);
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					// fileContent = fileContent + line;
					sBuilder.append(line);

				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取" + filePathAndName + "文件内容操作出错");
			e.printStackTrace();
		}
		// return fileContent;
		return sBuilder.toString();
	}

	public static String readFileLines(String filePathAndName) {
		// String fileContent = "";
		StringBuilder sBuilder = new StringBuilder();
		try {
			File f = new File(filePathAndName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f));
				BufferedReader reader = new BufferedReader(read);
				String line;

				while ((line = reader.readLine()) != null) {
					// fileContent = fileContent + "\n" + line;
					sBuilder.append(line);
					sBuilder.append("\n");
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		// return fileContent;
		return sBuilder.toString();
	}

	/*************************** 文件与byte之间操作 ***************************/
	public static byte[] readFileToByte(String name) {
		InputStream is = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			is = new FileInputStream(name);// pathStr 文件路径
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				out.write(b, 0, n);
			}// end while
		} catch (Exception e) {
		} finally {
			if (is != null) {
				try {

					is.close();

				} catch (Exception e) {

				}

			}

		}

		return out.toByteArray();

	}

	public static String readFirstRow(String filePathAndName) {

		String fileContent = "";
		try {
			File f = new File(filePathAndName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f));
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					fileContent = line;
					break;
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		return fileContent;
	}

	public static List<File> search(File file, List<File> files) {

		if (files == null) {
			files = new ArrayList<File>();
		}
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			// for(int i=0;i<fileList.length;i++)
			for (File newFile : fileList) {
				// 递归调用
				search(newFile, files);
			}
		} else {

			files.add(file);
			// System.out.println(file.getPath());

		}

		return files;
	}

	/**
	 * 查找指定文件夹下所有的，且指定后缀的文件
	 * 
	 * @param file
	 * @param extendName
	 * @param files
	 * @return
	 */
	public static List<File> search(File file, String extendName,
			List<File> files) {

		if (files == null) {
			files = new ArrayList<File>();
		}
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			// for(int i=0;i<fileList.length;i++)
			for (File newFile : fileList) {
				// 递归调用
				search(newFile, extendName, files);
			}
		} else {
			boolean bool = file.getPath().endsWith(extendName);
			if (bool) {
				files.add(file);
				System.out.println(file.getPath());
			}
		}

		return files;
	}

	// 2、JAVA写入文件，避免中文乱码。

	public static List<File> search(File file, String extendName,
			List<File> files, String[] excludes) {

		if (files == null) {
			files = new ArrayList<File>();
		}
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			// for(int i=0;i<fileList.length;i++)
			for (File newFile : fileList) {
				// 递归调用
				boolean flag = true;
				for (String ef : excludes) {
					if (ef.equalsIgnoreCase(newFile.getName())) {
						flag = false;
						break;
					}
				}
				if (flag == true) {
					search(newFile, extendName, files, excludes);
				}
			}
		} else {
			boolean bool = file.getPath().endsWith(extendName);
			if (bool) {
				files.add(file);
				System.out.println(file.getPath());
			}
		}

		return files;
	}

	public static List<File> searchFile(File file, String fileName,
			List<File> files, String[] excludes) {
		if (files == null) {
			files = new ArrayList<File>();
		}
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();

			for (File newFile : fileList) {
				// 递归调用
				boolean flag = true;
				for (String ef : excludes) {
					if (ef.equalsIgnoreCase(newFile.getName())) {
						flag = false;
						break;
					}
				}
				if (flag == true) {
					search(newFile, fileName, files, excludes);
				}
			}
		} else {
			boolean bool = file.getName().equals(fileName);
			if (bool) {
				files.add(file);
				return files;
			}
		}

		return files;
	}

	/**
	 * 功能描述：解压缩ZIP文件，将ZIP文件里的内容解压到descFileName目录下
	 * 
	 * @param zipFileName
	 *            需要解压的ZIP文件
	 * @param descFileName
	 *            目标文件
	 */
	public static void unZipFiles(String zipFileName, String descFileName) {
		if (!descFileName.endsWith(File.separator)) {
			descFileName = descFileName + File.separator;
		}
		try {
			// 根据ZIP文件创建ZipFile对象
			ZipFile zipFile = new ZipFile(zipFileName);
			ZipEntry entry = null;
			String entryName = null;
			String descFileDir = null;
			byte[] buf = new byte[4096];
			int readByte = 0;
			// 获取ZIP文件里所有的entry
			Enumeration enums = zipFile.entries();
			// 遍历所有entry
			while (enums.hasMoreElements()) {
				entry = (ZipEntry) enums.nextElement();
				// 获得entry的名字
				entryName = entry.getName();
				descFileDir = descFileName + entryName;
				if (entry.isDirectory()) {
					// 如果entry是一个目录，则创建目录
					new File(descFileDir).mkdirs();
					continue;
				} else {
					// 如果entry是一个文件，则创建父目录
					new File(descFileDir).getParentFile().mkdirs();
				}
				File file = new File(descFileDir);
				// 打开文件输出流
				FileOutputStream fouts = new FileOutputStream(file);
				// 从ZipFile对象中打开entry的输入流
				InputStream ins = zipFile.getInputStream(entry);
				while ((readByte = ins.read(buf)) != -1) {
					fouts.write(buf, 0, readByte);
				}
				fouts.close();
				ins.close();
			}
			System.out.println("文件解压成功!");
		} catch (Exception e) {
			System.out.println("文件解压失败：" + e.getMessage());
		}

	}

	/**
	 * 
	 * 用FileOutputStream向文件写入内容
	 * 
	 * 
	 * 
	 * @param _destFile
	 * 
	 * @throws IOException
	 */

	public static void writeByFileOutputStream(String _sDestFile,
			String _sContent) throws IOException {
		writeByFileOutputStream(_sDestFile, _sContent, "UTF-8");
	}

	public static void writeByFileOutputStream(String _sDestFile,
			String _sContent, String encoding) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(_sDestFile);
			fos.write(_sContent.getBytes(encoding));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
				fos = null;
			}
		}
	}

	/**
	 * 
	 * 用FileWrite向文件写入内容
	 * 
	 * 
	 * 
	 * @param _destFile
	 * 
	 * @throws IOException
	 */

	public static void writeByFileWrite(String _sDestFile, String _sContent)
			throws IOException {
		FileWriter fw = null;
		try {

			fw = new FileWriter(_sDestFile);

			fw.write(_sContent);
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			if (fw != null) {
				fw.close();
				fw = null;
			}

		}

	}

	/**
	 * 
	 * 用OutputStreamWrite向文件写入内容
	 * 
	 * 
	 * 
	 * @param _destFile
	 * 
	 * @throws IOException
	 */

	public static void writeByOutputStreamWrite(String _sDestFile,
			String _sContent) throws IOException {
		OutputStreamWriter os = null;
		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(_sDestFile);
			os = new OutputStreamWriter(fos, "UTF-8");
			os.write(_sContent);

		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			if (os != null) {
				os.close();
				os = null;
			}

			if (fos != null) {
				fos.close();
				fos = null;
			}

		}

	}

	public static void writeFile(String path, InputStream in) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				createFile(path);
			}
			FileOutputStream fos = new FileOutputStream(file);
			int bread;
			while ((bread = in.read()) != -1) {
				fos.write(bread);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void writeFile(String filePathAndName, String fileContent) {
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(String filePathAndName, String fileContent,
			String encoding) {
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(f), encoding);
			BufferedWriter writer = new BufferedWriter(write);

			// PrintWriter writer = new PrintWriter(new BufferedWriter(new
			// FileWriter(filePathAndName)));
			// PrintWriter writer = new PrintWriter(new
			// FileWriter(filePathAndName));
			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 功能描述：将目录压缩到ZIP输出流
	 * 
	 * @param dirPath
	 *            目录路径
	 * @param fileDir
	 *            文件信息
	 * @param zouts
	 *            输出流
	 */
	public static void zipDirectoryToZipFile(String dirPath, File fileDir,
			ZipOutputStream zouts) {
		if (fileDir.isDirectory()) {
			File[] files = fileDir.listFiles();
			// 空的文件夹
			if (files.length == 0) {
				// 目录信息
				ZipEntry entry = new ZipEntry(getEntryName(dirPath, fileDir));
				try {
					zouts.putNextEntry(entry);
					zouts.closeEntry();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					// 如果是文件，则调用文件压缩方法
					FileUtil.zipFilesToZipFile(dirPath, files[i], zouts);
				} else {
					// 如果是目录，则递归调用
					FileUtil.zipDirectoryToZipFile(dirPath, files[i], zouts);
				}
			}

		}

	}

	/**
	 * 
	 * 功能描述：压缩文件或目录
	 * 
	 * @param srcDirName
	 *            压缩的根目录
	 * @param fileName
	 *            根目录下的待压缩的文件名或文件夹名，其中*或""表示跟目录下的全部文件
	 * @param descFileName
	 *            目标zip文件
	 */
	public static void zipFiles(String srcDirName, String fileName,
			String descFileName) {
		// 判断目录是否存在
		if (srcDirName == null) {
			System.out.println("文件压缩失败，目录" + srcDirName + "不存在!");
			return;
		}
		File fileDir = new File(srcDirName);
		if (!fileDir.exists() || !fileDir.isDirectory()) {
			System.out.println("文件压缩失败，目录" + srcDirName + "不存在!");
			return;
		}
		String dirPath = fileDir.getAbsolutePath();
		File descFile = new File(descFileName);
		try {
			ZipOutputStream zouts = new ZipOutputStream(new FileOutputStream(
					descFile));
			if ("*".equals(fileName) || "".equals(fileName)) {
				FileUtil.zipDirectoryToZipFile(dirPath, fileDir, zouts);
			} else {
				File file = new File(fileDir, fileName);
				if (file.isFile()) {
					FileUtil.zipFilesToZipFile(dirPath, file, zouts);
				} else {
					FileUtil.zipDirectoryToZipFile(dirPath, file, zouts);
				}
			}
			zouts.close();
			System.out.println(descFileName + "文件压缩成功!");
		} catch (Exception e) {
			System.out.println("文件压缩失败：" + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 功能描述：将文件压缩到ZIP输出流
	 * 
	 * @param dirPath
	 *            目录路径
	 * @param file
	 *            文件
	 * @param zouts
	 *            输出流
	 */
	public static void zipFilesToZipFile(String dirPath, File file,
			ZipOutputStream zouts) {
		FileInputStream fin = null;
		ZipEntry entry = null;
		// 创建复制缓冲区
		byte[] buf = new byte[4096];
		int readByte = 0;
		if (file.isFile()) {
			try {
				// 创建一个文件输入流
				fin = new FileInputStream(file);
				// 创建一个ZipEntry
				entry = new ZipEntry(getEntryName(dirPath, file));
				// 存储信息到压缩文件
				zouts.putNextEntry(entry);
				// 复制字节到压缩文件
				while ((readByte = fin.read(buf)) != -1) {
					zouts.write(buf, 0, readByte);
				}
				zouts.closeEntry();
				fin.close();
				System.out
						.println("添加文件" + file.getAbsolutePath() + "到zip文件中!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param f
	 *            保存的文件
	 * 
	 * @param imgUrl
	 *            图片地址
	 */
	public void down(File f, String imgUrl) {
		byte[] buffer = new byte[8 * 1024];
		URL u;
		URLConnection connection = null;
		try {
			u = new URL(imgUrl);
			connection = u.openConnection();
		} catch (Exception e) {
			System.out.println("ERR:" + imgUrl);
			return;
		}
		connection.setReadTimeout(3);
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			f.createNewFile();
			is = connection.getInputStream();
			fos = new FileOutputStream(f);
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}

		} catch (Exception e) {
			f.delete();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		buffer = null;
		// System.gc();
	}

	/**
	 * 解压zip文件
	 * 
	 * @param targetPath
	 * @param zipFilePath
	 */
	public void unzipFile(String targetPath, String zipFilePath) {
		try {
			File zipFile = new File(zipFilePath);
			InputStream is = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry = null;
			System.out.println("开始解压:" + zipFile.getName() + "...");
			while ((entry = zis.getNextEntry()) != null) {
				String zipPath = entry.getName();
				try {
					if (entry.isDirectory()) {
						File zipFolder = new File(targetPath + File.separator
								+ zipPath);
						if (!zipFolder.exists()) {
							zipFolder.mkdirs();
						}
					} else {
						File file = new File(targetPath + File.separator
								+ zipPath);
						if (!file.exists()) {
							File pathDir = file.getParentFile();
							pathDir.mkdirs();
							file.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(file);
						int bread;
						while ((bread = zis.read()) != -1) {
							fos.write(bread);
						}
						fos.close();
					}
					System.out.println("成功解压:" + zipPath);
				} catch (Exception e) {
					System.out.println("解压" + zipPath + "失败");
					continue;
				}
			}
			zis.close();
			is.close();
			System.out.println("解压结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Java文件操作 获取文件扩展名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/*
	 * Java文件操作 获取不带扩展名的文件名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
}