package com.bm.insurance.service.job.tj_upload;

import com.bm.insurance.cloud.TjCheckInfoRequest;
import com.bm.insurance.cloud.bean.MedicalExamReportBean;
import com.bm.insurance.cloud.orm.bean.PeTjCheckInfo;
import com.bm.insurance.cloud.orm.dao.TJUserOrderMapper;
import com.bm.insurance.cloud.orm.dao.TjCheckInfoMapper;
import com.bm.insurance.cloud.orm.entity.TJUserOrder;
import com.bm.insurance.cloud.orm.entity.TjUploadInfo;
import com.bm.insurance.commonservice.oss.OSSService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.pobjects.PageTree;
import org.icepdf.core.pobjects.graphics.text.LineText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WFY
 * DATE: 2017/4/24
 * TIME：9:10
 */
@Service
public class TjUploadUtils {

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final Integer ONLY_ONE_FILE = 1;
    private static final Integer ONLY_ONE_NAME = 0;
    private static final Integer TJ_STATUS_MATCH_FILED = 0;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    //    final private String localTempPath = "C:\\Users\\WFY\\Desktop\\aaaaa\\";
    final private String BaseScrPath = "/upload/spider/";
    private TjCheckInfoRequest tjCheckInfoRequest;
    private String pdfTempPath = System.getProperty("java.io.tmpdir");
    private String pdfPath = (pdfTempPath.endsWith(FILE_SEPARATOR) ? pdfTempPath : (pdfTempPath + FILE_SEPARATOR)) + "PDF" + FILE_SEPARATOR;
    private int count = 0;
    @Autowired
    private OSSService ossService;

    @Autowired
    private TjCheckInfoMapper tjCheckInfoMapper;

    @Autowired
    private TjUploadProcess tjUploadProcess;

    @Autowired
    private TJUserOrderMapper tjUserOrderMapper;
    @Autowired
    private TjDaoProcess tjDaoProcess;

    public boolean deleteFile(File file) {
        Boolean flag = false;

        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public boolean deleteDirectory(File fileDir) {
        Boolean flag = false;

        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = fileDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i]);
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (fileDir.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Delete the whole files below the path
     *
     * @param str
     */
    public void DeleteFiles(String str) {
        File destDir = new File(str);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        for (File realTempFile : destDir.listFiles()) {
            if (!realTempFile.toString().equals(str)) {
                if (!realTempFile.isDirectory()) {
                    deleteFile(realTempFile);
                } else {
                    deleteDirectory(realTempFile);
                }
            }
        }
    }

    /**
     * getUploadFileByOssPath
     *
     * @param ossPath
     * @return TjUploadOssEntity
     */
    public void getUploadFileByOssPath(String ossPath, String localPath) throws Exception {
        InputStream inputStream;
        FileOutputStream fileOutputStream;
        File localTempZipFile;

        if (ossPath == null)
            return;
        List<String> fileNameList = ossService.getFilesNamePrivateDir(ossPath);
        Pattern pattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
        try {
            if (fileNameList == null)
                return;
            for (String ossNamepath : fileNameList) {
                if (ossNamepath.endsWith("/"))
                    continue;
                Matcher m = pattern.matcher(ossNamepath);
                if (m.find()) {
                    String subDate = m.group();
                    File subFileDir = new File(localPath + subDate);
                    if (!subFileDir.exists()) {
                        subFileDir.mkdirs();
                    }
                    String localTempPath = localPath + subDate + File.separator;
                    inputStream = ossService.downloadDJFile(ossNamepath);
//                    String realFileName = fileNameOnline.substring(fileNameOnline.lastIndexOf("/") + 1, fileNameOnline.length());
                    localTempZipFile = new File(localTempPath + ossNamepath.substring(ossNamepath.lastIndexOf("/"), ossNamepath.length()));
                    fileOutputStream = new FileOutputStream(localTempZipFile);
                    IOUtils.copyLarge(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    System.out.println(localTempPath);
                    if (localTempZipFile.toString().endsWith(".zip")) {
                        ZipDecompressingUtils.unZip(localTempZipFile, localTempPath, Charset.forName("GBK"));
                    }

                }
            }
        } catch (Exception e) {
            logger.info("Get Error files from oss " + e.toString());
        }
        return;
    }

    public TjUploadOssEntity getSignleFileName(File pdfFile) throws Exception {

        ArrayList<LineText> lines = null;
        LineText line = null;
        String lineStr = null;
        Long uid = null;
        String name = "";
        TjUploadOssEntity tjUploadOssEntity = new TjUploadOssEntity();
        InputStream inputStream;
        Pattern namePattern = Pattern.compile("(先生[ ]*[\\u4e00-\\u9fa5]*)|([\\u4e00-\\u9fa5]*[ ]*先生)");
        Pattern namePattern_ = Pattern.compile("(女士[ ]*[\\u4e00-\\u9fa5]*)|([\\u4e00-\\u9fa5]*[ ]*女士)");
        Pattern pattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
        Matcher m = pattern.matcher(pdfFile.toString());
        inputStream = new FileInputStream(pdfFile);
        Document document = new Document();
        document.setInputStream(inputStream, "");
        PageTree dTree = document.getPageTree();
        if (dTree == null)
            return null;
        Page page = dTree.getPage(0);
        if (page != null && page.getText() != null) {
            lines = page.getText().getPageLines();
            for (int l = 0; l < lines.size(); l++) {
                line = lines.get(l);

                if (line.toString().indexOf(", 女]") > 0) {
                    name = line.toString().substring(1, line.toString().indexOf(","));
                }
                if (line.toString().indexOf(", 男]") > 0) {
                    name = line.toString().substring(1, line.toString().indexOf(","));
                }
                //先替换[] 空格 逗号
                lineStr = line.toString().replaceAll("[,\" \"\\[\\]]", "").replaceAll("\\s+", "");
                if (StringUtils.isBlank(lineStr) || line.getWords().size() == 0) {
                    continue;
                }
                if (lineStr.contains("美年")) {
                    tjUploadOssEntity.setTjCompany("北京美年大健康");
                    tjUploadOssEntity.setTempFilePath(pdfFile.toString());
                    if (m.find())
                        tjUploadOssEntity.setOrderTime(m.group());
                }
                if (lineStr.contains("爱康")) {
                    tjUploadOssEntity.setTjCompany("爱康国宾");
                    tjUploadOssEntity.setTempFilePath(pdfFile.toString());
                    if (m.find())
                        tjUploadOssEntity.setOrderTime(m.group());
                }
                //美年的第一页包含身份证信息
                if (lineStr.contains("身份证")) {
                    //截取身份证字符串
                    String idCard = lineStr.replaceAll("[身份证：:\" \"]", "").trim();
                    tjUploadOssEntity.setIdCardNo(idCard);
                }
                Matcher nameMatcher = namePattern.matcher(lineStr);
                Matcher nameMatcher_ = namePattern_.matcher(lineStr);
                if (nameMatcher.find() && org.apache.commons.lang3.StringUtils.isBlank(name) && nameMatcher.group().length() > 3) {
                    name = nameMatcher.group().split("女士|先生")[0];
                } else if (nameMatcher_.find() && org.apache.commons.lang3.StringUtils.isBlank(name) && nameMatcher_.group().length() > 3) {
                    name = nameMatcher_.group().split("女士|先生")[0];
                }

                if (name != null && name.length() > 1)
                    tjUploadOssEntity.setuName(name);
            }

        }
        inputStream.close();
        return tjUploadOssEntity;
    }


    public List<TjUploadOssEntity> getFileNameAndOrderTime(String localPath) throws Exception {
        File localTempDirectory = new File(localPath);
        List<TjUploadOssEntity> tjUploadOssEntities = new ArrayList<TjUploadOssEntity>();
        if (!localTempDirectory.exists())
            return null;
        try {
            for (File file : localTempDirectory.listFiles()) {
                if (file.isDirectory()) {
                    for (File subfile : file.listFiles()) {
                        if (!subfile.toString().endsWith(".pdf"))
                            continue;
                        else {
                            TjUploadOssEntity tjUploadOssEntity = getSignleFileName(subfile);
                            if (tjUploadOssEntity != null)
                                tjUploadOssEntities.add(tjUploadOssEntity);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.info("getFileNameAndOrderTime is error!: " + e.toString());
        }
        return tjUploadOssEntities;
    }

    /***
     * Get Spider File Form OSS
     * The process of this function is :
     * Step1: check the upload record,get the last record
     * Step2: if the last record is null,get whole files form ossPath
     * Step3: else check the ossPath every day
     *
     * @return fdf file list
     */
    public List<TjUploadOssEntity> tjGetSpiderFile(String BaseScrPath, String localTempPath) throws IOException {
        List<String> fileNameList = new ArrayList<String>();
        List<String> fileRealNameList = new ArrayList<String>();
        List<TjUploadOssEntity> tjUploadOssEntities = new ArrayList<TjUploadOssEntity>();
        InputStream inputStream;
        File localTempZipFile;
        File localTempDirectory = new File(localTempPath);
        if (!localTempDirectory.exists()) {
            localTempDirectory.mkdir();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        FileOutputStream fileOutputStream;

        try {

            TjUploadInfo tjUploadInfo = tjDaoProcess.findTheLastUploadInfoByOderTime();
            //Get the last uploaded file,if the file exists,return the last uploaded file date,else return a static date "2016-01-01" as a flag
            String theLastUploadDate = tjUploadInfo == null ? "2016-01-01" : (tjUploadInfo.getTjDownloadTime() == null ? null : format.format(tjUploadInfo.getTjDownloadTime()));
            if (theLastUploadDate.equals("2016-01-01")) {
                getUploadFileByOssPath(BaseScrPath, localTempPath);

            } else {
                Date todayDate = new Date();
                Date tempDate = tjUploadInfo.getTjDownloadTime();
                while (tempDate.getTime() < todayDate.getTime()) {
                    String ossPath = BaseScrPath + format.format(tempDate);
                    getUploadFileByOssPath(ossPath, localTempPath);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(tempDate);
                    cal.add(Calendar.DATE, 1);
                    String stDate = format.format(cal.getTime());
                    tempDate = format.parse(stDate);
                }
            }
            // Get the whole pdf files belows localTemPath,and get the name and other info in pdf.
            tjUploadOssEntities = getFileNameAndOrderTime(localTempPath);


        } catch (Exception e) {
            logger.info("tjGetSpiderFile is error!: " + e.toString());

        }
        return tjUploadOssEntities;
    }


    /**
     * Match localPdfFile
     * the process is:
     * step1 :check the dateBase ,get the special record which is about the pdf file
     * step2: If there are only one record then goto the Analysis,else only upload the pdf file and insert the record
     *
     * @param
     * @param
     */
    public List<PeTjCheckInfo> tjMatchPdfFile(List<TjUploadOssEntity> tjUploadOssEntities) throws Exception {
        List<PeTjCheckInfo> MatchedInfoList = new ArrayList<PeTjCheckInfo>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (TjUploadOssEntity tjUploadOssEntity : tjUploadOssEntities) {
            Integer sameNameCount = 0;
            for (TjUploadOssEntity tjUploadOssEntity1 : tjUploadOssEntities) {
                //If there are more than one same name ,same tjcompany person,then just upload the pdf file
                if (tjUploadOssEntity1.getuName().equals(tjUploadOssEntity.getuName()) && (tjUploadOssEntity1.getIdCardNo() == null ? true : tjUploadOssEntity1.getIdCardNo().equals(tjUploadOssEntity.getIdCardNo()))
                        && tjUploadOssEntity1.getTjCompany().equals(tjUploadOssEntity.getTjCompany())) {
                    sameNameCount++;
                }
            }
            List<PeTjCheckInfo> peTjCheckInfoList = tjDaoProcess.findAllOrdersNoBindByNameAndIdcard(tjUploadOssEntity);
            TjUploadInfo tjUploadInfo = new TjUploadInfo();
            tjUploadInfo.setTjName(tjUploadOssEntity.getuName());
            tjUploadInfo.setTjOrderCompanyId(tjUploadOssEntity.getTjCompany());
            tjUploadInfo.setTjDownloadTime(format.parse(tjUploadOssEntity.getOrderTime()));
            //If not found orderInfo in the dataBase,then only record the Msg
            if (peTjCheckInfoList == null || peTjCheckInfoList.size() != ONLY_ONE_FILE || sameNameCount != ONLY_ONE_FILE) {
                tjUploadInfo.settjUploadStatus(TJ_STATUS_MATCH_FILED);
                uploadPdfByNoMatchedPdf(tjUploadInfo, tjUploadOssEntity.getTempFilePath());
            }
            //If there are only one matched orderInfo then update the MatchedInfoList
            if (peTjCheckInfoList.size() == ONLY_ONE_FILE && sameNameCount == ONLY_ONE_FILE) {
                if (peTjCheckInfoList.get(0).getIdCard() != null) {
                    tjUploadInfo.setCardId(peTjCheckInfoList.get(0).getIdCard());
                }
                if (peTjCheckInfoList.get(0).getMobile() != null) {
                    tjUploadInfo.setMoblie(peTjCheckInfoList.get(0).getMobile());
                }
                if (peTjCheckInfoList.get(0).getOrderTime() != null) {
                    tjUploadInfo.setTjOrderTime(format.parse(format.format(peTjCheckInfoList.get(0).getOrderTime())));
                }
                if (peTjCheckInfoList.get(0).getTjCompanyName() == null) {
                    peTjCheckInfoList.get(0).setTjCompanyName(tjUploadOssEntity.getTjCompany());
                }
                tjUploadInfo.settjUploadStatus(TJ_STATUS_MATCH_FILED);
                tjUploadInfo.setTjOrderId(peTjCheckInfoList.get(0).getOrderId());
                TjUploadInfo tjUploadInfo1 = tjCheckInfoMapper.selectUploadInfoByPrimaryKey(peTjCheckInfoList.get(0).getOrderId());
                //If there has been exist one record then only update the record
                if (tjUploadInfo1 == null) {
                    tjCheckInfoMapper.insertTjUploadInfo(tjUploadInfo);
                } else {
                    tjCheckInfoMapper.updateUploadInfoByOrderId(tjUploadInfo);
                }
                MatchedInfoList.add(peTjCheckInfoList.get(0));
            }

        }
        return MatchedInfoList;
    }

    public void uploadPdfByNoMatchedPdf(TjUploadInfo tjUploadInfo, String realFileName) throws IOException {


        File localTempFile = new File(realFileName);
        logger.info("localTempFile:" + localTempFile.toString() + "Real :" + realFileName);
        tjUploadProcess.uploadTjNoMatchedReportHandler(localTempFile, tjUploadInfo);

    }


    /**
     * This function is getting the CheckInfo to ExamReportBean
     *
     * @param tjMatchedInfoList
     * @return
     */
    public List<MedicalExamReportBean> tjGetMedicalBeanByMatchedInfo(List<PeTjCheckInfo> tjMatchedInfoList) {
        List<MedicalExamReportBean> medicalExamReportBeansList = new ArrayList<MedicalExamReportBean>();

        for (PeTjCheckInfo peTjCheckInfo : tjMatchedInfoList) {
            MedicalExamReportBean medicalExamReportBean = new MedicalExamReportBean();
            // the Key Id
            medicalExamReportBean.setId(peTjCheckInfo.getId());
            //OrderId
            medicalExamReportBean.setOrderId(peTjCheckInfo.getOrderId());
            //Upload status
            medicalExamReportBean.setIsUpload(peTjCheckInfo.getIsUpload());
            //tjCompanyName

            medicalExamReportBean.setCompanyName(peTjCheckInfo.getTjCompanyName());

            medicalExamReportBeansList.add(medicalExamReportBean);
        }

        return medicalExamReportBeansList;
    }

    /**
     * UploadPdf by MedicalExamReportBean List
     *
     * @param
     * @param
     */
    public void tjUploadPdfByMatchedInfo(List<MedicalExamReportBean> medicalExamReportBeansList, List<PeTjCheckInfo> tjMatchedInfoList, List<TjUploadOssEntity> tjUploadOssEntities) throws IOException {
        FileInputStream inputStream;
        File localTempFile;
        for (MedicalExamReportBean examReport : medicalExamReportBeansList) {
            TJUserOrder tjUserOrder = tjUserOrderMapper.selectByPrimaryKey(examReport.getOrderId());
            System.out.println();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            String upFileTime = sdf.format(new Date());

            for (PeTjCheckInfo peTjCheckInfo : tjMatchedInfoList) {
                if (peTjCheckInfo.getOrderId().equals(examReport.getOrderId())) {
                    String fileName = peTjCheckInfo.getName();//得到名称
                    for (TjUploadOssEntity tjUploadOssEntity : tjUploadOssEntities) {
                        if (tjUploadOssEntity.getuName().equals(fileName)) {
                            localTempFile = new File(tjUploadOssEntity.getTempFilePath());
                            tjUploadProcess.uploadTjReportHandler(localTempFile, tjUserOrder, examReport);//上传爱康美年(解析爱康美年)
                            continue;

                        }
                    }

                }

            }
        }

    }


    public void TjUploadProcess() throws Exception {
        try {
            //delete all the temp files and dir
            DeleteFiles(pdfPath);
            //get all undownload files from oss
            List<TjUploadOssEntity> tjUploadOssEntities = tjGetSpiderFile(BaseScrPath, pdfPath);
            //get matched files by name and idcard
            List<PeTjCheckInfo> tjMatchedInfoList = tjMatchPdfFile(tjUploadOssEntities);
            //get ExamReport Bean
            List<MedicalExamReportBean> medicalExamReportBeansList = tjGetMedicalBeanByMatchedInfo(tjMatchedInfoList);
            //upload and analysis the matched files
            tjUploadPdfByMatchedInfo(medicalExamReportBeansList, tjMatchedInfoList, tjUploadOssEntities);
        } catch (Exception e) {
            logger.info("TjUploadProcess is error!: " + e.toString());
        }

    }

    @Scheduled(cron = "0 0/5 *  * * ? ")//每一天运行一次
    public void AutoTjUploadProcess() throws Exception {
        try {

            TjUploadProcess();
        } catch (Exception e) {
            logger.info("AutoTjUploadProcess is error" + e.toString());
        }
    }
}
