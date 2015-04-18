import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import Jama.*;

public class Main
{
    public static void main(String[] args) throws BiffException, IOException, WriteException
    {
        double[][] testMatrix = new double[21][500]; 
        double[][] trainMatrix = new double[21][2000];
        double[][] testY = new double[1][500];
        double[][] trainY = new double[1][2000];
        Workbook workbook1 = Workbook.getWorkbook(new File("Sarcos_Data1_test.xls"));
        Workbook workbook2 = Workbook.getWorkbook(new File("Sarcos_Data1_train.xls"));
        Sheet sheet1 = workbook1.getSheet(0);
        Sheet sheet2 = workbook2.getSheet(0);
        for(int j=1;j<2001;j++)
        {
        	if(j<501)
            {
        		Cell cell1 = sheet1.getCell(1, j);
                testY[0][j-1]=Double.parseDouble(cell1.getContents());
            }
        	Cell cell2 = sheet2.getCell(1, j);
            trainY[0][j-1]=Double.parseDouble(cell2.getContents());
        }
        for(int i=2;i<23;i++)
        {
            for(int j=1;j<2001;j++)
            {
                if(j<501)
                {
                    Cell cell1 = sheet1.getCell(i, j);
                    testMatrix[i-2][j-1]=Double.parseDouble(cell1.getContents());
                }
                Cell cell2 = sheet2.getCell(i, j);
                trainMatrix[i-2][j-1]=Double.parseDouble(cell2.getContents());
            }
        }
        workbook1.close();
        workbook2.close();
        for(int i=0;i<21;i++)
        {
            System.out.println(testMatrix[i][499]);
        }
        /*
        for(int j=0; j < 500; j++)
        {
        	System.out.println(testY[0][j]);
        }*/
        
        Matrix test = new Matrix(testMatrix);
        Matrix testT = test.transpose();
        Matrix xTX = test.times(testT);
        Matrix invXTX = xTX.inverse();
        Matrix ident = xTX.times(invXTX);
        System.out.println(xTX.getRowDimension() + " ," + xTX.getColumnDimension());
        System.out.println(ident.getRowDimension() + " ," + ident.getColumnDimension());
        /*double[][] test2 = {{1.0,2.0},{3.0,4.0}};
        Matrix test2Matrix = new Matrix(test2);
        Matrix testIdent = test2Matrix.inverse().times(test2Matrix);*/
        for(int i=0; i<21; i++)
        {
        	for(int j=0; j<21; j++)
        	{
        		System.out.print(Math.round(ident.get(i, j)) + " ");
        	}
        	System.out.println();
        }
        /*System.out.println(testIdent.getRowDimension() + " ," + testIdent.getColumnDimension());
        for(int i=0; i<2; i++)
        {
        	for(int j=0; j<2; j++)
        	{
        		System.out.print(testIdent.get(i, j) + " ");
        	}
        	System.out.println();
        }*/
    }
}