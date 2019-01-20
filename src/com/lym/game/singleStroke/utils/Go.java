package com.lym.game.singleStroke.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import com.lym.game.singleStroke.objects.Checkpoint;
import com.lym.game.singleStroke.objects.MyPosition;

public class Go {
	public static class Result_Of_Analysis{
		public static int L;
		public static int Lg;
		public static int[][] array;
		public static MyPosition firstPosition; 
		//public static MyPosition startPosition; 
		public static int startI; 
		public static int startJ; 
	}
	
	public static BufferedImage convertGray(BufferedImage bi, String newFileName, int threshold) throws IOException {
		{
			//BufferedImage bi = ImageIO.read(new File(bi));// 通过imageio将图像载入
			int h = bi.getHeight();// 获取图像的高
			int w = bi.getWidth();// 获取图像的宽
			int[][] gray = new int[w][h];
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					gray[x][y] = getGray(bi.getRGB(x, y));
				}
			}

			BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
			int SW = threshold;//分析起点：200    智能分析图片:240
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
						nbi.setRGB(x, y, isBlack(gray, x, y, SW)?0xffffff:0);
				}
			}
			removeNoise(nbi);
			if(newFileName != null)
				ImageIO.write(nbi, "jpg", new File(newFileName + ".jpg"));   //
			return nbi;
			/*TIFFImageWriterSpi tiffws = new TIFFImageWriterSpi();
			ImageWriter writer = tiffws.createWriterInstance();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionType("CCITT T.6");
			param.setCompressionQuality(0.8f);
			File outFile = new File("D:/Test/binary/二值化后_有压缩.tiff");
			ImageOutputStream ios = ImageIO.createImageOutputStream(outFile);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(nbi, null, null), param);*/
		}

	}
	public static void removeNoise(BufferedImage nbi) {
			for (int y = 223; y < 315; y++) {
				for (int x = 0; x < 240; x++) {
					nbi.setRGB(x, y, 0xffffff);
			}
		}
	}
	public static int getGray(int rgb){
		int r = (rgb & 0xff0000) >> 16; 
		int g = (rgb & 0xff00) >> 8; 
		int b = rgb & 0xff; 
		return (r+g+b)/3;
	}
	/**
	 * @author StarMonitor
	 * 		由于边界点无所谓，边缘点周边为0
        	由于阈值可调，这里直接位运算除8，threshold = (9*real_threshold) >> 3处理即可,
        	多进行1次除法运->用位运算代替了hight*wight(200w)次除法运算
	 */
	public static boolean  isBlack(int[][] gray, int x, int y, int threshold)
    {
        int rs = gray[x][y]
                  	+ (x == 0 ? 0 : gray[x - 1][y])
		            + (x == 0 || y == 0 ? 0 : gray[x - 1][y - 1])
		            + (x == 0 || y == gray[0].length - 1 ? 0 : gray[x - 1][y + 1])
		            + (y == 0 ? 0 : gray[x][y - 1])
		            + (y == gray[0].length - 1 ? 0 : gray[x][y + 1])
		            + (x == gray.length - 1 ? 0 : gray[x + 1][ y])
		            + (x == gray.length - 1 || y == 0 ? 0 : gray[x + 1][y - 1])
		            + (x == gray.length - 1 || y == gray[0].length - 1 ? 0 : gray[x + 1][y + 1]);
        return rs >> 3 > threshold;
    }
	public static void analysisStart(BufferedImage image) throws Exception {
		for(int y = 315; y < 1250; y += 5 ) {
			for(int x = 10; x < 710; x+=5)
				if(isStart(image,x,y) ){
					//Result_Of_Analysis.startPosition = new MyPosition(x, y);
					initStartIndex(x,y);
					return;
				}
		}
	}
	public static boolean isStart(BufferedImage image,int x,int y ) throws Exception {
		//横着找
		for(int i = 0; i < 15; i++) {
			for(int w = 0; w < 5; w++ ) {
				if(getSumRGB(image.getRGB(x+i,y + w))>300)//有白色像素就返回
					return false;
			}		
		}
		//竖着找
		for(int i = 0; i < 15 ; i++) {
			for(int w = 0; w < 5; w++ ) {
				if(getSumRGB(image.getRGB(x+w,y + i))>100)//有白色像素就返回
					return false;
			}		
		}
		return true;
	}
	public static void initStartIndex(int x, int y) {
		int lg = Result_Of_Analysis.Lg;
		int firstX = Result_Of_Analysis.firstPosition.x;
		int firstY = Result_Of_Analysis.firstPosition.y;
		for(int i = 0; i < Result_Of_Analysis.array.length; i++) {
			for(int j = 0; j < Result_Of_Analysis.array[0].length; j++) {
				if((x < firstX + j * lg) && (y < firstY + i * lg)) {
					Result_Of_Analysis.startI = i;
					Result_Of_Analysis.startJ = j;
					return;
				}
			}
		}
	}
	public static void initArray(BufferedImage image) {
		int lg = Result_Of_Analysis.Lg;
		int firstX = Result_Of_Analysis.firstPosition.x;
		int firstY = Result_Of_Analysis.firstPosition.y;
		for(int i = 0; i < Result_Of_Analysis.array.length; i++) {
			for(int j = 0; j < Result_Of_Analysis.array[0].length; j++) {
					Result_Of_Analysis.array[i][j] = getSumRGB(image.getRGB(firstX + j * lg, firstY + i * lg))<300 ? 0 : -1;
			}
		}
	}

	public static void main(String[] args) throws Exception {
//选择下一个未通过的关卡
	selectNext();
	//破解当前关	
		//1.adb截屏传到电脑
		screenCap();
		Thread.sleep(2000);
		//System.out.println("正在获取屏幕..");
		//2.打开图片，创建副本(这里为代码编写简单，采用读两次的方式代替复制)，对其进行二值化240
		//System.out.print("读取分析中.");
		BufferedImage region ,backups;
		region = ImageIO.read(new File("screen.png"));
		backups = ImageIO.read(new File("screen.png"));
		//3.识别以上二值化的图像获得行列数，方格大小，方格间隙，以及理论上第一个方格的屏幕坐标，最后生成初始矩阵
		region = convertGray(region,null,240);
		analysisImage(region);
		//4.对内存中的原图片副本进行二值化180，
		backups = convertGray(backups,null,200);
		//5.对以上图像识别，获得起点像素坐标，求出起点对应下标
		analysisStart(backups);
		//6.初始化矩阵
		initArray(region);
		//7.对由图片分析出的迷宫进行求解
		//System.out.println("图片智能分析完毕，准备求解");
		Checkpoint question = new Checkpoint(Result_Of_Analysis.array,Result_Of_Analysis.startI,Result_Of_Analysis.startJ);
		question.caculate();
		//8.输出解   +  另一个线程异步用adb进行破解
		//System.out.println("求解完毕，正在自动通关");
		//question.print_plus2();
		(new Thread(new Runnable() {
			public void run() {
				try {
					question.print_plus2();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		})).start();
		through(question.getArray());
	}
	
	private static void selectNext() {
	}
	public static void through(int[][] array) throws Exception {
		TreeMap<Integer, MyPosition> map = new TreeMap<Integer,MyPosition>();
		for(int i = 0; i < array.length; i++) {
			for(int j = 0; j < array[0].length; j++) {
				map.put(array[i][j], new MyPosition(i,j));
			}
		}
		Iterator<Integer> iterator = map.keySet().iterator();
		
		int lg = Result_Of_Analysis.Lg;
		int firstX = Result_Of_Analysis.firstPosition.x;
		int firstY = Result_Of_Analysis.firstPosition.y;
		
		while (iterator.hasNext()) {
			int key = (int) iterator.next();
			if(key > 0) {
				click(firstX + lg * map.get(key).y, firstY + lg * map.get(key).x);
			}
			Thread.sleep(400);
		}
	}
	static void click(int x,int y) throws Exception {
		//System.out.println("点击屏幕("+ x +"," + y+")");
		cmd("adb shell input tap "+ x +" " + y);
	}
	//截图
	static void screenCap() throws Exception {
		cmd("adb shell screencap -p /sdcard/screen.png");
		cmd("adb pull /sdcard/screen.png");
	}
	//cmd("adb shell input swipe 900 2340 300 2340");//滑动
	static void cmd(String s) throws IOException {
		Runtime.getRuntime().exec(s); 
	}
	private static void analysisImage(BufferedImage bi) throws Exception {
		/*
		 * 求解：row、column、L+Gap、L（默认gap=10）理论第一个坐标
		 * 
		 */
		/*String filePath
		BufferedImage bi = ImageIO.read(new File(filePath));*/
		int h = 1250;
		int tempSum = 0;
		int i = 10;
		boolean hangFlag = false;
		ArrayList<Integer> row_Y_array = new ArrayList<Integer>();
		ArrayList<Integer> column_X_array = new ArrayList<Integer>();
		int gap = 10;
		int L = 0;
		MyPosition firstPosition;
		for(i = 315; i < h; ) {
			if(drawRect(bi,30,i,5) ){
				tempSum++;
				System.out.println("row:" + i);
				row_Y_array.add(i);
				i+=30;
			}else {
				hangFlag = false;
				for(int z = 50; z <= 200; z+=50)
					if(drawRect(bi,z,i,5)) {
						tempSum++;
						System.out.println("row:" + i);
						row_Y_array.add(i);
						i+=30;
						hangFlag = true;
						break;
					}	
				if(!hangFlag)
					i++;
			}	
		}
		int row = tempSum;
		tempSum = 0;
		//System.out.println(tempSum);
		//竖：
		for(i = 100; i < 700; ) {
			if(drawRect2(bi,i,315,5) ){
				tempSum++;
				System.out.println("column:" + i);
				column_X_array.add(i);
				i+=30;
			}else {
				hangFlag = false;
				for(int z = 355; z <= 800; z+=50) {
					
					if(drawRect2(bi,i,z,5)) {
						tempSum++;
						System.out.println("column:" + i);
						column_X_array.add(i);
						hangFlag = true;
						i+=30;
						break;
					}	
				}
					
				if(!hangFlag)
					i++;
			}	
		}
		int column = tempSum;
		L = ((column_X_array.get(column_X_array.size()-1) - column_X_array.get(0))/(column - 1) +
				(row_Y_array.get(row_Y_array.size()-1) - row_Y_array.get(0))/(row - 1) )/2 - gap;
		firstPosition = new MyPosition(column_X_array.get(0)-L/2, row_Y_array.get(0) - L/2);
		
		Result_Of_Analysis.array = new int[row][column];
		Result_Of_Analysis.firstPosition = firstPosition;
		Result_Of_Analysis.L = L;
		Result_Of_Analysis.Lg = L + gap;
		
		//ImageIO.write(bi, "jpg", new File("testRect.jpg"));
	}
	public static boolean drawRect(BufferedImage image, int x,int y, int width) {
		int countBlack = 0;
		for(int i = 0; i < 120; i++) {
			if(getSumRGB(image.getRGB(x+i,y-1))<300) {
				countBlack++;
				if(countBlack>20)
					break;
			}
			if(i > 100 && countBlack<20)
				return false;
		}
		for(int i = 0; i < 100; i++) {
			for(int w = 0; w < width; w++ ) {
				if(getSumRGB(image.getRGB(x+i,y + w))<300)//有黑色像素
					return false;
			}		
		}
		/*for(int i = 0; i < 200; i++) {
			for(int w = 0; w < width; w++ ) {
				image.setRGB(x+i,y + w,0xffff00);
			}		
		}*/
		return true;
	}
	//竖着画，x>1
	public static boolean drawRect2(BufferedImage image, int x,int y, int width) {
		int countBlack = 0;
		for(int i = 0; i < 120; i++) {
			if(getSumRGB(image.getRGB(x-1,y+i))<300) {
				countBlack++;
				if(countBlack>20)
					break;
			}
			else if(i > 100 && countBlack<20)
				return false;
		}
		for(int i = 0; i < 100 ; i++) {
			for(int w = 0; w < width; w++ ) {
				if(getSumRGB(image.getRGB(x+w,y + i))<100)//黑色像素
					return false;
			}		
		}
		/*for(int i = 0; i < 200; i++) {
			for(int w = 0; w < width; w++ ) {
				image.setRGB(x+w,y + i,0xbb2200);
			}		
		}*/
		return true;
	}

	
	public static int getSumRGB(int rgb) {
			int r = (rgb & 0xff0000) >> 16; 
			int g = (rgb & 0xff00) >> 8; 
			int b = rgb & 0xff; 
			return r+g+b;
	}
}
