package org.databio.pepjava;

import java.io.FileReader;
import java.util.*;

public class SampleTable {

    private class CSVTable {
        private FileReader fr;
        private List<String> columnNames = new ArrayList<String>();
        private Map<String, List<String>> rows = new HashMap<>();

        // this constructor will read everything in from the file
        public CSVTable(FileReader fr) {
            this.fr = fr;
            try {
                List<String[]> allRows = new com.opencsv.CSVReader(fr).readAll();
                // get the header of the file (names of columns)
                // could not use Arrays.asList because it creates a list that
                // does not support modification ops like "remove"
                for (String cName : allRows.get(0))
                    columnNames.add(cName);
                //columnNames = Arrays.asList(allRows.get(0));
                // remember the column names and create initial lists to keep row data
                for (String column : columnNames) {
                    rows.put(column, new ArrayList<>());
                }
                // add all the rows of the CSV file to object memory
                // if a certain row is missing a column, we will put null
                for (String[] row: allRows) {
                    if (columnNames.size() == row.length) {
                        for (int i=0; i<columnNames.size(); i++) {
                            rows.get(columnNames.get(i)).add(row[i]);
                        }
                    } else {
                        System.out.println("Row has length " + row.length + " while there are exactly " + columnNames.size() + " columns. Skipping");
                        System.out.println(Arrays.asList(row).toString());
                    }
                }
            } catch (java.io.IOException ioe) {
                System.out.println("Problem reading " + csvFileName + "." + ioe.getMessage());
            } catch (com.opencsv.exceptions.CsvException csve) {
                System.out.println("CSV file exception! " + csve.getMessage());
            }
        }

        public List<String> getColumnNames() {
            return columnNames;
        }

        public void setColumnNames(List<String> columnNames) {
            this.columnNames = columnNames;
        }

        public Map<String, List<String>> getRows() {
            return rows;
        }

        public void setRows(Map<String, List<String>> newRows) {
            rows = newRows;
        }
    }

    private String csvFileName;
    private FileReader csvFile;
    private CSVTable csvTable;

    public SampleTable(String fname) {
        csvFileName = fname;
        try {
            csvFile = new FileReader(csvFileName);
            csvTable = this.new CSVTable(csvFile);
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("File " + fname + " not found!");
        }
    }

    public String getSampleTableFileName() {
        return csvFileName;
    }

    public List<String> getSampleTableHeaders() {
        if (csvTable != null)
            return csvTable.getColumnNames();
        else
            return null;
    }

    public Map<String, List<String>> getSampleTableRows() {
        return csvTable.getRows();
    }

    // sample modifiers remove attributes
    public boolean processRemove(List<String> attributes) {
        for (String attr: attributes) {
            if (csvTable != null && csvTable.columnNames.contains(attr)) {
                // remove from rows as well
                csvTable.rows.remove(attr);
                csvTable.columnNames.removeIf((String x) -> x.equals(attr));
            } else {
                // FIXME: Better error communication
                System.out.println("Attribute " + attr + " from remove section does not exist in sample table");
                return false;
            }
        }
        return true;
    }

    // sample modifiers append section
    public void processAppend(Map<String,String> attrVal) {
        var ntimes = this.csvTable.rows.size();
        attrVal.forEach((k,v) -> {
            this.csvTable.columnNames.add(k);
            this.csvTable.rows.put(k,Collections.nCopies(ntimes, v));
        });
    }
}