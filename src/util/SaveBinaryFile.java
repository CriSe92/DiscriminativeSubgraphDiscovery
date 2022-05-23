package util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JOptionPane;

public class SaveBinaryFile {
	
	public static void writeDimension(int rows, int cols, DataOutputStream dos) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(8); 
		buffer.order(ByteOrder.LITTLE_ENDIAN);   
		//Scrivo prima il numero di colonne e poi il numero di righe per uniformità con Matlab
		buffer.putInt(cols);	
		buffer.putInt(rows);
		dos.write(buffer.array());
	}
	
	public static DataOutputStream open_file(String nomeFile) throws FileNotFoundException{
//		File f = new File(nomeFile);
//		if(f.exists()) {
//			int option = JOptionPane.showConfirmDialog(null, "File esistente, sovrasrivere?");
//			if(option==JOptionPane.YES_OPTION) {
//				//clean
//				f.delete();
//			}else {
//				return null;
//			}
//		}
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeFile));
		return dos;
	}
	
	public static void save(String nomeFile, float[][] matrix) throws IOException{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeFile));
		int rows = matrix.length;
		int cols = matrix[rows-1].length;
	
		writeDimension( rows, cols, dos);

		//int space = 4*(rows+rows*(rows-1)/2);
		int space = 4*cols*rows;		
		ByteBuffer buffer = ByteBuffer.allocate(space);
		buffer.order(ByteOrder.LITTLE_ENDIAN); 
		for(int i=0; i<matrix.length; i++) {
			for(int j=0; j<matrix[0].length; j++) {
				buffer.putFloat(matrix[i][j]);
			}
		}
		dos.write(buffer.array());
		dos.close();
	}
	
	public static void save_float_vector(String nomeFile, float[] vector) throws IOException{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeFile));
		int rows = 1;
		int cols = vector.length;
		
		writeDimension( rows, cols, dos);
		
		save_float_vector(dos,vector);
	}
	
	public static void save_float_vector(DataOutputStream dos, float[] vector) throws IOException{
		int space = 4*vector.length;
		ByteBuffer buffer = ByteBuffer.allocate(space);
		buffer.order(ByteOrder.LITTLE_ENDIAN); 
		for(int i=0; i<vector.length; i++) {
			buffer.putFloat(vector[i]);
		}
		dos.write(buffer.array());
	}
	
	
	public static void save_int_vector(DataOutputStream dos, int[] vector) throws IOException{
		int space = 4*vector.length;
		ByteBuffer buffer = ByteBuffer.allocate(space);
		buffer.order(ByteOrder.LITTLE_ENDIAN); 
		for(int i=0; i<vector.length; i++) {
			buffer.putInt(vector[i]);
		}
		dos.write(buffer.array());
	}
	
	
	
	/**/
	public static void saveAsShort(String nomeFile, float[] matrix, int rows, boolean isFirstRow ) throws IOException {
		File f = new File(nomeFile);
		if(f.exists() && isFirstRow) {
			int option = JOptionPane.showConfirmDialog(null, "File esistente, sovrasrivere?");
			if(option==JOptionPane.YES_OPTION) {
				//clean
				f.delete();
			}else {
				System.out.println("Procedura di salvataggio interrotta");
				System.exit(0);
			}
		}
//		for (int i = 0; i < matrix.length; i++) {
//			System.out.printf("%4.0f\t",matrix[i]*10000);
//		}
//		System.out.println();
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeFile,true));//Scrivo in coda al file
		int cols = matrix.length;
		 
		if(isFirstRow) {
			writeDimension(rows,cols,dos);
		}
		int space = 2*cols;
		ByteBuffer buffer = ByteBuffer.allocate(space);
		buffer.order(ByteOrder.LITTLE_ENDIAN); 
		for(int i=0; i<cols; i++) {
			buffer.putShort(NumberHelper.toShort(matrix[i]));
		}
		dos.write(buffer.array());
		dos.close();
	}
	
	public static void saveByteStream(String nomeFile, byte[] matrix, int rows, boolean isFirstRow) throws IOException {
		File f = new File(nomeFile);
		if(f.exists() && isFirstRow) {
			int option = JOptionPane.showConfirmDialog(null, "File "+nomeFile+" esistente, sovrasrivere?");
			if(option==JOptionPane.YES_OPTION) {
				//clean
				f.delete();
			}else {
				System.out.println("Procedura di salvataggio interrotta");
				System.exit(0);
			}
		}
//		for (int i = 0; i < matrix.length; i++) {
//			System.out.printf("%4.0f\t",matrix[i]*10000);
//		}
//		System.out.println();
		FileOutputStream dos = new FileOutputStream(nomeFile,true);//Scrivo in coda al file
		int cols = matrix.length/2;
		ByteBuffer buffer = null;
		if(isFirstRow) {
			buffer = ByteBuffer.allocate(8); 
			buffer.order(ByteOrder.LITTLE_ENDIAN);   
			//Scrivo prima il numero di colonne e poi il numero di righe per uniformità con Matlab
			buffer.putInt(cols);	
			buffer.putInt(rows);
			dos.write(buffer.array());
		}
		
		dos.write(matrix);
		dos.close();
	}
	
	
	/*Test*/
	public static void saveInt(String nomeFile, int [][] matrix) throws IOException{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeFile));
		int rows = matrix.length;
		int cols = matrix[rows-1].length;
	
		writeDimension( rows, cols, dos);
		
		int space = 4*(rows+rows*(rows-1)/2);
		ByteBuffer buffer = ByteBuffer.allocate(space);
		buffer.order(ByteOrder.LITTLE_ENDIAN); 
		for(int i=0; i<matrix.length; i++) {
			for(int j=0; j<=i; j++) {
				buffer.putInt(matrix[i][j]);
			}
		}
		dos.write(buffer.array());
		dos.close();
	}

}
