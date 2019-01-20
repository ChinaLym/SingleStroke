package com.lym.game.singleStroke.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.lym.game.singleStroke.objects.MyPosition;

public class BinaryImage {
	public static void convertGray(String filePath) throws IOException {
		{
			BufferedImage bi = ImageIO.read(new File(filePath));// 通过imageio将图像载入
			int h = bi.getHeight();// 获取图像的高
			int w = bi.getWidth();// 获取图像的宽
			int[][] gray = new int[w][h];
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					gray[x][y] = getGray(bi.getRGB(x, y));
				}
			}

			BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
			int SW = 240;
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
						nbi.setRGB(x, y, isBlack(gray, x, y, SW)?0xffffff:0);
				}
			}
			removeNoise(nbi);
            ImageIO.write(nbi, "jpg", new File("outPut.jpg"));   //
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
	public static boolean isRect(BufferedImage image, int x, int y,int l) {
		for(int i = x; i < x+l; i++) {
			
		}
		return true;
	}
	public static void main(String[] args) throws Exception {
		//generateArray("2.jpg");
		//convertGray("temp.jpg");
		draw("2.jpg");
	}
/*	public static int findBottom(BufferedImage image) {
		//检测水平线
		for(int height = 800; height < image.getHeight(); height ++) {
			out:
			for(int width = 0; width < image.getWidth(); width ++) {
				if(getSumRGB(image.getRGB(width,height)) <300 )
					break out;
				for(int d = 0; d < 50;d++) {
					
				}
				return height;
			}
			
		}
		return -1;
	}*/
	private static void draw(String filePath) throws Exception {
		/*
		 * 求解：row、column、L+Gap、L（默认gap=10）理论第一个坐标
		 * 
		 */
		
		
		BufferedImage bi = ImageIO.read(new File(filePath));
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
		
		ImageIO.write(bi, "jpg", new File("testRect.jpg"));
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
		for(int i = 0; i < 200; i++) {
			for(int w = 0; w < width; w++ ) {
				image.setRGB(x+i,y + w,0xffff00);
			}		
		}
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
		for(int i = 0; i < 200; i++) {
			for(int w = 0; w < width; w++ ) {
				image.setRGB(x+w,y + i,0xbb2200);
			}		
		}
		return true;
	}

	public static int[][] generateArray(String filePath) throws Exception{
		if(filePath == null)
			filePath = "outPut_1";
		BufferedImage bi = ImageIO.read(new File(filePath));// 通过imageio将图像载入
		System.out.println("Height:"+bi.getHeight()+"  Width:"+bi.getWidth());
		int h = 1250;//bi.getHeight();// 获取图像的高
		int w = bi.getWidth();// 获取图像的宽
		int row = 0;
		int column = 0;
		
		int tempSum = 0;
		int maxSum = 0;
		boolean flag = false;
		
		/*boolean[][] gray = new boolean[w][h];//true:black,false:white
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				gray[x][y] = getSumRGB(bi.getRGB(x, y)) > 100;
			}
		}*/
		
		//计算行数和列数
		int i = 10,j =1;
		//计算有多少列
		for(i = 315; i < h; i += 10) {
			tempSum = 0;
			for (j = 1; j < w; j++) {
				//遍历每行
				if(getSumRGB(bi.getRGB(j, i)) - getSumRGB(bi.getRGB(j - 1, i))>100)
					//与左边像素对比
				{
					tempSum++;
					j+=30;
					System.out.println("x:"+ j +" y:" + i);
				}
			}
			System.out.println("--count:  " + tempSum);
			if(tempSum > maxSum) {
				maxSum = tempSum;
				i+=40;
			}
		} 
		column = maxSum;
		
		/*for (j = 100; j < w; j+=10) {
			tempSum = 0;
			for(i = 1; i < h; i ++) {
				//遍历每列
				if(getSumRGB(bi.getRGB(j, i)) - getSumRGB(bi.getRGB(j, i-1))>100)
					//与左边像素对比
				{
					tempSum++;
					i+=10;
				}
			}
			if(tempSum > maxSum)
				maxSum = tempSum;	
			//System.out.println(tempSum);
		}*/ 	
		boolean throughBlack = true;
		boolean flagT = true;
		/*tempSum = 0;
		for(i = 1; i < w; ) {
			if(throughBlack&&(flagT = isWhiteLine_horizontal(bi,i,316,4))) {
				tempSum++;
				System.out.println("column" + i);
				i+=30;
				throughBlack = false;
			}else {
				i+=1;
				if(!flagT)
					throughBlack = true;
			}	
			if(tempSum > maxSum)
				maxSum = tempSum;	
			//System.out.println(tempSum);
		}
		column = maxSum - 1;*/
		
		throughBlack = true;
		flagT = true;
		tempSum = 0;
		for(i = 315; i < h; ) {
			if(throughBlack&&(flagT = isWhiteLine_horizontal(bi,10,i,4))) {
				tempSum++;
				System.out.println("row:" + i);
				i+=30;
				throughBlack = false;
			}else {
				i+=1;
				if(!flagT)
					throughBlack = true;
			}	
			if(tempSum > maxSum)
				maxSum = tempSum;	
			//System.out.println(tempSum);
		}
		row = tempSum - 1;
		
		System.out.println("row:" + row);
		System.out.println("column:" + column);
		int[][] array = new int[row][column];
		
		//处理开始位置，障碍物位置
		
		return array;
		
	}
	public static boolean isWhiteLine_horizontal(BufferedImage image, int x, int y,int width) {
		//检测水平线
		for(int i = x; x+i < 200; i++) {
			for(int w = 0; w < width; w++ ) {
				if(getSumRGB(image.getRGB(x+i,y + w))<300)//有黑色像素
					return false;
			}		
		}
		return true;
	}
	public static boolean isWhiteLine_vertical(BufferedImage image, int x, int y,int width) {
		//检测垂直白线
		for(int i = x; x+i < 400; i++) {
			for(int w = 0; w < width; w++ ) {
				if(getSumRGB(image.getRGB(x+w,y + i))<300)//有黑色像素
					return false;
			}		
		}
		return true;		
	}
	public static int getSumRGB(int rgb) {
			int r = (rgb & 0xff0000) >> 16; 
			int g = (rgb & 0xff00) >> 8; 
			int b = rgb & 0xff; 
			return r+g+b;
	}
	
}
