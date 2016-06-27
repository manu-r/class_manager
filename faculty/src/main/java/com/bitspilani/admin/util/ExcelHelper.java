package com.bitspilani.admin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by I311849 on 17/Jun/2016.
 */
public class ExcelHelper {
    private WritableCellFormat cellFormat;
    private File outputFile;
    private ArrayList<Faculty> faculties;

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void write(ArrayList<Faculty> faculties) throws IOException, WriteException {
        this.faculties = faculties;
        WorkbookSettings settings = new WorkbookSettings();
        settings.setLocale(Locale.ENGLISH);
        WritableWorkbook writableWorkbook = Workbook.createWorkbook(this.outputFile, settings);

        writableWorkbook.createSheet("Session", 0);
        WritableSheet writableSheet = writableWorkbook.getSheet(0);

        formatHeadingCells(writableSheet);
        createHeadings(writableSheet);
        createContent(writableSheet);

        CellView cell;
        for (int i = 0; i < 9; i++) {
            cell = writableSheet.getColumnView(i);
            cell.setSize(20 * 256);
            cell.setAutosize(true);
            writableSheet.setColumnView(i, cell);
        }

        writableWorkbook.write();
        writableWorkbook.close();
    }

    private void createHeadings(WritableSheet writableSheet) throws WriteException {
        WritableFont headingFont = new WritableFont(WritableFont.ARIAL, 12);
        headingFont.setBoldStyle(WritableFont.BOLD);
        // Define the cell format
        cellFormat = new WritableCellFormat(headingFont);
        cellFormat.setAlignment(Alignment.CENTRE);


        Label label;
        //Faculty Details ================================================================
        label = new Label(0, 0, "Name", cellFormat);
        writableSheet.addCell(label);

        label = new Label(1, 0, "Email", cellFormat);
        writableSheet.addCell(label);

        label = new Label(2, 0, "Ph. No.", cellFormat);
        writableSheet.addCell(label);

        //Session Details ================================================================
        label = new Label(3, 0, "Session Timings", cellFormat);
        writableSheet.addCell(label);

        label = new Label(3, 2, "Start", cellFormat);
        writableSheet.addCell(label);

        label = new Label(4, 2, "End", cellFormat);
        writableSheet.addCell(label);

        //Cab Details ====================================================================
        label = new Label(5, 0, "Cab Details", cellFormat);
        writableSheet.addCell(label);

        label = new Label(5, 1, "Location", cellFormat);
        writableSheet.addCell(label);
        label = new Label(7, 1, "Timings", cellFormat);
        writableSheet.addCell(label);

        label = new Label(5, 2, "Pickup", cellFormat);
        writableSheet.addCell(label);
        label = new Label(6, 2, "Drop", cellFormat);
        writableSheet.addCell(label);
        label = new Label(7, 2, "Pickup", cellFormat);
        writableSheet.addCell(label);
        label = new Label(8, 2, "Return", cellFormat);
        writableSheet.addCell(label);
    }

    private void createContent(WritableSheet writableSheet) throws WriteException {
        WritableFont contentFont = new WritableFont(WritableFont.ARIAL, 10);
        // Define the cell format
        cellFormat = new WritableCellFormat(contentFont);
        cellFormat.setAlignment(Alignment.LEFT);
        Label label;
        int row = 3;

        for(Faculty faculty : this.faculties) {
            label = new Label(0, row, faculty.getFirstName(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(1, row, faculty.getEmail(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(2, row, faculty.getPhNo(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(3, row, faculty.getSessionStart().toString(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(4, row, faculty.getSessionEnd().toString(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(5, row, faculty.getPickupLocation(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(6, row, faculty.getDropLocation(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(7, row, faculty.getPickupTime().toString(), cellFormat);
            writableSheet.addCell(label);

            label = new Label(8, row, faculty.getReturnTime().toString(), cellFormat);
            writableSheet.addCell(label);
            row++;
        }
    }

    private void formatHeadingCells(WritableSheet writableSheet) throws WriteException {
        //Merge rows
        writableSheet.mergeCells(0, 0, 0, 2);
        writableSheet.mergeCells(1, 0, 1, 2);
        writableSheet.mergeCells(2, 0, 2, 2);

        //Merge rows & columns
        writableSheet.mergeCells(3, 0, 4, 1);


        //Merge Columns
        writableSheet.mergeCells(5, 0, 8, 0);
        writableSheet.mergeCells(5, 1, 6, 1);
        writableSheet.mergeCells(7, 1, 8, 1);
    }
}
