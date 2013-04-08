package excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Gestion_excel {
	
	static FileInputStream file = null;
	static XSSFWorkbook workbook = null;
	public static void  create(String path){
		
		try {
			file = new FileInputStream(new File(path));
			workbook = new XSSFWorkbook(file);

		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	public static void ecrire_cellule(int n_sheet, int row, int column, double data){
		
		
		XSSFSheet sheet = workbook.getSheetAt(n_sheet);
		XSSFCell cell = null;
		cell = sheet.getRow(row-1).getCell(column-1);
		
		cell.setCellValue(data);
	}
	
	public static void evaluate(){
		workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
	}
	
	public static double getvalue(int sheet, int row, int column){
		return workbook.getSheetAt(sheet).getRow(row-1).getCell(column-1).getNumericCellValue();
	}
	public static void close(){
		
		try {
			file.close();
			FileOutputStream outFile = new FileOutputStream(new File("C:\\Users\\Toni\\Dropbox\\Réseau\\projet libre\\copie.xlsx"));
			workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
