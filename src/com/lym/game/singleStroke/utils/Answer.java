package com.lym.game.singleStroke.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.lym.game.singleStroke.objects.Checkpoint;

public class Answer {
	static int[][] array //= new int[8][6];
	
	 /*= {
			{0,	0,	0, 0},
			{0,	0,	0, 0},
			{0,	0,	0, 0},
			{-1,-1,	0,-1}};*/
	/*= {
			{0,	0,	0, 0,	0,	0},
			{0,	-1,	0, 0,	-1,	0},
			{0,	0,	0, 0,	0,	0},
			{0,	-1,	0, 0,	-1,	0},
			{0,	0,	0, 0,	0,	0}};*/
	
	= {
			{0,	-1,	0, 0,	0,	-1},
			{0,	0,	0, 0,	0,	0},
			{0,	0,	0, 0,	0,	0},
			{0,	0,	0, -1,	0,	0},
			
			{-1,0,	0, 0,	0,	0},
			{0,	0,	0, 0,	-1,	0},
			{0,	0,	0, 0,	0,	0},
			{0,	0,	0, 0,	0,	0}};
	public static void main(String[] args) throws Exception {
/*		array[0][1] = -1;
		array[0][5] = -1;
		array[3][3] = -1;
		array[4][0] = -1;
		array[5][4] = -1;
		array[0][1] = -1;
		array[1][1] =  1;*/
		
		Checkpoint question = new Checkpoint(array,1,1);
		
		question.caculate();
		
		question.print_plus2();
		
		
	}

	
}
