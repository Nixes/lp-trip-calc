package org.nixes;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class Tap {
    // ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN
    private String id;
    private LocalDateTime dateTimeUTC;
    private Boolean isTapOn;
    private String stopId;
    private String companyId;
    private String busId;
    private String primaryAccountNumber;

    public Tap(
            @NotNull String id,
            @NotNull LocalDateTime dateTimeUTC,
            boolean isTapOn,
            @NotNull String stopId,
            @NotNull String companyId,
            @NotNull String busId,
            @NotNull String primaryAccountNumber
    ) {
        this.id = id;
        this.dateTimeUTC = dateTimeUTC;
        this.isTapOn = isTapOn;
        this.stopId = stopId;
        this.companyId = companyId;
        this.busId = busId;
        this.primaryAccountNumber = primaryAccountNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDateTimeUTC() {
        return dateTimeUTC;
    }

    public void setDateTimeUTC(LocalDateTime dateTimeUTC) {
        this.dateTimeUTC = dateTimeUTC;
    }

    public Boolean getIsTapOn() {
        return isTapOn;
    }

    public void setIsTapOn(Boolean tapOn) {
        isTapOn = tapOn;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getPrimaryAccountNumber() {
        return primaryAccountNumber;
    }

    public void setPrimaryAccountNumber(String primaryAccountNumber) {
        this.primaryAccountNumber = primaryAccountNumber;
    }
}
