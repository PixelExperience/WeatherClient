package org.pixelexperience.weather.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherChannelApiResult {

    @SerializedName("MOHdr")
    @Expose
    private MOHdr mOHdr;
    @SerializedName("MOData")
    @Expose
    private MOData mOData;

    public MOHdr getMOHdr() {
        return mOHdr;
    }

    public void setMOHdr(MOHdr mOHdr) {
        this.mOHdr = mOHdr;
    }

    public MOData getMOData() {
        return mOData;
    }

    public void setMOData(MOData mOData) {
        this.mOData = mOData;
    }


    public class MOData {

        @SerializedName("stnNm")
        @Expose
        private String stnNm;
        @SerializedName("obsDayGmt")
        @Expose
        private String obsDayGmt;
        @SerializedName("obsTmGmt")
        @Expose
        private String obsTmGmt;
        @SerializedName("dyNght")
        @Expose
        private String dyNght;
        @SerializedName("locObsDay")
        @Expose
        private String locObsDay;
        @SerializedName("locObsTm")
        @Expose
        private String locObsTm;
        @SerializedName("tmpF")
        @Expose
        private Integer tmpF;
        @SerializedName("tmpC")
        @Expose
        private Integer tmpC;
        @SerializedName("sky")
        @Expose
        private Integer sky;
        @SerializedName("wx")
        @Expose
        private String wx;
        @SerializedName("iconExt")
        @Expose
        private Integer iconExt;
        @SerializedName("alt")
        @Expose
        private Double alt;
        @SerializedName("baroTrnd")
        @Expose
        private Integer baroTrnd;
        @SerializedName("baroTrndAsc")
        @Expose
        private String baroTrndAsc;
        @SerializedName("clds")
        @Expose
        private String clds;
        @SerializedName("dwptF")
        @Expose
        private Integer dwptF;
        @SerializedName("dwptC")
        @Expose
        private Integer dwptC;
        @SerializedName("hIF")
        @Expose
        private Integer hIF;
        @SerializedName("hIC")
        @Expose
        private Integer hIC;
        @SerializedName("rH")
        @Expose
        private Integer rH;
        @SerializedName("pres")
        @Expose
        private Double pres;
        @SerializedName("presChnge")
        @Expose
        private Double presChnge;
        @SerializedName("visM")
        @Expose
        private Double visM;
        @SerializedName("visK")
        @Expose
        private Double visK;
        @SerializedName("wCF")
        @Expose
        private Integer wCF;
        @SerializedName("wCC")
        @Expose
        private Integer wCC;
        @SerializedName("wDir")
        @Expose
        private Integer wDir;
        @SerializedName("wDirAsc")
        @Expose
        private String wDirAsc;
        @SerializedName("wSpdM")
        @Expose
        private Integer wSpdM;
        @SerializedName("wSpdK")
        @Expose
        private Integer wSpdK;
        @SerializedName("wSpdKn")
        @Expose
        private Integer wSpdKn;
        @SerializedName("tmpMx24F")
        @Expose
        private Integer tmpMx24F;
        @SerializedName("tmpMx24C")
        @Expose
        private Integer tmpMx24C;
        @SerializedName("tmpMn24F")
        @Expose
        private Integer tmpMn24F;
        @SerializedName("tmpMn24C")
        @Expose
        private Integer tmpMn24C;
        @SerializedName("prcp24")
        @Expose
        private Double prcp24;
        @SerializedName("prcp3_6hr")
        @Expose
        private Double prcp36hr;
        @SerializedName("prcpHr")
        @Expose
        private Double prcpHr;
        @SerializedName("prcpMTD")
        @Expose
        private Double prcpMTD;
        @SerializedName("prcpYr")
        @Expose
        private Double prcpYr;
        @SerializedName("prcp2Dy")
        @Expose
        private Double prcp2Dy;
        @SerializedName("prcp3Dy")
        @Expose
        private Double prcp3Dy;
        @SerializedName("prcp7Dy")
        @Expose
        private Double prcp7Dy;
        @SerializedName("snwDep")
        @Expose
        private Double snwDep;
        @SerializedName("snwIncr")
        @Expose
        private Double snwIncr;
        @SerializedName("snwTot")
        @Expose
        private Double snwTot;
        @SerializedName("snwTot6hr")
        @Expose
        private Double snwTot6hr;
        @SerializedName("snwMTD")
        @Expose
        private Double snwMTD;
        @SerializedName("snwSsn")
        @Expose
        private Double snwSsn;
        @SerializedName("snwYr")
        @Expose
        private Double snwYr;
        @SerializedName("snw2Dy")
        @Expose
        private Double snw2Dy;
        @SerializedName("snw3Dy")
        @Expose
        private Double snw3Dy;
        @SerializedName("snw7Dy")
        @Expose
        private Double snw7Dy;
        @SerializedName("sunrise")
        @Expose
        private String sunrise;
        @SerializedName("sunset")
        @Expose
        private String sunset;
        @SerializedName("uvIdx")
        @Expose
        private Integer uvIdx;
        @SerializedName("uvDes")
        @Expose
        private String uvDes;
        @SerializedName("uvWrn")
        @Expose
        private Integer uvWrn;
        @SerializedName("flsLkIdxF")
        @Expose
        private Integer flsLkIdxF;
        @SerializedName("flsLkIdxC")
        @Expose
        private Integer flsLkIdxC;
        @SerializedName("wxMan")
        @Expose
        private String wxMan;
        @SerializedName("_presIn")
        @Expose
        private Double presIn;
        @SerializedName("_snwDepCm")
        @Expose
        private Double snwDepCm;
        @SerializedName("_prcp24Cm")
        @Expose
        private Double prcp24Cm;
        @SerializedName("_prcp24Mm")
        @Expose
        private Double prcp24Mm;
        @SerializedName("_prcpYrMm")
        @Expose
        private Double prcpYrMm;
        @SerializedName("_prcpMTDMm")
        @Expose
        private Double prcpMTDMm;
        @SerializedName("_prcp2DyMm")
        @Expose
        private Double prcp2DyMm;
        @SerializedName("_prcp3DyMm")
        @Expose
        private Double prcp3DyMm;
        @SerializedName("_prcp7DyMm")
        @Expose
        private Double prcp7DyMm;
        @SerializedName("_snwYrCm")
        @Expose
        private Double snwYrCm;
        @SerializedName("_snw2DyCm")
        @Expose
        private Double snw2DyCm;
        @SerializedName("_snw3DyCm")
        @Expose
        private Double snw3DyCm;
        @SerializedName("_snw7DyCm")
        @Expose
        private Double snw7DyCm;
        @SerializedName("_sunriseISOLocal")
        @Expose
        private String sunriseISOLocal;
        @SerializedName("_sunsetISOLocal")
        @Expose
        private String sunsetISOLocal;
        @SerializedName("obsDateTimeISO")
        @Expose
        private String obsDateTimeISO;
        @SerializedName("sunriseISO")
        @Expose
        private String sunriseISO;
        @SerializedName("sunsetISO")
        @Expose
        private String sunsetISO;
        @SerializedName("_obsDateLocalTimeISO")
        @Expose
        private String obsDateLocalTimeISO;
        @SerializedName("_wDirAsc_en")
        @Expose
        private String wDirAscEn;

        public String getStnNm() {
            return stnNm;
        }

        public void setStnNm(String stnNm) {
            this.stnNm = stnNm;
        }

        public String getObsDayGmt() {
            return obsDayGmt;
        }

        public void setObsDayGmt(String obsDayGmt) {
            this.obsDayGmt = obsDayGmt;
        }

        public String getObsTmGmt() {
            return obsTmGmt;
        }

        public void setObsTmGmt(String obsTmGmt) {
            this.obsTmGmt = obsTmGmt;
        }

        public String getDyNght() {
            return dyNght;
        }

        public void setDyNght(String dyNght) {
            this.dyNght = dyNght;
        }

        public String getLocObsDay() {
            return locObsDay;
        }

        public void setLocObsDay(String locObsDay) {
            this.locObsDay = locObsDay;
        }

        public String getLocObsTm() {
            return locObsTm;
        }

        public void setLocObsTm(String locObsTm) {
            this.locObsTm = locObsTm;
        }

        public Integer getTmpF() {
            return tmpF;
        }

        public void setTmpF(Integer tmpF) {
            this.tmpF = tmpF;
        }

        public Integer getTmpC() {
            return tmpC;
        }

        public void setTmpC(Integer tmpC) {
            this.tmpC = tmpC;
        }

        public Integer getSky() {
            return sky;
        }

        public void setSky(Integer sky) {
            this.sky = sky;
        }

        public String getWx() {
            return wx;
        }

        public void setWx(String wx) {
            this.wx = wx;
        }

        public Integer getIconExt() {
            return iconExt;
        }

        public void setIconExt(Integer iconExt) {
            this.iconExt = iconExt;
        }

        public Double getAlt() {
            return alt;
        }

        public void setAlt(Double alt) {
            this.alt = alt;
        }

        public Integer getBaroTrnd() {
            return baroTrnd;
        }

        public void setBaroTrnd(Integer baroTrnd) {
            this.baroTrnd = baroTrnd;
        }

        public String getBaroTrndAsc() {
            return baroTrndAsc;
        }

        public void setBaroTrndAsc(String baroTrndAsc) {
            this.baroTrndAsc = baroTrndAsc;
        }

        public String getClds() {
            return clds;
        }

        public void setClds(String clds) {
            this.clds = clds;
        }

        public Integer getDwptF() {
            return dwptF;
        }

        public void setDwptF(Integer dwptF) {
            this.dwptF = dwptF;
        }

        public Integer getDwptC() {
            return dwptC;
        }

        public void setDwptC(Integer dwptC) {
            this.dwptC = dwptC;
        }

        public Integer getHIF() {
            return hIF;
        }

        public void setHIF(Integer hIF) {
            this.hIF = hIF;
        }

        public Integer getHIC() {
            return hIC;
        }

        public void setHIC(Integer hIC) {
            this.hIC = hIC;
        }

        public Integer getRH() {
            return rH;
        }

        public void setRH(Integer rH) {
            this.rH = rH;
        }

        public Double getPres() {
            return pres;
        }

        public void setPres(Double pres) {
            this.pres = pres;
        }

        public Double getPresChnge() {
            return presChnge;
        }

        public void setPresChnge(Double presChnge) {
            this.presChnge = presChnge;
        }

        public Double getVisM() {
            return visM;
        }

        public void setVisM(Double visM) {
            this.visM = visM;
        }

        public Double getVisK() {
            return visK;
        }

        public void setVisK(Double visK) {
            this.visK = visK;
        }

        public Integer getWCF() {
            return wCF;
        }

        public void setWCF(Integer wCF) {
            this.wCF = wCF;
        }

        public Integer getWCC() {
            return wCC;
        }

        public void setWCC(Integer wCC) {
            this.wCC = wCC;
        }

        public Integer getWDir() {
            return wDir;
        }

        public void setWDir(Integer wDir) {
            this.wDir = wDir;
        }

        public String getWDirAsc() {
            return wDirAsc;
        }

        public void setWDirAsc(String wDirAsc) {
            this.wDirAsc = wDirAsc;
        }

        public Integer getWSpdM() {
            return wSpdM;
        }

        public void setWSpdM(Integer wSpdM) {
            this.wSpdM = wSpdM;
        }

        public Integer getWSpdK() {
            return wSpdK;
        }

        public void setWSpdK(Integer wSpdK) {
            this.wSpdK = wSpdK;
        }

        public Integer getWSpdKn() {
            return wSpdKn;
        }

        public void setWSpdKn(Integer wSpdKn) {
            this.wSpdKn = wSpdKn;
        }

        public Integer getTmpMx24F() {
            return tmpMx24F;
        }

        public void setTmpMx24F(Integer tmpMx24F) {
            this.tmpMx24F = tmpMx24F;
        }

        public Integer getTmpMx24C() {
            return tmpMx24C;
        }

        public void setTmpMx24C(Integer tmpMx24C) {
            this.tmpMx24C = tmpMx24C;
        }

        public Integer getTmpMn24F() {
            return tmpMn24F;
        }

        public void setTmpMn24F(Integer tmpMn24F) {
            this.tmpMn24F = tmpMn24F;
        }

        public Integer getTmpMn24C() {
            return tmpMn24C;
        }

        public void setTmpMn24C(Integer tmpMn24C) {
            this.tmpMn24C = tmpMn24C;
        }

        public Double getPrcp24() {
            return prcp24;
        }

        public void setPrcp24(Double prcp24) {
            this.prcp24 = prcp24;
        }

        public Double getPrcp36hr() {
            return prcp36hr;
        }

        public void setPrcp36hr(Double prcp36hr) {
            this.prcp36hr = prcp36hr;
        }

        public Double getPrcpHr() {
            return prcpHr;
        }

        public void setPrcpHr(Double prcpHr) {
            this.prcpHr = prcpHr;
        }

        public Double getPrcpMTD() {
            return prcpMTD;
        }

        public void setPrcpMTD(Double prcpMTD) {
            this.prcpMTD = prcpMTD;
        }

        public Double getPrcpYr() {
            return prcpYr;
        }

        public void setPrcpYr(Double prcpYr) {
            this.prcpYr = prcpYr;
        }

        public Double getPrcp2Dy() {
            return prcp2Dy;
        }

        public void setPrcp2Dy(Double prcp2Dy) {
            this.prcp2Dy = prcp2Dy;
        }

        public Double getPrcp3Dy() {
            return prcp3Dy;
        }

        public void setPrcp3Dy(Double prcp3Dy) {
            this.prcp3Dy = prcp3Dy;
        }

        public Double getPrcp7Dy() {
            return prcp7Dy;
        }

        public void setPrcp7Dy(Double prcp7Dy) {
            this.prcp7Dy = prcp7Dy;
        }

        public Double getSnwDep() {
            return snwDep;
        }

        public void setSnwDep(Double snwDep) {
            this.snwDep = snwDep;
        }

        public Double getSnwIncr() {
            return snwIncr;
        }

        public void setSnwIncr(Double snwIncr) {
            this.snwIncr = snwIncr;
        }

        public Double getSnwTot() {
            return snwTot;
        }

        public void setSnwTot(Double snwTot) {
            this.snwTot = snwTot;
        }

        public Double getSnwTot6hr() {
            return snwTot6hr;
        }

        public void setSnwTot6hr(Double snwTot6hr) {
            this.snwTot6hr = snwTot6hr;
        }

        public Double getSnwMTD() {
            return snwMTD;
        }

        public void setSnwMTD(Double snwMTD) {
            this.snwMTD = snwMTD;
        }

        public Double getSnwSsn() {
            return snwSsn;
        }

        public void setSnwSsn(Double snwSsn) {
            this.snwSsn = snwSsn;
        }

        public Double getSnwYr() {
            return snwYr;
        }

        public void setSnwYr(Double snwYr) {
            this.snwYr = snwYr;
        }

        public Double getSnw2Dy() {
            return snw2Dy;
        }

        public void setSnw2Dy(Double snw2Dy) {
            this.snw2Dy = snw2Dy;
        }

        public Double getSnw3Dy() {
            return snw3Dy;
        }

        public void setSnw3Dy(Double snw3Dy) {
            this.snw3Dy = snw3Dy;
        }

        public Double getSnw7Dy() {
            return snw7Dy;
        }

        public void setSnw7Dy(Double snw7Dy) {
            this.snw7Dy = snw7Dy;
        }

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public Integer getUvIdx() {
            return uvIdx;
        }

        public void setUvIdx(Integer uvIdx) {
            this.uvIdx = uvIdx;
        }

        public String getUvDes() {
            return uvDes;
        }

        public void setUvDes(String uvDes) {
            this.uvDes = uvDes;
        }

        public Integer getUvWrn() {
            return uvWrn;
        }

        public void setUvWrn(Integer uvWrn) {
            this.uvWrn = uvWrn;
        }

        public Integer getFlsLkIdxF() {
            return flsLkIdxF;
        }

        public void setFlsLkIdxF(Integer flsLkIdxF) {
            this.flsLkIdxF = flsLkIdxF;
        }

        public Integer getFlsLkIdxC() {
            return flsLkIdxC;
        }

        public void setFlsLkIdxC(Integer flsLkIdxC) {
            this.flsLkIdxC = flsLkIdxC;
        }

        public String getWxMan() {
            return wxMan;
        }

        public void setWxMan(String wxMan) {
            this.wxMan = wxMan;
        }

        public Double getPresIn() {
            return presIn;
        }

        public void setPresIn(Double presIn) {
            this.presIn = presIn;
        }

        public Double getSnwDepCm() {
            return snwDepCm;
        }

        public void setSnwDepCm(Double snwDepCm) {
            this.snwDepCm = snwDepCm;
        }

        public Double getPrcp24Cm() {
            return prcp24Cm;
        }

        public void setPrcp24Cm(Double prcp24Cm) {
            this.prcp24Cm = prcp24Cm;
        }

        public Double getPrcp24Mm() {
            return prcp24Mm;
        }

        public void setPrcp24Mm(Double prcp24Mm) {
            this.prcp24Mm = prcp24Mm;
        }

        public Double getPrcpYrMm() {
            return prcpYrMm;
        }

        public void setPrcpYrMm(Double prcpYrMm) {
            this.prcpYrMm = prcpYrMm;
        }

        public Double getPrcpMTDMm() {
            return prcpMTDMm;
        }

        public void setPrcpMTDMm(Double prcpMTDMm) {
            this.prcpMTDMm = prcpMTDMm;
        }

        public Double getPrcp2DyMm() {
            return prcp2DyMm;
        }

        public void setPrcp2DyMm(Double prcp2DyMm) {
            this.prcp2DyMm = prcp2DyMm;
        }

        public Double getPrcp3DyMm() {
            return prcp3DyMm;
        }

        public void setPrcp3DyMm(Double prcp3DyMm) {
            this.prcp3DyMm = prcp3DyMm;
        }

        public Double getPrcp7DyMm() {
            return prcp7DyMm;
        }

        public void setPrcp7DyMm(Double prcp7DyMm) {
            this.prcp7DyMm = prcp7DyMm;
        }

        public Double getSnwYrCm() {
            return snwYrCm;
        }

        public void setSnwYrCm(Double snwYrCm) {
            this.snwYrCm = snwYrCm;
        }

        public Double getSnw2DyCm() {
            return snw2DyCm;
        }

        public void setSnw2DyCm(Double snw2DyCm) {
            this.snw2DyCm = snw2DyCm;
        }

        public Double getSnw3DyCm() {
            return snw3DyCm;
        }

        public void setSnw3DyCm(Double snw3DyCm) {
            this.snw3DyCm = snw3DyCm;
        }

        public Double getSnw7DyCm() {
            return snw7DyCm;
        }

        public void setSnw7DyCm(Double snw7DyCm) {
            this.snw7DyCm = snw7DyCm;
        }

        public String getSunriseISOLocal() {
            return sunriseISOLocal;
        }

        public void setSunriseISOLocal(String sunriseISOLocal) {
            this.sunriseISOLocal = sunriseISOLocal;
        }

        public String getSunsetISOLocal() {
            return sunsetISOLocal;
        }

        public void setSunsetISOLocal(String sunsetISOLocal) {
            this.sunsetISOLocal = sunsetISOLocal;
        }

        public String getObsDateTimeISO() {
            return obsDateTimeISO;
        }

        public void setObsDateTimeISO(String obsDateTimeISO) {
            this.obsDateTimeISO = obsDateTimeISO;
        }

        public String getSunriseISO() {
            return sunriseISO;
        }

        public void setSunriseISO(String sunriseISO) {
            this.sunriseISO = sunriseISO;
        }

        public String getSunsetISO() {
            return sunsetISO;
        }

        public void setSunsetISO(String sunsetISO) {
            this.sunsetISO = sunsetISO;
        }

        public String getObsDateLocalTimeISO() {
            return obsDateLocalTimeISO;
        }

        public void setObsDateLocalTimeISO(String obsDateLocalTimeISO) {
            this.obsDateLocalTimeISO = obsDateLocalTimeISO;
        }

        public String getWDirAscEn() {
            return wDirAscEn;
        }

        public void setWDirAscEn(String wDirAscEn) {
            this.wDirAscEn = wDirAscEn;
        }

    }


    public class MOHdr {
        @SerializedName("obsStn")
        @Expose
        private String obsStn;
        @SerializedName("procTm")
        @Expose
        private long procTm;
        @SerializedName("_procTmLocal")
        @Expose
        private String procTmLocal;
        @SerializedName("procTmISO")
        @Expose
        private String procTmISO;

        public String getObsStn() {
            return obsStn;
        }

        public void setObsStn(String obsStn) {
            this.obsStn = obsStn;
        }

        public long getProcTm() {
            return procTm;
        }

        public void setProcTm(Integer procTm) {
            this.procTm = procTm;
        }

        public String getProcTmLocal() {
            return procTmLocal;
        }

        public void setProcTmLocal(String procTmLocal) {
            this.procTmLocal = procTmLocal;
        }

        public String getProcTmISO() {
            return procTmISO;
        }

        public void setProcTmISO(String procTmISO) {
            this.procTmISO = procTmISO;
        }
    }

}