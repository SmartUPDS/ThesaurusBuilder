package com.smartupds.thesaurusbuilder.model;

import com.smartupds.thesaurusbuilder.commons.Common;
import lombok.Data;

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
    
    public String getEntryIri(){
        return Common.GEONAMES_IRI_PREFIX+this.code;
    }
    
    public String getCountryIri(){
        return Common.GEONAMES_IRI_PREFIX+this.countryCode;
    }
    
    public String toTrig(){
        StringBuilder trigBuilder=new StringBuilder();
        trigBuilder.append("<").append(this.getEntryIri()).append(">\n")
                   .append("\t{\n")
                   .append("\t<").append(this.getEntryIri()).append("> a <").append(Common.GEONAMES_CLASS_FEATURE).append(">;\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_NAME).append("> \"").append(this.getName()).append("\";\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_LATITUDE).append("> \"").append(this.getLatitude()).append("\";\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_LONGITUDE).append("> \"").append(this.getLongitude()).append("\";\n")
                   .append("\t\t<").append(Common.GEONAMES_PROPERTY_PARENT_COUNTRY).append("> <").append(this.getCountryIri()).append(">.\n")
                   .append("\n")
                   .append("\t<").append(this.getCountryIri()).append("> <").append(Common.GEONAMES_PROPERTY_NAME).append("> \"").append(this.country).append("\".\n")
                   .append("\t}");
        return trigBuilder.toString();
    }
}
