package org.databio.pepjava;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    //private FileReader csvFile;
    private CSVTable csvTable;

    public SampleTable(String fname) {
        csvFileName = fname;
        csvTable = readCSVFile(fname);
    }

    private CSVTable readCSVFile(String fname) {
        try {
            FileReader csvFile = new FileReader(fname);
            return this.new CSVTable(csvFile);
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("File " + fname + " not found!");
            return null;
        }
    }

    // this method implements the "amend" capability of the PEP spec
    private void mergeTables(CSVTable other) {
        // merge two CSV tables into one by:
        // keeping all the columns of this table not overriden by the other table
        // overriding existing columns of this table from the other table
        // adding the columns from other table not in this table
        Set<String> sHeadersThis = new HashSet<>(this.csvTable.columnNames);
        Set<String> sHeadersOther = new HashSet<>(other.columnNames);
        Set<String> sHeadersKeep = new HashSet<>(sHeadersThis);
        sHeadersKeep.removeIf(sHeadersOther::contains); // columns to keep
        // now get the columns to override
        Set<String> sHeadersOverride = new HashSet<>(sHeadersThis);
        sHeadersOverride.stream().filter(sHeadersOther::contains); // columns to override
        // find the columns to add
        Set<String> sHeadersAppend = new HashSet<>(sHeadersOther);
        sHeadersAppend.removeIf(sHeadersThis::contains); // columns to append
        // now do the job at hand by modifying in place
        Set<String> sHeadersFinal = new HashSet<>(sHeadersKeep);
        sHeadersFinal.addAll(sHeadersAppend);
        sHeadersFinal.addAll(sHeadersAppend);
        // final list of column names
        this.csvTable.columnNames = new ArrayList<>(sHeadersFinal);
        // now take care of the actual data rows/columns
        Map<String, List<String>> rowsFinal = new HashMap<>();
        sHeadersKeep.forEach(h -> {
            rowsFinal.put(h, this.csvTable.rows.get(h));
        });
        sHeadersOverride.forEach(h -> {
            rowsFinal.put(h, other.rows.get(h));
        });
        sHeadersAppend.forEach(h -> {
            rowsFinal.put(h, other.rows.get(h));
        });
        // final rows set here
        this.csvTable.rows = rowsFinal;
        return;
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

    // removes (a list of) columns from the table of samples
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

    // appends a column to the table of samples
    // where each row in the column has the same value
    public void processAppend(Map<String,String> attrVal) {
        var ntimes = this.csvTable.rows.size();
        attrVal.forEach((k,v) -> {
            this.csvTable.columnNames.add(k);
            this.csvTable.rows.put(k,Collections.nCopies(ntimes, v));
        });
    }

    // duplicates a column in the table of samples
    public void processDuplicate(Map<String,String> d) {
        // duplicates key of d map into the value, copies the list with new attribute
        d.forEach((k,v) -> {
            this.csvTable.columnNames.add(v);
            this.csvTable.rows.put(v, List.copyOf(this.csvTable.rows.get(k)));
        });
    }

    // process the if->then section
    // in a later iteration we will probably signal errors better
    public void processImply(List<IfThen> ifthenList) {
        // the _if part of the if->then section can be a list of it can be a single string
        ifthenList.forEach(ifthen -> {
            Map<String,Object> _if = ifthen.getIf();
            Map<String,String> _then = ifthen.getThen();
            // does the implied (then:) column exist in the sample?
            _then.forEach((k,v) -> {
                if (!this.csvTable.columnNames.contains(k)) {
                    // no, add a column of null values for now
                    processAppend(Map.of(k,""));
                }
            });
            // iterate through the existing column with key k (if: section)
            // and see what values need to be changed in new column
            // when k matches whatever values were in if: section of PEP spec
            String ifkey = (String)(_if.keySet().toArray())[0];
            Object ifVal = _if.get(ifkey);
            // get the attribute column and go through it row by row
            // e.g. we get the "genome" column and see if
            // any of the rows match what PEP file has in if: section
            // e.g. ["hg38", "hg19"]
            List<String> column = this.csvTable.rows.get(ifkey);
            // create empty lists for all _then columns
            // we will use these to build up the new columns
            Map<String,List<String>> thenCols = new HashMap<>();
            _then.forEach((k,v) -> {
                thenCols.put(k, new ArrayList<>());
            });
            // now run through the if columns and try and modify the then columns
            for (int i=0; i<column.size(); i++) {
                String row = column.get(i);
                if (ifVal instanceof List<?>) {
                    if (((List<String>) ((List<?>) ifVal)).contains(row)) {
                        _then.forEach((k, v) -> {
                            thenCols.get(k).add(v);
                        });
                    }
                } else if (row.equals((String)ifVal)){
                    // fix up the new _then columns added above
                    _then.forEach((k, v) -> {
                        thenCols.get(k).add(v);
                    });
                } else
                    thenCols.forEach((k,v) -> { v.add("");});
            }
            // now replace the new then columns with modified ones
            thenCols.forEach((k,v) -> { this.csvTable.rows.put(k,v); });
        });
    }

    // FIXME: improve error reporting
    // FIXME: implement wildcards and $HOME support
    public void processDerive(Derive derive) {
        List<String> attr = derive.getAttributes();
        Map<String, String> sources = derive.getSources();
        // check if we have the attributes in the columns
        for (String a : attr) {
            if (!this.csvTable.columnNames.contains(a)) {
                System.out.println("derive(): Attribute " + a + " does not exist in sample file. Aborting.");
                return;
            }
        }
        //attr.forEach(a -> System.out.println("attr=" + a));
        //sources.forEach((k, v) -> System.out.println("k=" + k + ",v=" + v));
        // fully expand sources
        // colName is a String
        // row is a Map<String,List<String>>
        for (String key : this.csvTable.rows.keySet()) {
            if (attr.contains(key)) {
                List<String> rows = this.csvTable.rows.get(key);
                for (int cnt = 0; cnt < rows.size(); cnt++) {
                    String row = rows.get(cnt);
                    if (sources.containsKey(row)) {
                        //System.out.println("Row entry for " + key + " equals one of sources:" + sources.keySet());
                        String val = sources.get(row);
                        //System.out.println("Value is: " + val);
                        List<String> pl = Pattern.compile("/")
                                .splitAsStream(val)
                                .collect(Collectors.toList());
                        String finalPath = "";
                        for (String p : pl) {
                            if (p.startsWith("{") && p.endsWith("}")) {
                                String coln = p.substring(1, p.length() - 1);
                                //System.out.println("coln=" + coln);
                                // get the value of the referred field
                                String referred = this.csvTable.rows.get(coln).get(cnt);
                                //System.out.println("Referred value @" + coln + " is " + referred);
                                finalPath += referred;
                            } else
                                finalPath += p + "/";
                        }
                        //System.out.println("Fully expanded final path=" + finalPath);
                        rows.set(cnt, finalPath);
                    }
                }
            }
        }
        // check to make sure the change has stuck
        /*this.csvTable.rows.forEach((k,v) -> {
            System.out.println("k=" + k + ", v=" + v);
        });*/
    }

    // process the amend portion of the PEP spec
    // anything can be replaced with anything else
    public void processAmend(Map<String,String> amendMap, String choiceAmendment) {
        // FIXME: Better error handling!
        if (!amendMap.containsKey(choiceAmendment)) {
            System.out.println("Amendment passed on command line does not match what is in PEP file.");
            System.exit(-1);
        }
        if (amendMap.containsKey("sample_table")) {
            // replace the current sample table with a new one
            this.csvTable = readCSVFile(amendMap.get("sample_table"));
        }
        for (String attr : amendMap.keySet()) {
            if (getSampleTableHeaders().equals(attr))
                // attribute already exists, we are replacing it
                processRemove(Collections.singletonList(attr));
            processAppend(Collections.singletonMap(attr,amendMap.get(attr)));
        }
    }

}