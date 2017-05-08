package com.bm.insurance.cloud.orm.entity;


import java.io.Serializable;
import java.util.Date;

public class TjUploadInfo implements Serializable {
    private static final long serialVersionUID = 7244505590103036300L;
    private String tjName;//体检人姓名
    private Long tjOrderId;//体检订单号
    private String tjOrderCompanyId;//体检公司
    private Date tjOrderTime;//预约体检时间
    private Date tjDownloadTime;//下载日期
    private Date tjUploadTime;//解析日期
    private int tjUploadStatus;//体检解析状态
    private String tjPdfPath;//PDF文件地址
    private String cardId;//身份证号
    private String moblie;//手机号
    private Long upLoadId;//上传的编号
    private String md5Key;

    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }


    public Long getUpLoadId() {
        return upLoadId;
    }

    public void setUpLoadId(Long upLoadId) {
        this.upLoadId = upLoadId;
    }



    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }



    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }




    public String getTjName() {
        return tjName;
    }

    public void setTjName(String tjName) {
        this.tjName = tjName;
    }

    public Long getTjOrderId() {
        return tjOrderId;
    }

    public void setTjOrderId(Long tjOrderId) {
        this.tjOrderId = tjOrderId;
    }

    public String getTjOrderCompanyId() {
        return tjOrderCompanyId;
    }

    public void setTjOrderCompanyId(String tjOrderCompanyId) {
        this.tjOrderCompanyId = tjOrderCompanyId;
    }

    public String getTjPdfPath() {
        return tjPdfPath;
    }

    public void setTjPdfPath(String tjPdfPath) {
        this.tjPdfPath = tjPdfPath;
    }

    public Date getTjOrderTime() {
        return tjOrderTime;
    }

    public void setTjOrderTime(Date tjOrderTime) {
        this.tjOrderTime = tjOrderTime;
    }

    public Date getTjDownloadTime() {
        return tjDownloadTime;
    }

    public void setTjDownloadTime(Date tjDownloadTime) {
        this.tjDownloadTime = tjDownloadTime;
    }

    public int getTjUploadStatus() {
        return tjUploadStatus;
    }

    public void settjUploadStatus(int tjUploadStatus) {
        this.tjUploadStatus = tjUploadStatus;
    }

    public Date getTjUploadTime() {
        return tjUploadTime;
    }

    public void setTjUploadTime(Date tjUploadTime) {
        this.tjUploadTime = tjUploadTime;
    }

}
