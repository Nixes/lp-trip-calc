package org.nixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trip {
    // Columns: Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status
    private LocalDateTime startedOnUTC;
    private LocalDateTime finishedOnUTC;
    private long durationSecs;
    private String fromStopId;
    private String toStopId;
    private BigDecimal chargeAmount;
    private String companyId;
    private String busID;
    private String primaryAccountNumber;
    private TripStatus status;

    public Trip(
            @NotNull LocalDateTime startedOnUTC,
            @NotNull LocalDateTime finishedOnUTC,
            long durationSecs,
            @NotNull String fromStopId,
            @NotNull String toStopId,
            @NotNull BigDecimal chargeAmount,
            @NotNull String companyId,
            @NotNull String busID,
            @NotNull String primaryAccountNumber,
            @NotNull TripStatus status
    ) {
        this.startedOnUTC = startedOnUTC;
        this.finishedOnUTC = finishedOnUTC;
        this.durationSecs = durationSecs;
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.chargeAmount = chargeAmount;
        this.companyId = companyId;
        this.busID = busID;
        this.primaryAccountNumber = primaryAccountNumber;
        this.status = status;
    }

    public LocalDateTime getStartedOnUTC() {
        return startedOnUTC;
    }

    public void setStartedOnUTC(LocalDateTime startedOnUTC) {
        this.startedOnUTC = startedOnUTC;
    }

    public LocalDateTime getFinishedOnUTC() {
        return finishedOnUTC;
    }

    public void setFinishedOnUTC(LocalDateTime finishedOnUTC) {
        this.finishedOnUTC = finishedOnUTC;
    }

    public long getDurationSecs() {
        return durationSecs;
    }

    public void setDurationSecs(long durationSecs) {
        this.durationSecs = durationSecs;
    }

    public String getFromStopId() {
        return fromStopId;
    }

    public void setFromStopId(String fromStopId) {
        this.fromStopId = fromStopId;
    }

    public String getToStopId() {
        return toStopId;
    }

    public void setToStopId(String toStopId) {
        this.toStopId = toStopId;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    public String getPrimaryAccountNumber() {
        return primaryAccountNumber;
    }

    public void setPrimaryAccountNumber(String primaryAccountNumber) {
        this.primaryAccountNumber = primaryAccountNumber;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }
}
