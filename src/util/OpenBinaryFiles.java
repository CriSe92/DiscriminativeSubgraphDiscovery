package util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Scanner;

import application.ExperimentInfo;
import minig.Preprocessing;
import model.Pattern;

public class OpenBinaryFiles{
	
	static int metadata_size = 8; //Numero di byte usati come metadati nei file .ds2. Si tratta dei due interi contenenti le informazioni circa le dimensioni del file

	/*
	 *I metodi di questa classe sono pensati per leggere file creati in ambiente Matlab.
	 *In particolare, i primi due byte dei file .ds2 indicano la dimesione della matrice e, poichè la funzione che si occupa del
	 *salvataggio in Matlab come prima cosa esegue la trasporta della matrice, il primo byte indica il numero di colonne e il
	 *secondo il numero di righe  
	 */
	public static int[] discoverSize(String nomeFile) throws IOException{
		int cols, rows = -1;
		int[] res = new int[2];
		DataInputStream dis = null;
		//System.out.println(nomeFile);
		try{
			dis = new DataInputStream(new FileInputStream(nomeFile));
			
			byte[] buf = new byte[4];
			dis.read(buf);
			cols = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
			dis.read(buf);
			rows = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
			res[0] = rows;
			res[1] = cols;
		}catch(Exception uncatched){}
		finally{dis.close();}
		
		return res;
	}
	
	/*Lettura di una matrice di float triangolare*/
	public static float[][] openTriangular(String nomeFile, int unused) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] buf = new byte[4];
		dis.read(buf);
		int cols = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
		System.out.println("COLS: "+cols);
		dis.read(buf);
		int rows = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
		System.out.println("ROWS: "+rows);
		
		float[][] matrix = MatrixHelper.createMatrix(rows);
		
		float num;
		int i=0,j=0;
		try{
			for(i=0; i<rows; i++){
				for(j =0; j<=i; j++){
					dis.read(buf);
					num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					matrix[i][j] = num;
				}
				dis.skip(4*(cols-i-1));
			}
		}catch(EOFException e){
			if(i==matrix.length && i==j)
				System.out.println("File letto correttamente");
			else
				System.out.println("Il file non letto completamente");
		}
		finally{
			dis.close();
		}
		return matrix;
	}
	
	/*Lettura di una matrice di float da file .ds2
	 * Il dataset è memorizzato in forma nGenes x nSamples, quindi occorre restituire la matrice trasposta
	 * Ciò è dovuto al fatto che Matlab memorizza le matrici per colonna
	 * */
	public static float[][] openDataset(String nomeFile) throws IOException{
		File f = new File(nomeFile);
		if(!f.exists()) {
			System.out.println("File not found!");
			System.exit(0);
		}
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] buf = new byte[4];
		dis.read(buf);
		int rows = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(buf);
		int cols = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
		System.out.printf("Righe lette: %d; Colonne lette: %d;\n",rows,cols);
		float[][] matrix = new float[rows][cols];
				
		float num;
		int i=0,j=0;
		try{
			for(i=0; i<matrix[0].length; i++){
				for(j =0; j<matrix.length; j++){
					dis.read(buf);
					num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					matrix[j][i] = num;
				}
			}
		}catch(EOFException e){
			if(i==matrix.length && i==j)
				System.out.println("File letto correttamente");
			else
				System.out.println("Il file non letto completamente");
		}
		finally{
			dis.close();
		}
		return matrix;
	}
	

	public static float[][] openMatrix(String nomeFile) throws IOException{
		File f = new File(nomeFile);
		if(!f.exists()) {
			System.out.println("File not found!");
			System.exit(0);
		}
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] buf = new byte[4];
		dis.read(buf);
		int cols = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(buf);
		int rows = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//System.out.printf("Righe lette: %d; Colonne lette: %d;\n",rows,cols);
		float[][] matrix = new float[rows][cols];
				
		float num;
		int i=0,j=0;
		try{
			for(i=0; i<matrix.length; i++){
				for(j =0; j<matrix[0].length; j++){
					dis.read(buf);
					num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					matrix[i][j] = num;
				}
			}
		}catch(EOFException e){
			if(i==matrix.length && i==j)
				System.out.println("File letto correttamente");
			else
				System.out.println("Il file non letto completamente");
		}
		finally{
			dis.close();
		}
		return matrix;
	}
	
	public static boolean[][] openLogicMatrix(String nomeFile) throws IOException{
		File f = new File(nomeFile);
		if(!f.exists()) {
			System.out.println("File not found!");
			System.exit(0);
		}
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] metadata = new byte[4];
		dis.read(metadata);
		int cols = ByteBuffer.wrap(metadata).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metadata);
		int rows = ByteBuffer.wrap(metadata).order(ByteOrder.LITTLE_ENDIAN).getInt();
		System.out.printf("Righe lette: %d; Colonne lette: %d;\n",rows,cols);
		boolean[][] matrix = new boolean[rows][cols];
		
		byte[] buf = new byte[1];
		byte num;
		int i=0,j=0;
		try{
			for(i=0; i<matrix.length; i++){
				for(j =0; j<matrix[0].length; j++){
					dis.read(buf);
					num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).get();//restituisce un byte
					matrix[i][j] = (num==1);
				}
			}
		}catch(EOFException e){
			if(i==matrix.length && i==j)
				System.out.println("File letto correttamente");
			else
				System.out.println("Il file non letto completamente");
		}
		finally{
			dis.close();
		}
		return matrix;
		
	}
		
	public static short[][] openShortMatrix(String nomeFile) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] metaData = new byte[4];
		dis.read(metaData);
		int cols = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metaData);
		int rows = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		System.out.printf("Righe: %d; Colonne: %d;\n",rows,cols);
		short[][] matrix = new short[rows][cols];
		
		short num;
		byte[] buf = new byte[2];
		int i=0,j=0;
		try{
			for(i=0; i<rows; i++){
				for(j =0; j<matrix[i].length; j++){
					dis.read(buf);
					num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
					matrix[i][j] = num;
				}
			}
		}catch(EOFException e){
			if(i==matrix.length && i==j)
				System.out.println("File letto correttamente");
			else
				System.out.println("Il file non letto completamente");
		}
		finally{
			dis.close();
		}
		return matrix;
	}
	
	//Used by Graph_ShortArray
	public static short[] openShortVector(String nomeFile, int id) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] metaData = new byte[4];
		dis.read(metaData);
		int cols = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metaData);
		int rows = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//System.out.printf("Righe: %d; Colonne: %d;\n",rows,cols);
		
		if(id>rows-1){
			dis.close();
			throw new IllegalArgumentException("ID campione non valido!");
		}
		
		short[] vector = new short[cols];
		long skip = (long)id*(cols*2);
		dis.skip(skip);
	
		byte[] buf = new byte[2];
		short num;
		int i=0;
		try{
			for(i=0; i<vector.length; i++){
				dis.read(buf);
				num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
				vector[i] = num;
			}
		}catch(EOFException e){
			if(i==vector.length)
				System.out.println("File letto correttamente");
			else
				System.out.println("Il file non letto completamente");
		}
		finally{
			dis.close();
		}
		return vector;
	}
	
	
	//colsToRead, id arco prima del preprocessig
	//IL DATASET é QEULLO DOPO IL PREPROCESSING
	//Used by Graph_sparseVector
	public static SparseVector openShortVector2SparseVector(String nomeFile, int id, int[] colsToRead) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] metaData = new byte[4];
		dis.read(metaData);
		int cols = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metaData);
		int rows = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//System.out.printf("Righe: %d; Colonne: %d;\n",rows,cols);
		
		if(id>rows-1){
			dis.close();
			throw new IllegalArgumentException("ID campione non valido!");
		}
		
//		int numNodi = (int) ((1+Math.sqrt(1+4*2*cols))/2);
//		SparseVector vector = new SparseVector(numNodi);
		SparseVector vector = new SparseVector();
		long skip = (long)id*(cols*2);
		dis.skip(skip);
	
		byte[] buf = new byte[2];
		short num;
		int i=0;
		try{
			for(i=0; i<cols; i++){
				dis.read(buf);
				num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
				if(num>0)
					vector.put(colsToRead[i], num);
			}
		}catch(EOFException e){
			e.printStackTrace();
		}
		finally{
			dis.close();
		}
		return vector;
	}
	
	
	public static SparseVector openShortVector2SparseVector(String nomeFile, int id, BinaryVector colsToRead) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] metaData = new byte[4];
		dis.read(metaData);
		int cols = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metaData);
		int rows = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//System.out.printf("Righe: %d; Colonne: %d;\n",rows,cols);
		
		if(id>rows-1){
			dis.close();
			throw new IllegalArgumentException("ID campione non valido!");
		}
		
//		int numNodi = (int) ((1+Math.sqrt(1+4*2*cols))/2);
//		SparseVector vector = new SparseVector(numNodi);
		SparseVector vector = new SparseVector();
		long skip = (long)id*(cols*2);
		dis.skip(skip);
	
		byte[] buf = new byte[2];//Leggere l'intera riga per ottimizzare e muoversi poi di 2 byte
		short num;
		int i=0;
		try{
			for(i=0; i<cols; i++){
				dis.read(buf);
				num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
				if(num>0 && colsToRead.get(i))
					vector.put(i, num);
			}
		}catch(EOFException e){
			e.printStackTrace();
		}
		finally{
			dis.close();
		}
		return vector;
	}
	
	public static SparseVector openShortVector2SparseVector(String nomeFile, int id, char sample_type) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] metaData = new byte[4];
		dis.read(metaData);
		int cols = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metaData);
		int rows = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//System.out.printf("Righe: %d; Colonne: %d;\n",rows,cols);
		
		if(id>rows-1){
			dis.close();
			throw new IllegalArgumentException("ID campione non valido!");
		}
		
//		int numNodi = (int) ((1+Math.sqrt(1+4*2*cols))/2);
//		SparseVector vector = new SparseVector(numNodi);
		SparseVector vector = new SparseVector();
		long skip = (long)id*(cols*2);
		dis.skip(skip);
		
		byte[] line = new byte[2*cols];
		try{
			dis.read(line);
		}catch(EOFException e){
			e.printStackTrace();
			System.exit(0);
		}
		finally{
			dis.close();
		}
		
		int edge_id=-2, num_edges;
		byte[] buf = new byte[2];
		short num;
		
		ArrayList<Pattern> orderedEdges = Preprocessing.get_ordered_edges();
		num_edges = (orderedEdges!=null)?orderedEdges.size():line.length;		
		
		for(int i=0; i<num_edges; i++){
			edge_id = (orderedEdges!=null)?orderedEdges.get(i).getEdges()[0]*2:edge_id+2;
			buf[0] = line[edge_id];
			buf[1] = line[edge_id+1];
			num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
//			System.out.println("Edge_id: "+edge_id/2+" Value: "+num);
			if(num>0){
				vector.put(edge_id/2, num);
				/*Aggiornare il binaryVector relativo al supporto nei patterns di primo livello già inseriti in orderedEdges*/
				if(orderedEdges!=null && ExperimentInfo.id==sample_type) {
					//System.out.println("Updating edge "+orderedEdges.get(i).getEdges()[0]+" [adding sample "+id+"]");
					orderedEdges.get(i).addGraph(id);
				}
			}
		}
		return vector;
	}
	
	//Attenzione, matlab memorizza in modo trasposto
	//READ INT VECTOR CREATED BY MATLAB
	public static int[] openVector(String nomeFile, int[] info) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		
		byte[] metaData = new byte[4];
		dis.read(metaData);
		@SuppressWarnings("unused")
		int rows = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metaData);
		int cols = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//System.out.printf("Righe: %d; Colonne: %d;\n",rows,cols);
		
		byte[] buf = new byte[4];
		int num;
		int i=0;
		
		if(info[0]>0){//Il primo valore fa parte dei metadati poiché contiene il numero di geni. Simulo passaggio per riferimento per restituire tale informzione
			dis.read(buf);
			info[0]=ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
			cols=cols-1;
		}
		
		int[] ids = new int[cols];
		
		try{
			for(i=0; i<cols; i++){
				dis.read(buf);
				num = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
				ids[i]=num;
			}
		}catch(EOFException e){
			e.printStackTrace();
		}
		finally{
			dis.close();
		}
		return ids;
		
	}

	//Used by Graph_on Disk
	public static short getValue(String nomeFile, int idSample, int edge, int nEdge) throws IOException {
		RandomAccessFile r = new RandomAccessFile(nomeFile, "r");
		long pos=4+4+(long)idSample*(2*nEdge)+edge*2;
		byte[] buf = new byte[2];
		try {
			r.seek(pos);
			r.read(buf);
		} catch (IOException e) {
			System.out.println("ID SAMPLE: "+idSample);
			System.out.println("NUM EDGES: "+nEdge);
			System.out.println("EDGE: "+edge);
			e.printStackTrace();
			System.exit(0);
		}finally{
			r.close();
		}		
		return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort();
	}
	
	/*La funzione riceve il file .ds2 contnente, per ciascuna riga i, la matrice di adiacenza associata al campione i-esimo ed effettua la valutazione della commoness per ciascun arco */
	public static float[] computeCommonnessVector(String nomeFile) throws IOException{
		int[] size=discoverSize(nomeFile);//size[0]=numrows; size[1] = numcols
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		dis.skip(metadata_size);
		float[] commonness_vector = new float[size[1]];
		byte[] line = new byte[size[1]*2];
		byte[] buf = new byte[2];
		int i=0, j=0, k=0;
//		float num;
//		long time = System.currentTimeMillis();
		try{
			for(i=0; i<size[0]; i++){
				//System.out.println("Campione "+(i+1));
				k=0;
				dis.read(line); 
				for(j=0; j<size[1]; j++) {
					buf[0] = line[k];
					buf[1] = line[k+1];
					k=k+2;
					commonness_vector[j] = commonness_vector[j] + NumberHelper.toFloat((ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort()));
				}
				//System.out.println("Campione "+(i+1)+" Time: "+(System.currentTimeMillis()-time));
				//time = System.currentTimeMillis();
			}
		}catch(EOFException e){
			e.printStackTrace();
		}
		finally{
			dis.close();
		}

		return commonness_vector;
	}
	
	/*La funzione riceve il file .ds2 contnente, per ciascuna riga i, la matrice di adiacenza associata al campione i-esimo ed effettua la valutazione della commoness per ciascun arco */
	public static float[] computeCommonnessAndSupportVector(String nomeFile, BinaryVector[] support) throws IOException{
		int[] size=discoverSize(nomeFile);//size[0]=numrows; size[1] = numcols
		DataInputStream dis = new DataInputStream(new FileInputStream(nomeFile));
		dis.skip(metadata_size);
		float[] commonness_vector = new float[size[1]];
		byte[] buf = new byte[2];//Leggere una riga per volta invece di 2 byte
		int i=0, j=0;
		float num;
		//long time = System.currentTimeMillis();
		Scanner sc = new Scanner(System.in);
		System.out.println("INIZIA: ");
		sc.nextLine();
		try{
			for(i=0; i<size[0]; i++){//Num campioni
				//System.out.println("Campione "+(i+1));
				for(j=0; j<size[1]; j++) {//Num archi
					if(i==0) {
						support[j] = new BinaryVector(size[0]);
						System.out.print("BITARRAY "+(j+1)+" ");
						sc.nextLine();
					}
					dis.read(buf);
					num = NumberHelper.toFloat((ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort()));
					commonness_vector[j] = commonness_vector[j] + num;
					if(num>0) 
						support[j].set(i);
				}
				//System.out.println("Campione "+(i+1)+" Time: "+(System.currentTimeMillis()-time));
				sc.nextLine();
			}
		}catch(EOFException e){
			e.printStackTrace();
		}
		finally{
			dis.close();
		}
		
		/*Scandire nuovamente per ottenere vettore di float usando l'apposita frunzione in NUmberHelper*/
		return commonness_vector;
	}
	
	/*
	 * Divide su due file il contenuto di un file binario.
	 * E'pensato per dividere in due parti il file contnente le matrici di adiacenza di ciascun campione
	 * in modo da porre nEdges/2 in un file e i restanti in un altro
	 * */
	public static void splitBigFile(String inpurFile, String outputFile1, String outputFile2) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(inpurFile));
		byte[] metaData = new byte[4];
		
		dis.read(metaData);
		int cols = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		dis.read(metaData);
		int rows = ByteBuffer.wrap(metaData).order(ByteOrder.LITTLE_ENDIAN).getInt();
		System.out.printf("Righe: %d; Colonne: %d;\n",rows,cols);
		int cols_part1 = cols/2; //NB 5/2=2
		int cols_part2 = cols - cols_part1;
		
		byte[] buf_part1 = new byte[cols_part1*2];
		byte[] buf_part2 = new byte[cols_part2*2];
		try{
			for(int i=0; i<rows; i++){
				dis.read(buf_part1);
				dis.read(buf_part2);
				SaveBinaryFile.saveByteStream(outputFile1, buf_part1, rows, i==0?true:false);
				SaveBinaryFile.saveByteStream(outputFile2, buf_part2, rows, i==0?true:false);
			}
		}catch(EOFException e){
			e.printStackTrace();
		}finally{
			dis.close();
		}
	}


	
	public static void main(String[] args) throws IOException {
		String path = "/home/cristina/Dropbox/DiscriminativePattern_workspace/dataset/small_corrH.ds2";
		int[] size = discoverSize(path);
		System.out.println("SIZE "+size[0]+" x "+size[1]);
		float[] cv = computeCommonnessVector(path);
		for(int i=0; i<size[1]; i++) {
			System.out.println(cv[i]);
		}
		
		
	}
}
