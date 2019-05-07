package com.smartupds.thesaurusbuilder.commons;

/** Common resources
 * 
 * @author Yannis Marketakis
 */
public class Common {
    public static final String GEONAMES_IRI_PREFIX="http://sws.geonames.org/";
    public static final String GEONAMES_ONTOLOGY_NS="http://www.geonames.org/ontology#";
    public static final String GEO_POSITIONING_ONTOLOGY_NS="http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String RDFS_NS="http://www.w3.org/2000/01/rdf-schema#";
    public static final String GEONAMES_CLASS_FEATURE=GEONAMES_ONTOLOGY_NS+"Feature";
    public static final String GEONAMES_PROPERTY_NAME=GEONAMES_ONTOLOGY_NS+"name";
    public static final String GEONAMES_PROPERTY_LATITUDE=GEO_POSITIONING_ONTOLOGY_NS+"lat";
    public static final String GEONAMES_PROPERTY_LONGITUDE=GEO_POSITIONING_ONTOLOGY_NS+"long";
    public static final String GEONAMES_PROPERTY_COUNTRY_CODE=GEONAMES_ONTOLOGY_NS+"countryCode";
    public static final String GEONAMES_PROPERTY_PARENT_COUNTRY=GEONAMES_ONTOLOGY_NS+"parentCountry";
    public static final String RDFS_PROPERTY_LABEL=RDFS_NS+"label";
    
    public static final int EXPORT_MAXIMUM_FILESIZE=15000000;
    public static final String EXPORT_FILE_EXTENSION="trig";
}