package com.smartupds.thesaurusbuilder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.smartupds.thesaurusbuilder.commons.Common;
import com.smartupds.thesaurusbuilder.exception.ThesaurusBuilderException;
import com.smartupds.thesaurusbuilder.model.GeoNameEntry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.Pair;

/** KB Builder with cities from GeoNames
 * 
 * @author Yannis Marketakis (marketak 'at' ics 'dot' forth 'dot' gr)
 */
@Log4j
public class GeoNamesKBBuilder {
    private Multimap<String,GeoNameEntry> cities=HashMultimap.create();
    private Map<String,Pair<String,String>> countryInfo;
    private String geonamesDumpPath;
    private static final int INDEX_OF_CITIES_GEONAME_ID=0;
    private static final int INDEX_OF_CITIES_NAME=1;
    private static final int INDEX_OF_CITIES_LATUTUDE=4;
    private static final int INDEX_OF_CITIES_LONGITUDE=5;
    private static final int INDEX_OF_CITIES_COUNTRY_CODE=8;
    private static final int INDEX_OF_COUNTRY_ISO_CODE=0;
    private static final int INDEX_OF_COUNTRY_NAME=4;
    private static final int INDEX_OF_COUNTRY_CODE=16;
    private static int EXPORT_COUNTER=1;
    
    public GeoNamesKBBuilder(String countryInfoPath, String dumpPath) throws ThesaurusBuilderException{
        log.info("Initializing Geonames parse and KB Builder");
        try{
            this.countryInfo=this.loadCountriesInfo(countryInfoPath);
            this.geonamesDumpPath=dumpPath;
        }catch(IOException ex){
            log.error("An error occured while loading countries information",ex);
            throw new ThesaurusBuilderException("An error occured while loading countries information",ex);
        }
    }
    
    public Map<String,Pair<String,String>> loadCountriesInfo(String countriesInfoDumpPath) throws IOException{
        log.info("Loading information about countries");
        Map<String,Pair<String,String>> retMap=new HashMap<>();
        String line;
        BufferedReader br=new BufferedReader(new FileReader(new File(countriesInfoDumpPath)));
        while((line=br.readLine())!=null){
            if(!line.startsWith("#")){
                String[] countryTokens=line.split("\t");
                retMap.put(countryTokens[INDEX_OF_COUNTRY_ISO_CODE],
                           Pair.of(countryTokens[INDEX_OF_COUNTRY_NAME], countryTokens[INDEX_OF_COUNTRY_CODE]));
            }
        }
        return retMap;
    }
    
    public void parseDump() throws ThesaurusBuilderException {
        log.info("Parsing data about cities from GeoNames dump");
        try{
            String line;
            BufferedReader br=new BufferedReader(new FileReader(new File(this.geonamesDumpPath)));
            while((line=br.readLine())!=null){
                String[] cityTokens=line.split("\t");
                GeoNameEntry entry=new GeoNameEntry();
                entry.setCode(cityTokens[INDEX_OF_CITIES_GEONAME_ID]);
                entry.setName(cityTokens[INDEX_OF_CITIES_NAME].replace("\"", "\\\""));
                entry.setLatitude(cityTokens[INDEX_OF_CITIES_LATUTUDE]);
                entry.setLongitude(cityTokens[INDEX_OF_CITIES_LONGITUDE]);
                entry.setCountryCodeIso(cityTokens[INDEX_OF_CITIES_COUNTRY_CODE]);
                entry.setCountry(this.countryInfo.get(cityTokens[INDEX_OF_CITIES_COUNTRY_CODE]).getLeft());
                entry.setCountryCode(this.countryInfo.get(cityTokens[INDEX_OF_CITIES_COUNTRY_CODE]).getRight());
                this.cities.put(entry.getCode(), entry);
            }
        }catch(IOException ex){
            log.error("An error occured while parsing geonames dump",ex);
            throw new ThesaurusBuilderException("An error occured while parsing geonames dump",ex);
        }
    }
    
    public void exportDump(String filename) throws ThesaurusBuilderException{
        log.info("Exporting GeoNames data in TRIG format to file with name "+filename);
        StringBuilder dataBuilder=new StringBuilder();
        for(GeoNameEntry entry : this.cities.values()){
            dataBuilder.append(entry.toTrig())
                       .append("\n\n");
            dataBuilder=this.checkAndExport(dataBuilder,filename);
        }
    }
    
    public StringBuilder checkAndExport(StringBuilder dataBuilder, String filename) throws ThesaurusBuilderException{
        if(dataBuilder.length()>=Common.EXPORT_MAXIMUM_FILESIZE){
            try{
                BufferedWriter writer=new BufferedWriter(new FileWriter(new File(filename+"-"+(EXPORT_COUNTER++)+"."+Common.EXPORT_FILE_EXTENSION)));
                writer.append(dataBuilder.toString());
                writer.flush();
                writer.close();
            }catch(IOException ex){
                log.error("An error occured while exporting dump",ex);
                throw new ThesaurusBuilderException("An error occured while exporting dump",ex);
            }
            return new StringBuilder();
        }else{
            return dataBuilder;
        }
    }

    public static void main(String[] args) throws ThesaurusBuilderException{
        GeoNamesKBBuilder builder=new GeoNamesKBBuilder("countryInfo.txt","cities500.txt");
        builder.parseDump();
        builder.exportDump("geonames_cities");
    }
}