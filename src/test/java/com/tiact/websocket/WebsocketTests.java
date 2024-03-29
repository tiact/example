package com.tiact.websocket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebsocketTests {



	public  void compressPNG(File inputFile, File outputFile, float compressionQuality) {
		try {
			BufferedImage image = ImageIO.read(inputFile);

			// 原始尺寸
			int originalWidth = image.getWidth();
			int originalHeight = image.getHeight();

			// 缩小尺寸
			int newWidth = originalWidth / 2;
			int newHeight = originalHeight / 2;

			// 创建缩小后的图片
			BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
			resizedImage.getGraphics().drawImage(image, 0, 0, newWidth, newHeight, null);

			// 输出压缩后的图片
			ImageIO.write(resizedImage, "png", outputFile);

			System.out.println("压缩完成！");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ImageCompressor(File inputFile,String path,String formatName){
		if(Stream.of("jpg","png").anyMatch(formatName::contains)){
			//formatName = formatName.substring(1);
			try {
				BufferedImage originalImage = ImageIO.read(inputFile);

				// 设置输出图片的压缩参数
				float compressionQuality = 0.5f; // 压缩质量，范围为 0.0 到 1.0
				formatName = "jpg"; // 输出图片的格式

				// 获取 ImageWriter 对象
				ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(formatName).next();

				// 设置 ImageWriteParam 对象
				ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
				imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				imageWriteParam.setCompressionQuality(compressionQuality);

				// 禁用颜色空间转换
				//imageWriteParam.setDestinationType(imageWriteParam.getDestinationType());
				// 设置目标图像类型
				ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
				imageWriteParam.setDestinationType(imageTypeSpecifier);

				// 写入压缩后的图片
				File outputFile = new File(path+ File.separator + "scale" + File.separator + inputFile.getName());
				if (!outputFile.getParentFile().exists()) {
					outputFile.getParentFile().mkdirs();
				}
				FileImageOutputStream outputStream = new FileImageOutputStream(outputFile);
				imageWriter.setOutput(outputStream);
				imageWriter.write(null, new IIOImage(originalImage, null, null), imageWriteParam);

				// 关闭输出流
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	@Test
	public void testImg(){
		//File outputFile = new File(path+ File.separator + "scale" + File.separator + tempFile.getName());
		// hutool
		//Img.from(tempFile).setQuality(0.5).write(outputFile);
		// imageio
		//compressPNG(tempFile,outputFile,0.5f);
		//ImageCompressor(tempFile,path,suffixName);
		String[] imageUrls = {
				"http://picture.septwolves.info:8082/SKU/2024/1D1E70603995_003.jpg"
		};

		for (int i = 0; i < imageUrls.length; i++) {
			String imageUrl = imageUrls[i];
			String destinationFile = "image" + (i + 1) + ".jpg";

			try {
				URL url = new URL(imageUrl);
				URLConnection connection = url.openConnection();
				try (InputStream inputStream = connection.getInputStream()) {
					Files.copy(inputStream, Paths.get(destinationFile));
					System.out.println("Image " + (i + 1) + " downloaded successfully.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	@Test
	public void testArr(){
		Integer[] numbers = {5,3,11,8,15,2};
		Integer[] nums = {5,21,18,15,12};
		Arrays.parallelSort(numbers);

		List<Integer> list = Arrays.asList(numbers);

		List<Integer> numList = Arrays.asList(nums);


		//交集
		List<Integer> intersection = list.stream().filter(numList::contains).collect(Collectors.toList());
		System.out.println(intersection);

//		list.forEach(System.out::print);

		List<Integer> listAll = list.parallelStream().collect(Collectors.toList());
		List<Integer> listAll2 = numList.parallelStream().collect(Collectors.toList());
		//并集
		listAll.addAll(listAll2);

		System.out.println(listAll.stream().max(Integer::compareTo).get());

		//并集去重
//		listAll.stream().distinct().forEach(System.out::println);


//		System.out.println(list.stream().map(x -> x*2).reduce(0,Integer::sum));
//		System.out.println(list.stream().filter(x -> x > 0).reduce(0,Integer::sum));
//		System.out.println(Arrays.stream(numbers).reduce(0,Integer::sum));
	}

	@Test
	public void test() throws IOException {
		String route = "/";
		String path = "D:/data/s";
		path = path + route;
		File file = new File(path);
		if(file.exists()){
			File[] files = file.listFiles();
			if(files.length>0){
				File zipFile = new File("d:/data" + File.separator + "hello.zip");
				ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(
						zipFile));
				List<File> fileList = new ArrayList<>();
				batchFile(file,fileList);
				for(File f : fileList){
					int len = path.length();
					if(path.lastIndexOf("/")!=len-1){
						len += 1;
					}
					System.out.println(len);
					String fileName = f.getAbsolutePath();
					System.out.println(fileName);
					InputStream input = new FileInputStream(f);
					System.out.println(fileName.substring(len));
					zipOut.putNextEntry(new ZipEntry(fileName.substring(len)));
					int temp = 0;
					//读取相关的文件
					while((temp = input.read()) != -1){
						//写入输出流中
						zipOut.write(temp);
					}
					input.close();
				}
				// 设置注释
				zipOut.setComment("hello");
				//关闭流
				zipOut.close();
			}else{
				System.out.println("没有文件");
			}
		}else{
			System.out.println("文件不存在");
		}
	}


	@Test
	public void searchTest(){
		File file = new File("D:/data/s/");
		List<File> fileList = new ArrayList<>();
		batchFile(file,fileList);
		System.out.println(fileList.toString());
	}

	public void batchFile(File file,List<File> fileList) {
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.exists()&&f.isFile()) {
				fileList.add(f);
			} else {
				batchFile(f,fileList);
			}
		}
	}


	@Test
	public void patternTest() {
		String reg = "[a-z]";
		String str = "agDA";
		System.out.println(str.matches(reg));
	}


	public long getFileSize(File file) {
		return file.length();
	}

	public BigDecimal tranSize(BigDecimal val,int hit){
//		BigDecimal fileSize = new BigDecimal(val);
		BigDecimal div = new BigDecimal(1024);
		if(val.compareTo(div) > 0){
			hit++;
			return tranSize(val.divide(div),hit);
		}
		return val;
	}

	@Test
	public void TestWs() throws IOException {
		Properties props = System.getProperties();

		System.out.println("操作系统的名称：" + props.getProperty("os.name"));
		System.out.println("Java的安装路径：" + props.getProperty("java.home"));
		System.out.println("Java的虚拟机规范版本号：" + props.getProperty("java.vm.specification.version"));
		System.out.println("操作系统的构架：" + props.getProperty("os.arch"));
		System.out.println("用户的账户名称：" + props.getProperty("user.name"));
		System.out.println("用户的主文件夹：" + props.getProperty("user.home"));
		System.out.println("用户的当前工作文件夹：" + props.getProperty("user.dir"));
		String path = "D:/data/s";
		File file = new File(path);
		if(file.isDirectory()){
//			File file1 = new File(path,"root");
//			if(!file1.exists()){
//				file1.mkdirs();
//			}
			String[] arr = file.list();
			Map<String, List<String>> result = new HashMap<>();
			List<String> dirList = new ArrayList<>();
			List<String> fileList = new ArrayList<>();
			Map<String,Object> map = new HashMap<>();

			for (int i = 0; i < arr.length; i++) {
				File f = new File(path+File.separator+arr[i]);
				if(f.isDirectory()){
					dirList.add(arr[i]);
				}else{
					fileList.add(arr[i]);
					long fileSize = getFileSize(f)/1024;
					BigDecimal  tranArg = tranSize(new BigDecimal(fileSize),0).setScale(1,BigDecimal.ROUND_UP);
					String sizeStr = "";
					if(fileSize>1024*1024){
						sizeStr = tranArg + "G";
					}else if(fileSize>1024){
						sizeStr = tranArg + "M";
					}else{
						sizeStr = tranArg + "kb";
					}
					map.put(arr[i],sizeStr);
				}
			}
			result.put("dir",dirList);
			result.put("file",fileList);
			for(Map.Entry<String, Object> s:map.entrySet()){
				System.out.println(s.getKey()+":"+s.getValue());
			}
//			for(Map.Entry entry:result.entrySet()){
//				System.out.println(entry.getKey()+"："+entry.getValue().toString());
//			}
		}
	}

}
