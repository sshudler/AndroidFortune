package com.sergeis.androidfortune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Random;
import android.content.Context;
import android.content.res.AssetManager;
 
public class FortuneFileReader {

	private static final int NUM_FORTUNE_FILES = 3;
	private static final String[] FORTUNE_FILES = {"fortunes_a", "literature_a", "riddles_a"};
	
	private boolean mIsInitialized;
	private int[][] mFortunePointers;
	private BufferedReader[] mFortuneReaders;
	private Random m_randomGen;
	
	public FortuneFileReader() {
		
		mIsInitialized = false;
		m_randomGen = new Random(System.currentTimeMillis());
	}
	
	public boolean isInitialized() {
		
		return mIsInitialized;	
	}
	
	public String getNextFortune() throws IOException {
		
		int fortCat = m_randomGen.nextInt(NUM_FORTUNE_FILES);
		BufferedReader currReader = mFortuneReaders[fortCat];
		int[] currFortPointers = mFortunePointers[fortCat];
		
		int fortIdx = m_randomGen.nextInt(currFortPointers.length - 1);
		currReader.skip(currFortPointers[fortIdx]);
		CharBuffer charBuffer = 
			CharBuffer.allocate(currFortPointers[fortIdx+1] - currFortPointers[fortIdx] - 3);
		currReader.read(charBuffer.array(), 0, charBuffer.array().length);
		currReader.reset();
		
		return charBuffer.toString();
		
		/**
		StringBuffer fortBuff = new StringBuffer(charBuffer.toString());
		StringBuffer outBuff = new StringBuffer(fortBuff.length());
		int currIdx = 0;
		int prevIdx = 0;
		boolean newlineAppended = false;
		currIdx = fortBuff.indexOf("\n", prevIdx);
		if (currIdx == -1)	currIdx = fortBuff.length();
		outBuff.append(fortBuff.subSequence(prevIdx, currIdx));
		if (currIdx < fortBuff.length() && fortBuff.charAt(currIdx) == '\n')	currIdx++;
		
		while (currIdx < fortBuff.length())
		{
			newlineAppended = false;
			switch (fortBuff.charAt(currIdx)) {
			case '\t':
				if (currIdx+1 < fortBuff.length() && fortBuff.charAt(currIdx+1) == '\t')
				{
					outBuff.append('\n');
					newlineAppended = true;
				}
				currIdx++;
				break;
			case 'Q':
			case 'A':
				if (currIdx+1 < fortBuff.length() && fortBuff.charAt(currIdx+1) == ':')
				{
					outBuff.append('\n');
					newlineAppended = true;
				}
				break;
			}
			if (!newlineAppended)
				outBuff.append(' ');
			prevIdx = currIdx;
			currIdx = fortBuff.indexOf("\n", prevIdx);
			if (currIdx == -1)	currIdx = fortBuff.length();
			outBuff.append(fortBuff.subSequence(prevIdx, currIdx));
			if (currIdx < fortBuff.length() && fortBuff.charAt(currIdx) == '\n')	currIdx++;
		}
		
		return outBuff.toString();
		**/
	}
	
	public void close() throws IOException { 
		
		for (int i = 0; i < NUM_FORTUNE_FILES; i++)
			mFortuneReaders[i].close();	
	}
	
	public void init(Context context) throws IOException {
		
		mFortunePointers = new int[NUM_FORTUNE_FILES][];
		mFortuneReaders = new BufferedReader[NUM_FORTUNE_FILES];
		for (int i = 0; i < NUM_FORTUNE_FILES; i++)
		{
			initFortunePointers(context, i);
			int len = mFortunePointers[i][mFortunePointers[i].length-1];
			mFortuneReaders[i] =
				new BufferedReader(new InputStreamReader(context.getAssets().open(FORTUNE_FILES[i], AssetManager.ACCESS_RANDOM)), len);
			mFortuneReaders[i].mark(len);
		}
		mIsInitialized = true;
	}
	
	private void initFortunePointers(Context context, int fortuneFileIdx) throws IOException {
		
		InputStream iStream = context.getAssets().open(FORTUNE_FILES[fortuneFileIdx]+".dat");
		ByteBuffer byteBuffer = ByteBuffer.allocate(28); 
		iStream.read(byteBuffer.array(), 0, byteBuffer.array().length);
		// Read version
		byteBuffer.getInt();
		// Read number of fortune cookies
		int numFortunes = byteBuffer.getInt();
		// Read the length of the longest fortune cookie
		byteBuffer.getInt();
		// Read the length of the shortest fortune cookie
		byteBuffer.getInt();
		// Read the flags
		byteBuffer.getInt();
		// Read the delimiters
		byteBuffer.get();
		byteBuffer.position(28);
		
		mFortunePointers[fortuneFileIdx] = new int[numFortunes + 1];
		byteBuffer = ByteBuffer.allocate(numFortunes*4);
		iStream.read(byteBuffer.array(), 0, byteBuffer.array().length);
		mFortunePointers[fortuneFileIdx][0] = 0;
		for (int i = 0; i < numFortunes; i++)
			mFortunePointers[fortuneFileIdx][i+1] = byteBuffer.getInt();
	}
	
}
