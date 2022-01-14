/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extraClasses;

/**
 *
 * @author KL_Schweizer
 */
public class ChartData {
    private String series;
    private String category;
    private Double value;

    public ChartData(String series, String category, Double value) {
        this.series = series;
        this.category = category;
        this.value = value;
    }

 
    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
    
}
