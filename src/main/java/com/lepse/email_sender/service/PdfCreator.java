package com.lepse.email_sender.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lepse.email_sender.models.Employee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PdfCreator {

    public enum Headers {
        fired_date("Дата увольнения"),
        uid("UID"),
        id("ID"),
        name("ФИО"),
        group("Полная группа"),
        last_login("Последний вход"),
        status("Статус"),
        is_out("Вне офиса");

        private final String header;

        Headers(String header) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }

        public static String[] getHeaders() {
            Headers[] headers = values();
            String[] values = new String[headers.length];

            for (int i = 0; i < headers.length; i++) {
                values[i] = headers[i].getHeader();
            }
            return values;
        }
    }
    public enum Subheaders {
        uid("UID"),
        id("ID"),
        name("ФИО"),
        start_end_date("Срок назначения");


        private final String header;
        Subheaders(String header) {
            this.header = header;
        }

        public String getSubheader() {
            return header;
        }

        public static String[] getSubheaders() {
            Subheaders[] subheaders = values();
            String[] values = new String[subheaders.length];

            for (int i = 0; i < subheaders.length; i++) {
                values[i] = subheaders[i].getSubheader();
            }
            return values;
        }
    }

    private static final String MAIN_HEADER = "Список уволившихся активных пользователей ";
    private static final String FONT_BOLD = "JetBrainsMonoNL-ExtraBold.ttf";
    private static final String FONT_MEDIUM = "JetBrainsMonoNL-Medium.ttf";
    private List<Employee> employeeList;
    private Font fontBold;
    private Font font;
    private Font fontHeader;
    private final String path;

    @Getter
    @Setter
    private Date date;

    /**
     * A class for generating a report in .pdf format
     * @param path Path to save the file
     * */
    @Autowired
    public PdfCreator(String path) {
        this.path = path;
    }

    /**
     * Report generation in .pdf format
     * @param employeeList List of employees
     * @return Path to file
     */
    public String createPdf(List<Employee> employeeList) {
        this.employeeList = employeeList;
        registryFonts();

        File file = new File(path);
        try {
            Document document = new Document(PageSize.A4.rotate(), -50f, -50f, 30f, 30f);
            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();

            document.add(addMainHeader());
            PdfPTable tableMain = new PdfPTable(12);
            tableMain.setWidths(new float[]{0.35f, 0.415f, 0.3f, 0.4f, 1, 0.5f, 0.2f, 0.17f, 0.415f, 0.3f, 0.4f, 0.5f});
            createHeaders(tableMain);
            addRows(tableMain);
            document.add(tableMain);

            document.close();

            return file.getPath();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * Creating a report signature
     * @return paragraph
     * */
    private Paragraph addMainHeader() throws DocumentException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Chunk chunk = new Chunk(PdfCreator.MAIN_HEADER + format.format(date) + "\n\n", fontHeader);
        Paragraph paragraph = new Paragraph();
        paragraph.add(chunk);
        paragraph.setAlignment(Element.ALIGN_CENTER);

        return paragraph;
    }

    /**
     * Creating and adding table headers
     * @param table A table for the record
     * */
    private void createHeaders(PdfPTable table) throws DocumentException {
        Arrays.stream(Headers.getHeaders())
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(String.valueOf(columnTitle), fontBold));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
        createSubheaders(table);
    }

    /**
     * Creating a new table to add headers for Surrogate
     * @param table A table for the record
     * */
    private void createSubheaders(PdfPTable table) throws DocumentException {
        PdfPTable subheaders = new PdfPTable(4);
        subheaders.setWidths(new float[] {0.415f, 0.3f, 0.4f, 0.5f});

        PdfPCell header = new PdfPCell(new Phrase("Новый получатель задач", fontBold));
        header.setColspan(4);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        subheaders.addCell(header);

        Arrays.stream(Subheaders.getSubheaders())
                .forEach(columnTitle -> {
                    PdfPCell subheader = new PdfPCell();
                    subheader.setPhrase(new Phrase(String.valueOf(columnTitle), fontBold));
                    subheader.setHorizontalAlignment(Element.ALIGN_CENTER);
                    subheaders.addCell(subheader);
                });

        PdfPCell lastCell = new PdfPCell(subheaders);
        lastCell.setColspan(4);
        table.addCell(lastCell);
        table.completeRow();
    }

    /**
     * Adding rows to a table
     * @param table A table for the record
     * */
    private void addRows(PdfPTable table) {
        for (Employee employee : employeeList) {
            table.addCell(new PdfPCell(new Phrase(employee.getFiredDate(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getUid(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getId(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getName(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getGroup().replaceAll("\\.", "\n"), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getLastLogin(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getStatus(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getIsOut(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getSurrogate().getUid(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getSurrogate().getUserId(), font)));
            table.addCell(new PdfPCell(new Phrase(employee.getSurrogate().getUserName(), font)));

            String startEndDate = employee.getSurrogate().getStartDate() + " -\n" +
                    employee.getSurrogate().getEndDate();
            if (startEndDate.equals(" -\n")) {
                table.addCell(new PdfPCell(new Phrase("", font)));
            } else {
                table.addCell(new PdfPCell(new Phrase(startEndDate, font)));
            }

            table.completeRow();
        }
    }

    /**
     * Adding a custom font
     * */
    private void registryFonts() {
        FontFactory.register(String.valueOf(getClass().getResource(FONT_BOLD)));
        FontFactory.register(String.valueOf(getClass().getResource(FONT_MEDIUM)));
        font = FontFactory.getFont(FONT_MEDIUM, "cp1251", BaseFont.EMBEDDED, 6);
        fontBold = FontFactory.getFont(FONT_BOLD, "cp1251", BaseFont.EMBEDDED, 6);
        fontHeader = FontFactory.getFont(FONT_BOLD, "cp1251", BaseFont.EMBEDDED, 10);
    }
}
