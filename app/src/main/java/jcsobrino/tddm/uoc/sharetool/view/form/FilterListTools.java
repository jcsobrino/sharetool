package jcsobrino.tddm.uoc.sharetool.view.form;

import java.util.Date;

import jcsobrino.tddm.uoc.sharetool.common.ToolOrderEnum;

/**
 * Created by JoséCarlos on 22/11/2015.
 */
public class FilterListTools {

    private Boolean distanceFilter;
    private Boolean priceFilter;
    private Boolean dateDaysFilter;

    private Float maxDistance;
    private Float maxPrice;
    private Date date;
    private Integer days;

    public ToolOrderEnum getToolOrderEnum() {
        return toolOrderEnum;
    }

    public void setToolOrderEnum(ToolOrderEnum toolOrderEnum) {
        this.toolOrderEnum = toolOrderEnum;
    }

    private ToolOrderEnum toolOrderEnum;

    private String toolName;


    public FilterListTools() {
        super();

        // Default filter values;
        distanceFilter = false;
        priceFilter = false;
        dateDaysFilter = false;
        toolOrderEnum = ToolOrderEnum.MIN_PRICE;

    }

    public Boolean getDistanceFilter() {
        return distanceFilter;
    }

    public void setDistanceFilter(Boolean distanceFilter) {
        this.distanceFilter = distanceFilter;
    }

    public Boolean getPriceFilter() {
        return priceFilter;
    }

    public void setPriceFilter(Boolean priceFilter) {
        this.priceFilter = priceFilter;
    }

    public Boolean getDateDaysFilter() {
        return dateDaysFilter;
    }

    public void setDateDaysFilter(Boolean dateDaysFilter) {
        this.dateDaysFilter = dateDaysFilter;
    }

    public Float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
}
