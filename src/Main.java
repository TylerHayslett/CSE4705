import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import jxl.read.biff.BiffException;

import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Main
{
    public static void main(String[] args) throws BiffException, IOException, WriteException
    {
        double[][] testMatrix = new double[22][500]; 
        double[][] trainMatrix = new double[22][2000];
        Workbook workbook1 = Workbook.getWorkbook(new File("Sarcos_Data1_test.xls"));
        Workbook workbook2 = Workbook.getWorkbook(new File("Sarcos_Data1_train.xls"));
        Sheet sheet1 = workbook1.getSheet(0);
        Sheet sheet2 = workbook2.getSheet(0);
        for(int i=1;i<23;i++)
        {
            for(int j=1;j<2001;j++)
            {
                if(j<501)
                {
                    Cell cell1 = sheet1.getCell(i, j);
                    testMatrix[i-1][j-1]=Double.parseDouble(cell1.getContents());
                }
                Cell cell2 = sheet2.getCell(i, j);
                trainMatrix[i-1][j-1]=Double.parseDouble(cell2.getContents());
            }
        }
        workbook1.close();
        workbook2.close();
        for(int i=0;i<22;i++)
        {
            System.out.println(testMatrix[i][499]);
        }
    }
}