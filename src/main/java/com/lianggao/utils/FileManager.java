package com.lianggao.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.commons.codec.digest.Crypt;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import ws.schild.jave.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;


public class FileManager {

    /**
     * 转PDF格式值
     */
    static final int WORD_FORMAT_PDF = 17;
    static final int EXCEL_FORMAT_PDF = 0;
    static final int PPT_FORMAT_PDF = 32;

    /*** @Description:根据文件类型转换为pdf */
    public static void convert2PDF(String inputFile, String pdfFile) {
        String suffix = getFileSufix(inputFile);
        if (suffix.equals("doc") || suffix.equals("docx") || suffix.equals("txt")) {
            word2PDF(inputFile, pdfFile);
        } else if (suffix.equals("xls") || suffix.equals("xlsx")) {
            // xlsToPdf(inputFile, pdfFile);
            xlsToPdf(inputFile, pdfFile);
            //  excel2PDF(inputFile, inputFile.split("\\.")[0]+".htm;");
        } else if (suffix.equals("ppt") || suffix.equals("pptx")) {
            ppt2PDF(inputFile, pdfFile);
        } else {
            System.out.println("文件格式不支持转换!");
        }
    }

    //EXCEL转PDF
    public static String xlsToPdf(String inFilePath, String outFilePath) {

        ComThread.InitSTA(true);
        ActiveXComponent ax = new ActiveXComponent("Excel.Application");
        try {
            ax.setProperty("Visible", new Variant(false));
            ax.setProperty("AutomationSecurity", new Variant(3)); //禁用宏
            Dispatch excels = ax.getProperty("Workbooks").toDispatch();

            Dispatch excel = Dispatch.invoke(excels, "Open", Dispatch.Method, new Object[]{
                            inFilePath,
                            new Variant(false),
                            new Variant(false)
                    },
                    new int[9]).toDispatch();
            //转换格式
            Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, new Object[]{
                    new Variant(0), //PDF格式=0
                    outFilePath,
                    new Variant(0)  //0=标准 (生成的PDF图片不会变模糊) 1=最小文件 (生成的PDF图片糊的一塌糊涂)
            }, new int[1]);

            //这里放弃使用SaveAs
		/*Dispatch.invoke(excel,"SaveAs",Dispatch.Method,new Object[]{
			outFile,
			new Variant(57),
			new Variant(false),
			new Variant(57),
			new Variant(57),
			new Variant(false),
			new Variant(true),
			new Variant(57),
			new Variant(true),
			new Variant(true),
			new Variant(true)
		},new int[1]);*/

            Dispatch.call(excel, "Close", new Variant(false));

            if (ax != null) {
                ax.invoke("Quit", new Variant[]{});
                ax = null;
            }
            ComThread.Release();
            return "";
        } catch (Exception es) {
            return es.toString();
        }
    }

    /**
     * @Description:word转pdf
     */
    private static void word2PDF(String inputFile, String pdfFile) {
        System.out.println("启动Word...");
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            // 创建一个word对象
            app = new ActiveXComponent("Word.Application");
            // 不可见打开word （但默认是不可见的）
            app.setProperty("Visible", new Variant(false));
            // 获取文挡属性
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.call(docs, "Open", inputFile).toDispatch();
            System.out.println("打开文档..." + inputFile);
            System.out.println("转换文档到PDF..." + pdfFile);
            File tofile = new File(pdfFile);
            if (tofile.exists()) {
                tofile.delete();
            }
            // word保存为pdf格式宏，值为17
            Dispatch.call(doc, "SaveAs", pdfFile, WORD_FORMAT_PDF);
            long end = System.currentTimeMillis();
            System.out.println("转换完成..用时：" + (end - start) + "ms.");
        } catch (Exception e) {
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(doc, "Close", false);
            System.out.println("关闭文档");
            if (app != null) {
                app.invoke("Quit", new Variant[]{});
            }

        }
        //如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
    }

    /**
     * @Description:excel转pdf
     */
    public static void excel2PDF(String inputFile, String pdfFile) {

        ActiveXComponent app = null;
        Dispatch excel = null;
        app = new ActiveXComponent("Excel.Application"); // 启动Excel
        try {
            app.setProperty("Visible", new Variant(false));
            Dispatch excels = app.getProperty("Workbooks").toDispatch();
            excel = Dispatch.invoke(
                    excels,
                    "Open",
                    Dispatch.Method,
                    new Object[]{inputFile, new Variant(false), new Variant(true)},
                    new int[1]).toDispatch();
//              Dispatch sheet = Dispatch.invoke(excel, "sheet(0)", arg2, arg3, arg4)
            Dispatch.invoke(excel, "SaveAs", Dispatch.Method, new Object[]{
                    pdfFile, new Variant(44)}, new int[1]);
            Variant f = new Variant(false);
            Dispatch.call(excel, "Close", f);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            app.invoke("Quit", new Variant[]{});
        }
        //如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
    }

    /*** @Description:ppt转pdf  */
    private static void ppt2PDF(String inputFile, String pdfFile) {
        System.out.println("启动PPT...");
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch ppt = null;
        try {
            // 创建一个ppt对象
            app = new ActiveXComponent("PowerPoint.Application");
            // 不可见打开（PPT转换不运行隐藏，所以这里要注释掉）
            // app.setProperty("Visible", new Variant(false));
            // 获取文挡属性
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            ppt = Dispatch.call(ppts, "Open", inputFile, true, true, false).toDispatch();
            System.out.println("打开文档..." + inputFile);
            System.out.println("转换文档到PDF..." + pdfFile);
            File tofile = new File(pdfFile);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(ppt, "SaveAs", pdfFile, PPT_FORMAT_PDF);
            long end = System.currentTimeMillis();
            System.out.println("转换完成..用时：" + (end - start) + "ms.");
        } catch (Exception e) {
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(ppt, "Close");
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[]{});
        }
        //如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
    }

    /*** @Description:获取文件后缀*/
    private static String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }


   /* private static boolean getLicense(String fileType) {
        boolean result = false;
        try {

            InputStream is = FileManager.class.getClassLoader().getResourceAsStream("License.xml"); // license.xml应放在..\WebRoot\WEB-INF\classes路径下
            if (fileType.equals("ppt")) {
                com.aspose.slides.License aposeLic = new com.aspose.slides.License();
                aposeLic.setLicense(is);
            } else if (fileType.equals("doc")) {
                com.aspose.words.License aposeLic = new com.aspose.words.License();
                aposeLic.setLicense(is);
            } else if (fileType.equals("xls")) {
                com.aspose.cells.License aposeLic = new com.aspose.cells.License();
                aposeLic.setLicense(is);
            }

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/

    /*public static void Office2Pdf(String fileName, String pdfPath) {
        try {
            FileOutputStream out = new FileOutputStream(new File(pdfPath));

            String lowerFileName = fileName.toLowerCase();
            if ((lowerFileName.endsWith(".xls")) || (lowerFileName.endsWith(".xlsx")) || (lowerFileName.endsWith(".csv"))) {
                if (!getLicense("xls")) { // 验证License 若不验证则转化出的pdf文档会有水印产生
                    return;
                }
                Workbook workbook = new Workbook(lowerFileName);
                workbook.save(out, 13);//13->SaveFormat.PDF 不同文件类型对应的SaveFormat.PDF不同
            } else if ((lowerFileName.endsWith(".doc")) || (lowerFileName.endsWith(".docx")) || (lowerFileName.endsWith(".rtf"))) {
                if (!getLicense("doc")) {
                    return;
                }
                Document doc = new Document(lowerFileName);
                doc.save(out, 40);
            } else if ((lowerFileName.endsWith(".ppt")) || (lowerFileName.endsWith(".pptx")) || (lowerFileName.endsWith(".pps")) || (lowerFileName.endsWith(".ppsx"))) {
                if (!getLicense("ppt")) {
                    return;
                }
                Presentation ppt = new Presentation(lowerFileName);
                ppt.save(out, 1);
            } else if ((lowerFileName.endsWith(".vdx")) || (lowerFileName.endsWith(".vsdx")) || (lowerFileName.endsWith(".vsx")) || (lowerFileName.endsWith(".vtx")) || (lowerFileName.endsWith(".vsd"))) {
                Diagram visio = new Diagram(lowerFileName);
                visio.save(out, 8);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }*/

    /**
     * 任意音频转MP3
     * <p>
     * 第一个参数source表示要解码的源文件。
     * <p>
     * 第二个参数target是将要创建和编码的目标文件。
     * <p>
     * <p>
     * 请注意，此方法是阻塞的：只有在转码操作完成（或失败）后，该方法才会返回
     */

    public static void ConvertingAnyAudioToMp3(String localFilePath, String targetPath) {

        //  ConvertProgressListener listener = new ConvertProgressListener();
        File source = new File(localFilePath);
        File target = new File(targetPath);

        try {
            /*
             * 自定义 TODO
             *
             */
//                  File source = new File("file path source");
            /*
             * 自定义 TODO
             *
             */
//                  File target = new File("file path target");

            // Audio Attributes/音频编码属性
            AudioAttributes audio = new AudioAttributes();
            /*
             * 它设置将用于音频流转码的编解码器的名称。您必须从当前Encoder实例的getAudioEncoders（）方法返回的列表中选择一个值。否则，
             * 您可以传递AudioAttributes.DIRECT_STREAM_COPY特殊值，该值需要源文件中原始音频流的副本。
             */
            audio.setCodec("libmp3lame");
            /*
             * 它设置新重新编码的音频流的比特率值。如果未设置比特率值，编码器将选择默认值。该值应以每秒位数表示。例如，如果你想要128 kb /
             * s比特率，你应该调用setBitRate（new Integer（128000））。
             */
            audio.setBitRate(128000);
            /* 它设置将在重新编码的音频流中使用的音频通道的数量（1 =单声道，2 =立体声）。如果未设置通道值，编码器将选择默认值。 */
            audio.setChannels(2);
            /*
             * 它设置新重新编码的音频流的采样率。如果未设置采样率值，编码器将选择默认值。该值应以赫兹表示。例如，如果您想要类似CD的44100
             * Hz采样率，则应调用setSamplingRate（new Integer（44100））。
             */
            audio.setSamplingRate(44100);
            /* 可以调用此方法来改变音频流的音量。值256表示没有音量变化。因此，小于256的值是音量减小，而大于256的值将增加音频流的音量。 */
            audio.setVolume(new Integer(256));

            // Encoding attributes/编码属性
            EncodingAttributes attrs = new EncodingAttributes();
            /*
             * 它设置将用于新编码文件的流容器的格式。给定参数表示格式名称。
             * 编码格式名称有效且仅在它出现在正在使用的Encoder实例的getSupportedEncodingFormats（）方法返回的列表中时才受支持。
             */
            attrs.setFormat("mp3");
            /* 它设置音频编码属性。如果从未调用过新的EncodingAttributes实例，或者给定参数为null，则编码文件中不会包含任何音频流 */
            attrs.setAudioAttributes(audio);
            /*
             * 它为转码操作设置偏移量。源文件将从其开始的偏移秒开始重新编码。例如，如果您想剪切源文件的前五秒，
             * 则应在传递给编码器的EncodingAttributes对象上调用setOffset（5）。
             */
            // attrs.setOffset(5F);
            /*
             * 它设置转码操作的持续时间。只有源的持续时间秒才会在目标文件中重新编码。例如，如果您想从源中提取和转码30秒的一部分，
             * 则应在传递给编码器的EncodingAttributes对象上调用setDuration（30）
             */
            // attrs.setDuration(30F);

            // Encode/编码
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(source), target, attrs);

        } catch (Exception ex) {
            ex.printStackTrace();
            //   succeeded = false;
        }
        // return succeeded;
    }

    public static void ConvertingVideoToMp4(String localFilePath, String targetPath) throws EncoderException {
        File source = new File(localFilePath);
        File target = new File(targetPath);
        try {


            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(new Integer(56000));
            audio.setChannels(new Integer(1));
            audio.setSamplingRate(new Integer(22050));
            VideoAttributes video = new VideoAttributes();
            video.setCodec("libx264");
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp4");  //h264编码
            attrs.setAudioAttributes(audio);
            attrs.setVideoAttributes(video);
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(source), target, attrs);
        } catch (Exception ex) {
            ex.printStackTrace();
            //   succeeded = false;
        }
    }


    public static void downloadFile(HttpServletResponse response, String path, String fileName) throws Exception {

        Crypt crypt = new Crypt();
        String temp = fileName;
        String type = "." + temp.split("\\.")[temp.split("\\.").length - 1];
        temp = temp.replace(type, "");
        //temp = crypt.DESEncrypt(temp, "shirleyL");
        File file = new File(path + "\\" + temp + type);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "ISO-8859-1"));
            //打开本地文件流
            InputStream inputStream = new FileInputStream(path + "\\" + temp + type);
            //激活下载操作
            OutputStream os = response.getOutputStream();
            //循环写入输出流
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            // 这里主要关闭。
            os.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int deleteFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }
    public static int deleteFile(String path, String filname) {
        File file = new File(path, filname);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }

    public static void Txt2Pdf(String text, String pdf) throws DocumentException, IOException {
        File docfile = new File(text);
        File pdffile = new File(pdf);
        if (!docfile.exists() || pdffile.exists()) {
            System.out.println("error");
            return;
        }
        //  BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED)
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        // BaseFont bfChinese =
        // BaseFont.createFont("C://Windows//Fonts//simsunb.ttf,1",BaseFont.IDENTITY_H,
        // BaseFont.EMBEDDED);
        Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
        FileOutputStream out = new FileOutputStream(pdf);
        Rectangle rect = new Rectangle(PageSize.A4.rotate());
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(rect);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();
        Paragraph p = new Paragraph();
        p.setFont(FontChinese);

        InputStreamReader isr = new InputStreamReader(new FileInputStream(text), "GB2312");
        BufferedReader read = new BufferedReader(isr);
        // BufferedReader read = new BufferedReader(new FileReader(text));

        String line = read.readLine();
        while (line != null) {
            System.out.println(line);
            p.add(line + "\n");
            line = read.readLine();
        }
        read.close();
        doc.add(p);

        doc.close();

    }

    public static void Xls2Pdf_Office(String inFilePath, String outFilePath) throws Exception {


        File docfile = new File(inFilePath);
        File pdffile = new File(outFilePath);
        if (!docfile.exists() || pdffile.exists()) {
            System.out.println("error");
            return;
        }
        ActiveXComponent ax = null;
        Dispatch excel = null;
        try {
            ComThread.InitSTA();
            ax = new ActiveXComponent("Excel.Application");
            ax.setProperty("Visible", new Variant(false));
            ax.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch excels = ax.getProperty("Workbooks").toDispatch();
            Object[] obj = new Object[]{inFilePath, new Variant(false), new Variant(false)};
            excel = Dispatch.invoke(excels, "Open", Dispatch.Method, obj, new int[9]).toDispatch();
            File tofile = new File(outFilePath);
            // System.err.println(getDocPageSize(new File(sfileName)));
            if (tofile.exists()) {
                tofile.delete();
            }
            // 转换格式
            Object[] obj2 = new Object[]{new Variant(0), // PDF格式=0
                    outFilePath, new Variant(0) // 0=标准 (生成的PDF图片不会变模糊) ; 1=最小文件
            };
            Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, obj2, new int[1]);
            System.out.println("转换完毕！");
        } catch (Exception es) {
            es.printStackTrace();
            throw es;
        } finally {
            if (excel != null) {
                Dispatch.call(excel, "Close", new Variant(false));
            }
            if (ax != null) {
                ax.invoke("Quit", new Variant[]{});
                ax = null;
            }
            ComThread.Release();
        }
    }


    public static void PPT2Pdf_Office(String inFilePath, String outFilePath) throws Exception {
        File docfile = new File(inFilePath);
        File pdffile = new File(outFilePath);
        if (!docfile.exists() || pdffile.exists()) {
            System.out.println("error");
            return;
        }
        try {
            ComThread.InitSTA(true);
            ActiveXComponent app = new ActiveXComponent("PowerPoint.Application");
            // app.setProperty("Visible", false);
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            Dispatch ppt = Dispatch.call(ppts, "Open", inFilePath, true, false).toDispatch();
            File tofile = new File(outFilePath);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.invoke(ppt, "SaveAs", Dispatch.Method, new Object[]{outFilePath, new Variant(32)}, new int[1]);
            Dispatch.call(ppt, "Close");
            app.invoke("Quit");
            //ComThread.Release();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            ComThread.Release();
//			Process process;
//			int pid = 0;
//			try {
//				process = Runtime.getRuntime().exec("tasklist");
//				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//				String str = "";
//		        while((str = br.readLine()) != null) {
//		        	if (str.contains("POWERPNT.EXE")) {
//						StringBuffer buf = new StringBuffer();
//						for (int i = 0; i < str.length(); i++) {
//							char ch = str.charAt(i);
//							if (ch != ' ') {
//								buf.append(ch);
//							}
//
//						}
//						pid = Integer.parseInt(buf.toString().split("Console")[0].substring("POWERPNT.EXE".length()));
//						Runtime.getRuntime().exec("tskill" + " " + pid);
//					}
//		        	br.close();
//		        }
//		      //Scanner in = new Scanner(process.getInputStream());
////				while (in.hasNextLine()) {
////					String p = in.nextLine();
////					if (p.contains("POWERPNT.EXE")) {
////						StringBuffer buf = new StringBuffer();
////						for (int i = 0; i < p.length(); i++) {
////							char ch = p.charAt(i);
////							if (ch != ' ') {
////								buf.append(ch);
////							}
////
////						}
////						//System.out.println(buf.toString().split("Console")[0].substring("POWERPNT.EXE".length()));
////						pid = Integer.parseInt(buf.toString().split("Console")[0].substring("POWERPNT.EXE".length()));
////						Runtime.getRuntime().exec("tskill" + " " + pid);
////					}
////					in.close();
////				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

        }
    }


    public static void Doc2Pdf_Office(String inFilePath, String outFilePath) throws Exception {

        File docfile = new File(inFilePath);
        File pdffile = new File(outFilePath);
        if (!docfile.exists() || pdffile.exists()) {
            System.out.println("error");
            return;
        }
        ComThread.InitSTA();
        ActiveXComponent actcom = new ActiveXComponent("Word.Application");
        Dispatch documents = null;
        Dispatch document = null;

        try {
            actcom.setProperty("Visible", new Variant(false));

            documents = actcom.getProperty("Documents").toDispatch();
            document = Dispatch.invoke(documents, "Open", Dispatch.Method, new Object[]{inFilePath, new Variant(false), new Variant(false)}, new int[1]).toDispatch();

            Dispatch.invoke(document, "SaveAs", Dispatch.Method, new Object[]{outFilePath, new Variant(17)}, new int[1]);

            Dispatch.call(document, "Close", new Object[]{new Variant(false)});
			/*if (actcom != null) {
				actcom.invoke("Quit", new Variant[] {});
				actcom = null;
			}
			ComThread.Release();*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (actcom != null) {
                actcom.invoke("Quit", new Variant[]{});
                actcom = null;
            }
            ComThread.Release();
        }
    }

    public static void Print(String path) throws Exception {
        String PRINT_NAME = "";// 打印机名称
        // 初始化COM线程
        ComThread.InitSTA();
        ActiveXComponent doc = new ActiveXComponent("Word.Application");
        try {

            // 设置是否显示打开word
            Dispatch.put(doc, "Visible", new Variant(true));
            // 打开具体的工作簿
            Dispatch documents = doc.getProperty("Documents").toDispatch();
            Dispatch word = Dispatch.call(documents, "Open", path).toDispatch();

            // 设置打印属性并打印
            Dispatch.callN(word, "PrintOut", new Object[]{Variant.VT_MISSING, Variant.VT_MISSING, new Integer(1), new Boolean(false), PRINT_NAME, new Boolean(true), Variant.VT_MISSING, ""});

            // 关闭文档
            Dispatch.call(word, "Close", new Variant(false));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            doc.invoke("Quit", new Variant[0]);
            // 始终释放资源
            ComThread.Release();
        }
    }


    public static void ExportToExcel(String fileName, String title, String
            secondRow, Map<String, String> columnHead, List<Map<String, Object>> dataList, HttpServletResponse response) throws Exception {
        int collumnNum = columnHead.size();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        sheet.setDefaultColumnStyle(collumnNum, style);

        HSSFRow row0 = sheet.createRow(0);
        for (int i = 0; i < columnHead.size(); i++) {
            row0.createCell(i);
        }
        row0.getCell(0).setCellValue(title);

        HSSFRow row1 = sheet.createRow(1);
        for (int i = 0; i < columnHead.size(); i++) {
            row1.createCell(i);
        }
        row1.getCell(0).setCellValue(secondRow);

        CellRangeAddress title_cell = new CellRangeAddress(0, 0, 0, collumnNum - 1);
        CellRangeAddress conditions_cell = new CellRangeAddress(1, 1, 0, collumnNum - 1);// 起始行,结束行,起始列,结束列
        sheet.addMergedRegion(title_cell);
        sheet.addMergedRegion(conditions_cell);

        // 表头行
        int headRow = sheet.getLastRowNum() + 1;
        HSSFRow row_head = sheet.createRow(headRow);
        int k = 0;
        for (Map.Entry<String, String> entry : columnHead.entrySet()) {
            HSSFCell cell = row_head.createCell(k);
            cell.setCellValue(entry.getKey());
            k++;
        }

        // 数据行
        int startRow = headRow + 1;
        for (int i = startRow; i < startRow + dataList.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            int j = 0;
            for (Map.Entry<String, String> entry : columnHead.entrySet()) {
                HSSFCell cell = row.createCell(j);
                if (entry.getValue().equals("rn")) {
                    cell.setCellValue(Integer.toString(i - startRow + 1));
                } else {
                    try {
                        cell.setCellValue(dataList.get(i - startRow).get(entry.getValue()).toString());
                    } catch (NullPointerException e) {
                        // TODO: handle exception
                        cell.setCellValue("");
                    }

                }
                j++;

            }

        }
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
        sheet.setColumnWidth(0, 2000);

        HSSFCellStyle titlestyle = wb.createCellStyle();
        titlestyle.setBorderLeft(BorderStyle.THIN);
        titlestyle.setBorderRight(BorderStyle.THIN);
        titlestyle.setBorderTop(BorderStyle.THIN);
        titlestyle.setBorderBottom(BorderStyle.THIN);
        titlestyle.setWrapText(true);
        titlestyle.setAlignment(CENTER);
        titlestyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont titlefont = wb.createFont();
        titlefont.setFontName("宋体");
        titlefont.setFontHeightInPoints((short) 16);
        titlefont.setBold(true);
        titlestyle.setFont(titlefont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(0).getCell(i).setCellStyle(titlestyle);
        }
        sheet.getRow(0).setHeightInPoints((short) 16);

        HSSFCellStyle headstyle = wb.createCellStyle();
        headstyle.cloneStyleFrom(titlestyle);
        HSSFFont headfont = wb.createFont();
        headfont.setFontName("宋体");
        headfont.setFontHeightInPoints((short) 12);
        headfont.setBold(true);
        headstyle.setFont(headfont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(2).getCell(i).setCellStyle(headstyle);
        }

        HSSFCellStyle datastyle = wb.createCellStyle();
        datastyle.cloneStyleFrom(titlestyle);
        HSSFFont datafont = wb.createFont();
        datafont.setFontName("宋体");
        datafont.setFontHeightInPoints((short) 12);
        datastyle.setFont(datafont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(1).getCell(i).setCellStyle(datastyle);
        }

        for (int i = 3; i < 3 + dataList.size(); i++) {
            for (int j = 0; j < columnHead.size(); j++) {
                sheet.getRow(i).getCell(j).setCellStyle(datastyle);
            }

        }

        String filename = fileName + ".xls";
        response.setContentType("application/ms-excel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(filename, "UTF-8"))));
        OutputStream out = response.getOutputStream();
        try {
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            wb.close();
        }

    }

    public static void ExportToExcel(String fileName, String title, String
            secondRow, Map<String, String> columnHead, List<Map<String, Object>> dataList, SimpleDateFormat formatter, HttpServletResponse response) throws Exception {
        int collumnNum = columnHead.size();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        sheet.setDefaultColumnStyle(collumnNum, style);

        HSSFRow row0 = sheet.createRow(0);
        for (int i = 0; i < columnHead.size(); i++) {
            row0.createCell(i);
        }
        row0.getCell(0).setCellValue(title);

        HSSFRow row1 = sheet.createRow(1);
        for (int i = 0; i < columnHead.size(); i++) {
            row1.createCell(i);
        }
        row1.getCell(0).setCellValue(secondRow);

        CellRangeAddress title_cell = new CellRangeAddress(0, 0, 0, collumnNum - 1);
        CellRangeAddress conditions_cell = new CellRangeAddress(1, 1, 0, collumnNum - 1);// 起始行,结束行,起始列,结束列
        sheet.addMergedRegion(title_cell);
        sheet.addMergedRegion(conditions_cell);

        // 表头行
        int headRow = sheet.getLastRowNum() + 1;
        HSSFRow row_head = sheet.createRow(headRow);
        int k = 0;
        for (Map.Entry<String, String> entry : columnHead.entrySet()) {
            HSSFCell cell = row_head.createCell(k);
            cell.setCellValue(entry.getKey());
            k++;
        }

        // 数据行
        int startRow = headRow + 1;
        for (int i = startRow; i < startRow + dataList.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            int j = 0;
            for (Map.Entry<String, String> entry : columnHead.entrySet()) {
                HSSFCell cell = row.createCell(j);
                if (entry.getValue().equals("rn")) {
                    cell.setCellValue(Integer.toString(i - startRow + 1));
                } else {
                    try {
                        Object obj = dataList.get(i - startRow).get(entry.getValue());
                        if (obj instanceof Date) {
                            cell.setCellValue(formatter.format((Date) obj));
                        } else {
                            cell.setCellValue(obj.toString());
                        }

                    } catch (NullPointerException e) {
                        // TODO: handle exception
                        cell.setCellValue("");
                    }

                }
                j++;

            }

        }
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
        sheet.setColumnWidth(0, 2000);

        HSSFCellStyle titlestyle = wb.createCellStyle();
        titlestyle.setBorderLeft(BorderStyle.THIN);
        titlestyle.setBorderRight(BorderStyle.THIN);
        titlestyle.setBorderTop(BorderStyle.THIN);
        titlestyle.setBorderBottom(BorderStyle.THIN);
        titlestyle.setWrapText(true);
        titlestyle.setAlignment(CENTER);
        titlestyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont titlefont = wb.createFont();
        titlefont.setFontName("宋体");
        titlefont.setFontHeightInPoints((short) 16);
        titlefont.setBold(true);
        titlestyle.setFont(titlefont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(0).getCell(i).setCellStyle(titlestyle);
        }
        sheet.getRow(0).setHeightInPoints((short) 16);

        HSSFCellStyle headstyle = wb.createCellStyle();
        headstyle.cloneStyleFrom(titlestyle);
        HSSFFont headfont = wb.createFont();
        headfont.setFontName("宋体");
        headfont.setFontHeightInPoints((short) 10);
        headfont.setBold(true);
        headstyle.setFont(headfont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(2).getCell(i).setCellStyle(headstyle);
        }

        HSSFCellStyle datastyle = wb.createCellStyle();
        datastyle.cloneStyleFrom(titlestyle);
        HSSFFont datafont = wb.createFont();
        datafont.setFontName("宋体");
        datafont.setFontHeightInPoints((short) 10);
        datastyle.setFont(datafont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(1).getCell(i).setCellStyle(datastyle);
        }

        for (int i = 3; i < 3 + dataList.size(); i++) {
            for (int j = 0; j < columnHead.size(); j++) {
                sheet.getRow(i).getCell(j).setCellStyle(datastyle);
            }

        }

        String filename = fileName + ".xls";
        response.setContentType("application/ms-excel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(filename, "UTF-8"))));
        OutputStream out = response.getOutputStream();
        try {
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            wb.close();
        }

    }

    public static void ExportToExcelForEmployee(String fileName, String title, String
            secondRow, Map<String, String> columnHead, Map[] dataList, HttpServletResponse response) throws Exception {
        int collumnNum = columnHead.size();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(CENTER);
        sheet.setDefaultColumnStyle(collumnNum, style);

        HSSFRow row0 = sheet.createRow(0);
        for (int i = 0; i < columnHead.size(); i++) {
            row0.createCell(i);
        }
        row0.getCell(0).setCellValue(title);

        HSSFRow row1 = sheet.createRow(1);
        for (int i = 0; i < columnHead.size(); i++) {
            row1.createCell(i);
        }
        row1.getCell(0).setCellValue(secondRow);

        CellRangeAddress title_cell = new CellRangeAddress(0, 0, 0, collumnNum - 1);
        CellRangeAddress conditions_cell = new CellRangeAddress(1, 1, 0, collumnNum - 1);// 起始行,结束行,起始列,结束列
        sheet.addMergedRegion(title_cell);
        sheet.addMergedRegion(conditions_cell);

        // 表头行
        int headRow = sheet.getLastRowNum() + 1;
        HSSFRow row_head = sheet.createRow(headRow);
        int k = 0;
        for (Map.Entry<String, String> entry : columnHead.entrySet()) {
            HSSFCell cell = row_head.createCell(k);
            cell.setCellValue(entry.getKey());
            k++;
        }

        // 数据行
        int startRow = headRow + 1;
        for (int i = startRow; i < startRow + dataList.length; i++) {
            HSSFRow row = sheet.createRow(i);
            int j = 0;
            for (Map.Entry<String, String> entry : columnHead.entrySet()) {
                HSSFCell cell = row.createCell(j);
                if (entry.getValue().equals("rn")) {
                    cell.setCellValue(Integer.toString(i - startRow + 1));
                } else if (entry.getValue().equals("Onstate")) {
                    if (dataList[i - startRow].get(entry.getValue()).toString().equals("1")) {
                        cell.setCellValue("是");
                    } else {
                        cell.setCellValue("否");
                    }

                } else if (entry.getValue().equals("OutCompanyOrNot")) {
                    if (dataList[i - startRow].get(entry.getValue()).toString().equals("1")) {
                        cell.setCellValue("是");
                    } else {
                        cell.setCellValue("否");
                    }

                } else if (entry.getValue().equals("IsRetire")) {
                    if (dataList[i - startRow].get(entry.getValue()).toString().equals("1")) {
                        cell.setCellValue("是");
                    } else {
                        cell.setCellValue("否");
                    }

                } else if (entry.getValue().equals("JoinCompanyDate")) {
                    if(dataList[i - startRow].get(entry.getValue())!=null){
                        if (dataList[i - startRow].get(entry.getValue()).toString().equals("1970-01-01")) {
                            cell.setCellValue("");
                        } else {
                            cell.setCellValue(dataList[i - startRow].get(entry.getValue()).toString());
                        }
                    }else {
                        cell.setCellValue("");
                    }

                } else if (entry.getValue().equals("OutCompanyDate")) {
                    if (dataList[i - startRow].get(entry.getValue()).toString().equals("1970-01-01")) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(dataList[i - startRow].get(entry.getValue()).toString());
                    }

                } else if (entry.getValue().equals("RetireTime")) {
                    if (dataList[i - startRow].get(entry.getValue()).toString().equals("1970-01-01")) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(dataList[i - startRow].get(entry.getValue()).toString());
                    }

                } else if (entry.getValue().equals("EmployeeBirth")) {
                    if (dataList[i - startRow].get(entry.getValue()) == null || dataList[i - startRow].get(entry.getValue()).toString().equals("1970-01-01")) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(dataList[i - startRow].get(entry.getValue()).toString());
                    }

                } else {
                    try {
                        cell.setCellValue(dataList[i - startRow].get(entry.getValue()).toString());
                    } catch (NullPointerException e) {
                        // TODO: handle exception
                        cell.setCellValue("");
                    }

                }
                j++;

            }
        }
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
        sheet.setColumnWidth(0, 2000);

        HSSFCellStyle titlestyle = wb.createCellStyle();
        titlestyle.setBorderLeft(BorderStyle.THIN);
        titlestyle.setBorderRight(BorderStyle.THIN);
        titlestyle.setBorderTop(BorderStyle.THIN);
        titlestyle.setBorderBottom(BorderStyle.THIN);
        titlestyle.setWrapText(true);
        titlestyle.setAlignment(CENTER);
        titlestyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont titlefont = wb.createFont();
        titlefont.setFontName("宋体");
        titlefont.setFontHeightInPoints((short) 16);
        titlefont.setBold(true);
        titlestyle.setFont(titlefont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(0).getCell(i).setCellStyle(titlestyle);
        }
        sheet.getRow(0).setHeightInPoints((short) 16);

        HSSFCellStyle headstyle = wb.createCellStyle();
        headstyle.cloneStyleFrom(titlestyle);
        HSSFFont headfont = wb.createFont();
        headfont.setFontName("宋体");
        headfont.setFontHeightInPoints((short) 12);
        headfont.setBold(true);
        headstyle.setFont(headfont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(2).getCell(i).setCellStyle(headstyle);
        }

        HSSFCellStyle datastyle = wb.createCellStyle();
        datastyle.cloneStyleFrom(titlestyle);
        HSSFFont datafont = wb.createFont();
        datafont.setFontName("宋体");
        datafont.setFontHeightInPoints((short) 12);
        datastyle.setFont(datafont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(1).getCell(i).setCellStyle(datastyle);
        }

        for (int i = 3; i < 3 + dataList.length; i++) {
            for (int j = 0; j < columnHead.size(); j++) {
                sheet.getRow(i).getCell(j).setCellStyle(datastyle);
            }

        }

        String filename = fileName + ".xls";
        response.setContentType("application/ms-excel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(filename, "UTF-8"))));
        OutputStream out = response.getOutputStream();
        try {
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            wb.close();
        }

    }

    public static void ExportToExcelForEvaluation(String fileName, String title, String
            secondRow, Map<String, String> columnHead, List<Map<String, Object>> dataList, HttpServletResponse response) throws Exception {
        int collumnNum = columnHead.size();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(CENTER);
        sheet.setDefaultColumnStyle(collumnNum, style);

        HSSFRow row0 = sheet.createRow(0);
        for (int i = 0; i < columnHead.size(); i++) {
            row0.createCell(i);
        }
        row0.getCell(0).setCellValue(title);

        HSSFRow row1 = sheet.createRow(1);
        for (int i = 0; i < columnHead.size(); i++) {
            row1.createCell(i);
        }
        row1.getCell(0).setCellValue(secondRow);

        CellRangeAddress title_cell = new CellRangeAddress(0, 0, 0, collumnNum - 1);
        CellRangeAddress conditions_cell = new CellRangeAddress(1, 1, 0, collumnNum - 1);// 起始行,结束行,起始列,结束列
        sheet.addMergedRegion(title_cell);
        sheet.addMergedRegion(conditions_cell);

        // 表头行
        int headRow = sheet.getLastRowNum() + 1;
        HSSFRow row_head = sheet.createRow(headRow);
        int k = 0;
        for (Map.Entry<String, String> entry : columnHead.entrySet()) {
            HSSFCell cell = row_head.createCell(k);
            cell.setCellValue(entry.getKey());
            k++;
        }

        // 数据行
        int startRow = headRow + 1;
        for (int i = startRow; i < startRow + dataList.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            int j = 0;
            for (Map.Entry<String, String> entry : columnHead.entrySet()) {
                HSSFCell cell = row.createCell(j);
                if (entry.getValue().equals("rn")) {
                    cell.setCellValue(Integer.toString(i - startRow + 1));
                } else if (entry.getValue().equals("ScoreLevel")) {
                    if (dataList.get(i - startRow).get(entry.getValue()).toString().equals("1")) {
                        cell.setCellValue("优秀");
                    } else if (dataList.get(i - startRow).get(entry.getValue()).toString().equals("2")) {
                        cell.setCellValue("良好");
                    } else if (dataList.get(i - startRow).get(entry.getValue()).toString().equals("3")) {
                        cell.setCellValue("合格");
                    } else if (dataList.get(i - startRow).get(entry.getValue()).toString().equals("4")) {
                        cell.setCellValue("不合格");
                    } else {
                        cell.setCellValue("");
                    }

                } else if (entry.getValue().equals("Score")) {
                    if (dataList.get(i - startRow).get(entry.getValue()) == null) {
                        cell.setCellValue("");
                    } else {
                        String value = dataList.get(i - startRow).get(entry.getValue()).toString();
                        if (value.substring(value.length() - 2).equals(".0")) {
                            cell.setCellValue(value.substring(0, value.length() - 2));
                        } else {
                            cell.setCellValue(value);
                        }
                    }
                } else {
                    try {
                        cell.setCellValue(dataList.get(i - startRow).get(entry.getValue()).toString());
                    } catch (NullPointerException e) {
                        // TODO: handle exception
                        cell.setCellValue("");
                    }

                }
                j++;

            }
        }

        //多加一行  表尾  制表人： 部门负责人：
        HSSFRow row2 = sheet.createRow(sheet.getLastRowNum() + 2);
        for (int i = 0; i < columnHead.size(); i++) {
            row2.createCell(i);
        }
        row2.getCell(0).setCellValue("制表人：");
        row2.getCell(4).setCellValue("部门负责人（签字）：");

        CellRangeAddress title_end_cell = new CellRangeAddress(startRow + dataList.size()+1, startRow + dataList.size()+1, 4, 5);
        sheet.addMergedRegion(title_end_cell);


        for (int i = 0; i < columnHead.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(2,6000);
        sheet.setColumnWidth(3,6000);

        HSSFCellStyle titlestyle = wb.createCellStyle();
        titlestyle.setBorderLeft(BorderStyle.THIN);
        titlestyle.setBorderRight(BorderStyle.THIN);
        titlestyle.setBorderTop(BorderStyle.THIN);
        titlestyle.setBorderBottom(BorderStyle.THIN);
        titlestyle.setWrapText(true);
        titlestyle.setAlignment(CENTER);
        titlestyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont titlefont = wb.createFont();
        titlefont.setFontName("宋体");
        titlefont.setFontHeightInPoints((short) 16);
        titlefont.setBold(true);
        titlestyle.setFont(titlefont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(0).getCell(i).setCellStyle(titlestyle);
        }
        sheet.getRow(0).setHeight((short) 400);

        HSSFCellStyle headstyle = wb.createCellStyle();
        headstyle.cloneStyleFrom(titlestyle);
        HSSFFont headfont = wb.createFont();
        headfont.setFontName("宋体");
        headfont.setFontHeightInPoints((short) 12);
        headfont.setBold(true);
        headstyle.setFont(headfont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(2).getCell(i).setCellStyle(headstyle);
        }

        HSSFCellStyle datastyle = wb.createCellStyle();
        datastyle.cloneStyleFrom(titlestyle);
        HSSFFont datafont = wb.createFont();
        datafont.setFontName("宋体");
        datafont.setFontHeightInPoints((short) 12);
        datastyle.setFont(datafont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(1).getCell(i).setCellStyle(datastyle);
        }

        for (int i = 3; i < 3 + dataList.size(); i++) {
            for (int j = 0; j < columnHead.size(); j++) {
                sheet.getRow(i).getCell(j).setCellStyle(datastyle);
            }

        }

        String filename = fileName + ".xls";
        response.setContentType("application/ms-excel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(filename, "UTF-8"))));
        OutputStream out = response.getOutputStream();
        try {
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            wb.close();
        }

    }

    public static String captureName(String name) {
        // name = name.substring(0, 1).toUpperCase() + name.substring(1);
        // return name;
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);

    }

    public static int getEnvironment() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return 1;
        } else {
            return 2;
        }
    }

    public static void ExportToExcel(String fileName, String title, String
            secondRow, Map<String, String> columnHead, Map[] dataList, HttpServletResponse response) throws Exception {
        int collumnNum = columnHead.size();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(CENTER);
        sheet.setDefaultColumnStyle(collumnNum, style);

        HSSFRow row0 = sheet.createRow(0);
        for (int i = 0; i < columnHead.size(); i++) {
            row0.createCell(i);
        }
        row0.getCell(0).setCellValue(title);

        HSSFRow row1 = sheet.createRow(1);
        for (int i = 0; i < columnHead.size(); i++) {
            row1.createCell(i);
        }
        row1.getCell(0).setCellValue(secondRow);

        CellRangeAddress title_cell = new CellRangeAddress(0, 0, 0, collumnNum - 1);
        CellRangeAddress conditions_cell = new CellRangeAddress(1, 1, 0, collumnNum - 1);// 起始行,结束行,起始列,结束列
        sheet.addMergedRegion(title_cell);
        sheet.addMergedRegion(conditions_cell);

        // 表头行
        int headRow = sheet.getLastRowNum() + 1;
        HSSFRow row_head = sheet.createRow(headRow);
        int k = 0;
        for (Map.Entry<String, String> entry : columnHead.entrySet()) {
            HSSFCell cell = row_head.createCell(k);
            cell.setCellValue(entry.getKey());
            k++;
        }

        // 数据行
        int startRow = headRow + 1;
        for (int i = startRow; i < startRow + dataList.length; i++) {
            HSSFRow row = sheet.createRow(i);
            int j = 0;
            for (Map.Entry<String, String> entry : columnHead.entrySet()) {
                HSSFCell cell = row.createCell(j);
                if (entry.getValue().equals("rn")) {
                    cell.setCellValue(Integer.toString(i - startRow + 1));
                } else {
                    try {
                        cell.setCellValue(dataList[i - startRow].get(entry.getValue()).toString());
                    } catch (NullPointerException e) {
                        // TODO: handle exception
                        cell.setCellValue("");
                    }

                }
                j++;

            }

        }
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.setColumnWidth(i, 3500);
        }
        sheet.setColumnWidth(0, 2000);

        HSSFCellStyle titlestyle = wb.createCellStyle();
        titlestyle.setBorderLeft(BorderStyle.THIN);
        titlestyle.setBorderRight(BorderStyle.THIN);
        titlestyle.setBorderTop(BorderStyle.THIN);
        titlestyle.setBorderBottom(BorderStyle.THIN);
        titlestyle.setWrapText(true);
        titlestyle.setAlignment(CENTER);
        titlestyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont titlefont = wb.createFont();
        titlefont.setFontName("宋体");
        titlefont.setFontHeightInPoints((short) 16);
        titlefont.setBold(true);
        titlestyle.setFont(titlefont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(0).getCell(i).setCellStyle(titlestyle);
        }
        sheet.getRow(0).setHeightInPoints((short) 16);

        HSSFCellStyle headstyle = wb.createCellStyle();
        headstyle.cloneStyleFrom(titlestyle);
        HSSFFont headfont = wb.createFont();
        headfont.setFontName("宋体");
        headfont.setFontHeightInPoints((short) 10);
        headfont.setBold(true);
        headstyle.setFont(headfont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(2).getCell(i).setCellStyle(headstyle);
        }

        HSSFCellStyle datastyle = wb.createCellStyle();
        datastyle.cloneStyleFrom(titlestyle);
        HSSFFont datafont = wb.createFont();
        datafont.setFontName("宋体");
        datafont.setFontHeightInPoints((short) 10);
        datastyle.setFont(datafont);
        for (int i = 0; i < columnHead.size(); i++) {
            sheet.getRow(1).getCell(i).setCellStyle(datastyle);
        }

        for (int i = 3; i < 3 + dataList.length; i++) {
            for (int j = 0; j < columnHead.size(); j++) {
                sheet.getRow(i).getCell(j).setCellStyle(datastyle);
            }

        }

        String filename = fileName + ".xls";
        response.setContentType("application/ms-excel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(filename, "UTF-8"))));
        OutputStream out = response.getOutputStream();
        try {
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            wb.close();
        }

    }

    public static class ExcelCommons {
        public static void handleResponse(HttpServletResponse response,HSSFWorkbook wb,String fileName){
            response.setContentType("application/ms-excel;charset=UTF-8");
            OutputStream out=null;
            try {
                response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
                out = response.getOutputStream();
                wb.write(out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(out!=null){
                        out.close();
                    }
                    wb.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        public static void setMergedRegions(HSSFSheet sheet,int dataRow,List<Map<String,Object>> dataList,String target,int i,int j){
            if(i== dataList.size()-1){
                if((dataList.get(i).get(target)!=null && dataList.get(i).get(target).equals(dataList.get(i-1).get(target))) || (dataList.get(i-1).get(target)!=null && dataList.get(i-1).get(target).equals(dataList.get(i).get(target)))){
                    sheet.getRow(i+dataRow).getCell(j).setCellValue("");
                    int t;
                    for (t=i;t>=1 && dataList.get(t).get(target)!=null && dataList.get(t).get(target).equals(dataList.get(t-1).get(target));t--){
                        sheet.getRow(t+dataRow).getCell(j).setCellValue("");
                    }
                    sheet.addMergedRegion(new CellRangeAddress(t+dataRow,i+dataRow,j,j));
                }else{
                    int t;
                    for (t=i-1;t>=1 && dataList.get(t).get(target)!=null && dataList.get(t).get(target).equals(dataList.get(t-1).get(target));t--){
                        sheet.getRow(t+dataRow).getCell(j).setCellValue("");
                    }
                    if(i-1>t){
                        sheet.addMergedRegion(new CellRangeAddress(t+dataRow,i-1+dataRow,j,j));
                    }
                }
            } else if((dataList.get(i).get(target)!=null && !dataList.get(i).get(target).equals(dataList.get(i-1).get(target))) || (dataList.get(i-1).get(target)!=null && !dataList.get(i-1).get(target).equals(dataList.get(i).get(target)))){
                int t;
                for (t=i-1;t>=1 && dataList.get(t).get(target)!=null && dataList.get(t).get(target).equals(dataList.get(t-1).get(target));t--){
                    sheet.getRow(t+dataRow).getCell(j).setCellValue("");
                }
                if(i-1>t){
                    sheet.addMergedRegion(new CellRangeAddress(t+dataRow,i-1+dataRow,j,j));
                }
            }
        }
        public static void setCommonStyle(HSSFWorkbook wb,HSSFSheet sheet,int columnNum,int dataSize,int headRow){
            HSSFCellStyle titlestyle = getCommonStyle(wb);
            titlestyle.setFont(getFont(wb,16,true));
            for (int i = 0; i < columnNum; i++) {
                sheet.getRow(0).getCell(i).setCellStyle(titlestyle);
            }
            sheet.getRow(0).setHeightInPoints(30);

            HSSFCellStyle headstyle = getCommonStyle(wb);
            headstyle.setFont(getFont(wb,12,true));
            for (int i = 0; i < columnNum; i++) {
                sheet.getRow(headRow).getCell(i).setCellStyle(headstyle);
            }

            HSSFCellStyle datastyle = getCommonStyle(wb);
            datastyle.setFont(getFont(wb,12,false));
            for (int i = headRow+1; i < headRow+1 + dataSize; i++) {
                for (int j = 0; j < columnNum; j++) {
                    sheet.getRow(i).getCell(j).setCellStyle(datastyle);
                }
            }
        }
        public static HSSFFont getFont(HSSFWorkbook wb,int fontHeight,boolean isBold){
            HSSFFont titlefont = wb.createFont();
            titlefont.setFontName("宋体");
            titlefont.setFontHeightInPoints((short) fontHeight);
            titlefont.setBold(isBold);
            return titlefont;
        }
        public static HSSFCellStyle getCommonStyle(HSSFWorkbook wb){
            HSSFCellStyle titlestyle = wb.createCellStyle();
            titlestyle.setBorderLeft(BorderStyle.THIN);
            titlestyle.setBorderRight(BorderStyle.THIN);
            titlestyle.setBorderTop(BorderStyle.THIN);
            titlestyle.setBorderBottom(BorderStyle.THIN);
            titlestyle.setWrapText(true);
            titlestyle.setAlignment(HorizontalAlignment.CENTER);
            titlestyle.setVerticalAlignment(VerticalAlignment.CENTER);
            return titlestyle;
        }
    }
}
