package com.smartupds.thesaurusbuilder.model;

import com.smartupds.thesaurusbuilder.commons.Common;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

/** POJO for entries from GeoNames.
 * 
 * @author Yannis Marketakis 
 */
@Data
public class GeoNameEntry {
    private String code;
    private String name;
    private String longitude;
    private String latitude;
    private String countryCodeIso;
    private String country;
    private String countryCode;
    private String continentCode;
    private String continent;
    
    private static final List<Pair<String,String>> continents=Arrays.asList(
            Pair.of("AF", "Africa"),
            Pair.of("AN", "Antarctiva"),
            Pair.of("AS", "Asia"),
            Pair.of("EU", "Europe"),
            Pair.of("NA", "North America"),
            Pair.of("OC", "Oceania"),
            Pair.of("SA", "South America")
    );
    
    public String getEntryIri(){
        return Common.GEONAMES_IRI_PREFIX+this.code;
    }
    
    public String getCountryIri(){
        return Common.GEONAMES_IRI_PREFIX+this.countryCode;
    }
    
    public void setContinentCode(String continentCode){
        this.continentCode=continentCode;
        this.continent=continents.stream().filter(pair -> pair.getLeft().equalsIgnoreCase(continentCode)).findFirst().get().getRight();
    }
    
    public String toTrig(){
        StringBuilder trigBuilder=new StringBuilder();
        trigBuilder.append("<").append(this.getEntryIri()).append(">\n")
                   .append("\t{\n")
                   .append("\t<").append(this.getEntryIri()).append("> a <").append(Common.GEONAMES_CLASS_FEATURE).append(">;\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_NAME).append("> \"").append(this.getName()).append("\";\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_LATITUDE).append("> \"").append(this.getLatitude()).append("\";\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_LONGITUDE).append("> \"").append(this.getLongitude()).append("\";\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_PARENT_COUNTRY).append("> <").append(this.getCountryIri()).append(">;\n")
                   .append("\t\t<").append(Common.RDFS_PROPERTY_LABEL).append("> \"")
                                                                      .append(this.getContinent())
                                                                      .append(", ")
                                                                      .append(this.getCountry())
                                                                      .append(", ")
                                                                      .append(this.getName()).append("\".\n ")
                   .append("\n")
                   .append("\t<").append(this.getCountryIri()).append("> <").append(Common.GEONAMES_PROPERTY_NAME).append("> \"").append(this.country).append("\".\n")
                   .append("\t}");
        return trigBuilder.toString();
    }
}
