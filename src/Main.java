import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import jxl.NumberCell;

import Jama.*;

public class Main
{
    public static void main(String[] args) throws BiffException, IOException, WriteException
    {
    	//load data from excel sheets
        double[][] testMatrix = new double[500][21]; 
        double[][] trainMatrix = new double[2000][21];
        double[][] testY = new double[500][1];
        double[][] trainY = new double[2000][1];
        Workbook workbook1 = Workbook.getWorkbook(new File("Sarcos_Data1_test.xls"));
        Workbook workbook2 = Workbook.getWorkbook(new File("Sarcos_Data1_train.xls"));
        Sheet sheet1 = workbook1.getSheet(0);
        Sheet sheet2 = workbook2.getSheet(0);
        for(int i=1;i<2001;i++)
        {
        	if(i<501)
            {
        		Cell cell1 = sheet1.getCell(1, i);
        		NumberCell n1 = (NumberCell) cell1;
                testY[i-1][0]=n1.getValue();
            }
        	Cell cell2 = sheet2.getCell(1, i);
        	NumberCell n2 = (NumberCell) cell2;
            trainY[i-1][0]=n2.getValue();
        }
        for(int j=2;j<23;j++)
        {
            for(int i=1;i<2001;i++)
            {
                if(i<501)
                {
                    Cell cell1 = sheet1.getCell(j, i);
                    NumberCell n1 = (NumberCell) cell1;
                    testMatrix[i-1][j-2]=n1.getValue();
                }
                Cell cell2 = sheet2.getCell(j, i);
                NumberCell n2 = (NumberCell) cell2;
                trainMatrix[i-1][j-2]=n2.getValue();
            }
        }
        workbook1.close();
        workbook2.close();
        
        //Matrix w = MathUtils.ridgeReg(trainY,trainMatrix,0);
        System.out.println(MathUtils.tenFoldCV(trainY,trainMatrix,1000));
        
        /*
        System.out.println(w.getRowDimension() + " ," + w.getColumnDimension());
        for(int i=0; i<21; i++){
        	System.out.println(w.get(i, 0));
        }
        */
        
    }
}