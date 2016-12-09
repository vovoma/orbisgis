/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.Format;
import net.opengis.wps._2_0.LiteralDataType.LiteralDataDomain;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.model.*;
import org.orbisgis.wpsservice.model.Enumeration;
import org.orbisgis.wpsservice.model.TranslatableString;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URI;
import java.util.*;

/**
 * Class able to convert annotation into object and object into annotation.
 *
 * @author Sylvain PALOMINOS
 **/

public class ObjectAnnotationConverter {

    public static void annotationToObject(DescriptionTypeAttribute descriptionTypeAttribute,
                                          DescriptionType descriptionType){
        List<LanguageStringType> titleList = new ArrayList<>();
        //The title attribute can't be empty.
        String[] titles = descriptionTypeAttribute.title();
        if(titles.length == 1){
            LanguageStringType string = new LanguageStringType();
            string.setValue(titles[0].trim());
            titleList.add(string);
        }
        else {
            for (int i = 0; i < titles.length; i += 2) {
                LanguageStringType string = new LanguageStringType();
                string.setValue(titles[i].trim());
                string.setLang(titles[i + 1]);
                titleList.add(string);
            }
        }
        descriptionType.getTitle().clear();
        descriptionType.getTitle().addAll(titleList);

        List<LanguageStringType> descriptionList = new ArrayList<>();

        String[] descriptions = descriptionTypeAttribute.description();

        if(descriptions.length == 1){
            LanguageStringType resume = new LanguageStringType();
            resume.setValue(descriptions[0].trim());
            descriptionList.add(resume);
        }
        else {
            for (int i = 0; i < descriptions.length; i += 2) {
                LanguageStringType resume = new LanguageStringType();
                resume.setValue(descriptions[i].trim());
                resume.setLang(descriptions[i + 1]);
                descriptionList.add(resume);
            }
        }
        descriptionType.getAbstract().clear();
        descriptionType.getAbstract().addAll(descriptionList);


        if(!descriptionTypeAttribute.identifier().isEmpty()){
            CodeType codeType = new CodeType();
            codeType.setValue(descriptionTypeAttribute.identifier().trim());
            descriptionType.setIdentifier(codeType);
        }

        String[] keywords = descriptionTypeAttribute.keywords();
        if(keywords.length == 1) {
            LinkedList<KeywordsType> keywordTypeList = new LinkedList<>();
            String[] split = keywords[0].split(",");
            for(String str : split){
                LanguageStringType keywordString = new LanguageStringType();
                keywordString.setValue(str.trim());

                List<LanguageStringType> keywordList = new ArrayList<>();
                keywordList.add(keywordString);

                KeywordsType keywordsType = new KeywordsType();
                keywordsType.getKeyword().addAll(keywordList);
                keywordTypeList.add(keywordsType);
            }
            descriptionType.getKeywords().clear();
            descriptionType.getKeywords().addAll(keywordTypeList);
        }
        else if(keywords.length != 0) {
            LinkedList<KeywordsType> keywordTypeList = new LinkedList<>();
            for(int i=0; i<keywords.length; i+=2){
                if(keywordTypeList.isEmpty()){
                    String language = keywords[i+1];
                    String[] split = keywords[i].split(",");
                    for(String str : split){
                        LanguageStringType keywordString = new LanguageStringType();
                        keywordString.setValue(str.trim());
                        keywordString.setLang(language);

                        List<LanguageStringType> keywordList = new ArrayList<>();
                        keywordList.add(keywordString);

                        KeywordsType keywordsType = new KeywordsType();
                        keywordsType.getKeyword().addAll(keywordList);
                        keywordTypeList.add(keywordsType);
                    }
                }
                else{
                    String language = keywords[i+1];
                    String[] split = keywords[i].split(",");
                    for(int j=0; j<split.length; j++){
                        String str = split[j];
                        LanguageStringType keywordString = new LanguageStringType();
                        keywordString.setValue(str.trim());
                        keywordString.setLang(language);

                        List<LanguageStringType> keywordList = keywordTypeList.get(j).getKeyword();
                        keywordList.add(keywordString);
                    }
                }
            }
            descriptionType.getKeywords().clear();
            descriptionType.getKeywords().addAll(keywordTypeList);
        }

        String[] metadata = descriptionTypeAttribute.metadata();
        if(metadata.length != 0){
            List<MetadataType> metadataList = new ArrayList<>();
            for(int i=0; i<metadata.length; i+=2){
                MetadataType metadataType = new MetadataType();
                metadataType.setRole(metadata[i]);
                metadataType.setTitle(metadata[i+1]);
                metadataList.add(metadataType);
            }
            descriptionType.getMetadata().clear();
            descriptionType.getMetadata().addAll(metadataList);
        }
    }

    public static Format annotationToObject(FormatAttribute formatAttribute){
        Format format = new Format();
        format.setMimeType(formatAttribute.mimeType());
        format.setSchema(URI.create(formatAttribute.schema()).toString());
        format.setDefault(formatAttribute.isDefaultFormat());
        if(formatAttribute.maximumMegaBytes() == 0) {
            format.setMaximumMegabytes(null);
        }
        else{
            format.setMaximumMegabytes(BigInteger.valueOf(formatAttribute.maximumMegaBytes()));
        }
        format.setEncoding(formatAttribute.encoding());
        return format;
    }

    public static MetadataType annotationToObject(MetadataAttribute descriptionTypeAttribute){
        URI href = URI.create(descriptionTypeAttribute.href());
        URI role = URI.create(descriptionTypeAttribute.role());
        String title = descriptionTypeAttribute.title();

        MetadataType metadata = new MetadataType();
        metadata.setHref(href.toString());
        metadata.setRole(role.toString());
        metadata.setTitle(title);

        return metadata;
    }

    public static Object annotationToObject(ValuesAttribute valueAttribute){
        if(valueAttribute.type().toUpperCase().equals(ValuesType.VALUE.name())){
            if(valueAttribute.value().equals(ValuesAttribute.defaultValue)){
                return null;
            }
            ValueType value = new ValueType();
            value.setValue(valueAttribute.value().trim());
            return value;
        }
        else if(valueAttribute.type().toUpperCase().equals(ValuesType.RANGE.name())){
            RangeType range = new RangeType();
            if(!valueAttribute.spacing().equals(ValuesAttribute.defaultSpacing)) {
                ValueType spacing = new ValueType();
                spacing.setValue(valueAttribute.spacing().trim());
                range.setSpacing(spacing);
            }
            if(!valueAttribute.maximum().equals(ValuesAttribute.defaultMaximum)){
                ValueType max = new ValueType();
                max.setValue(valueAttribute.maximum().trim());
                range.setMaximumValue(max);
            }
            if(!valueAttribute.minimum().equals(ValuesAttribute.defaultMinimum)){
                ValueType min = new ValueType();
                min.setValue(valueAttribute.minimum().trim());
                range.setMinimumValue(min);
            }
            return range;
        }
        return null;
    }

    public static LiteralDataDomain annotationToObject(LiteralDataDomainAttribute literalDataDomainAttribute){
        LiteralDataDomain literalDataDomain = new LiteralDataDomain();
        literalDataDomain.setDefault(literalDataDomainAttribute.isDefault());
        if(!literalDataDomainAttribute.uom().isEmpty()){
            DomainMetadataType uom = new DomainMetadataType();
            URI uomUri = URI.create(literalDataDomainAttribute.uom());
            uom.setValue(uomUri.getPath());
            uom.setReference(uomUri.toString());
            literalDataDomain.setUOM(uom);
        }
        DataType dataType = DataType.valueOf(literalDataDomainAttribute.dataType());
        DomainMetadataType domainDataType = new DomainMetadataType();
        domainDataType.setReference(dataType.getUri().toString());
        domainDataType.setValue(dataType.name().trim());
        literalDataDomain.setDataType(domainDataType);

        Object value = ObjectAnnotationConverter.annotationToObject(literalDataDomainAttribute.possibleLiteralValues());
        if(value instanceof AllowedValues){
            literalDataDomain.setAllowedValues((AllowedValues)value);
        }
        else if(value instanceof AnyValue){
            literalDataDomain.setAnyValue((AnyValue) value);
        }
        else if(value instanceof ValuesReference){
            literalDataDomain.setValuesReference((ValuesReference) value);
        }
        ValueType defaultValue = new ValueType();
        defaultValue.setValue(literalDataDomainAttribute.defaultValue().trim());
        literalDataDomain.setDefaultValue(defaultValue);

        return literalDataDomain;
    }

    public static LiteralDataType annotationToObject(LiteralDataAttribute literalDataAttribute,
                                                     LiteralDataDomain defaultDomain) {
        LiteralDataType literalDataType = new LiteralDataType();

        List<Format> formatList = new ArrayList<>();
        if(literalDataAttribute.formats().length == 0){
            formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
            formatList.get(0).setDefault(true);
        }
        else {
            boolean isDefault = false;
            for (FormatAttribute formatAttribute : literalDataAttribute.formats()) {
                Format format = ObjectAnnotationConverter.annotationToObject(formatAttribute);
                if(format.isDefault()){
                    isDefault = true;
                }
                formatList.add(format);
            }
            if(!isDefault){
                formatList.get(0).setDefault(true);
            }
        }
        literalDataType.getFormat().clear();
        literalDataType.getFormat().addAll(formatList);

        List<LiteralDataType.LiteralDataDomain> lddList = new ArrayList<>();
        if(literalDataAttribute.validDomains().length == 0){
            if(defaultDomain != null){
                lddList.add(defaultDomain);
            }
            else {
                LiteralDataDomain literalDataDomain = new LiteralDataDomain();
                literalDataDomain.setDefault(true);
                AnyValue anyValue = new AnyValue();
                literalDataDomain.setAnyValue(anyValue);
                ValueType defaultValue = new ValueType();
                defaultValue.setValue("");
                literalDataDomain.setDefaultValue(defaultValue);
                DomainMetadataType domainMetadataType = new DomainMetadataType();
                domainMetadataType.setReference(DataType.STRING.getUri().toString());
                domainMetadataType.setValue(DataType.STRING.name());
                literalDataDomain.setDataType(domainMetadataType);
                lddList.add(literalDataDomain);
            }
        }
        else {
            boolean isDefault = false;
            for (LiteralDataDomainAttribute literalDataDomainAttribute : literalDataAttribute.validDomains()) {
                LiteralDataDomain ldd = ObjectAnnotationConverter.annotationToObject(literalDataDomainAttribute);
                if(ldd.isDefault()){
                    isDefault = true;
                }
                lddList.add(ldd);
            }
            if(!isDefault){
                lddList.get(0).setDefault(true);
            }
        }
        literalDataType.getLiteralDataDomain().clear();
        literalDataType.getLiteralDataDomain().addAll(lddList);

        return literalDataType;
    }

    public static RawData annotationToObject(RawDataAttribute rawDataAttribute, Format format) {
        try {
            //Instantiate the RawData
            format.setDefault(true);
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            RawData rawData = new RawData(formatList);
            rawData.setFile(rawDataAttribute.isFile());
            rawData.setDirectory(rawDataAttribute.isDirectory());
            rawData.setMultiSelection(rawDataAttribute.multiSelection());
            rawData.setFileTypes(rawDataAttribute.fileTypes());
            rawData.setExcludedTypes(rawDataAttribute.excludedTypes());
            return rawData;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static void annotationToObject(InputAttribute inputAttribute, InputDescriptionType input){
        input.setMaxOccurs(""+inputAttribute.maxOccurs());
        input.setMinOccurs(BigInteger.valueOf(inputAttribute.minOccurs()));
    }

    public static Object annotationToObject(
            PossibleLiteralValuesChoiceAttribute possibleLiteralValuesChoiceAttribute){
        if(possibleLiteralValuesChoiceAttribute.anyValues() ||
                (possibleLiteralValuesChoiceAttribute.allowedValues().length != 0 &&
                        !possibleLiteralValuesChoiceAttribute.reference().isEmpty())){
            return new AnyValue();
        }
        else if(possibleLiteralValuesChoiceAttribute.allowedValues().length != 0){
            AllowedValues allowedValues = new AllowedValues();
            List<Object> valueList = new ArrayList<>();
            for(ValuesAttribute va : possibleLiteralValuesChoiceAttribute.allowedValues()){
                valueList.add(ObjectAnnotationConverter.annotationToObject(va));
            }
            allowedValues.getValueOrRange().clear();
            allowedValues.getValueOrRange().addAll(valueList);
            return allowedValues;
        }
        else if (!possibleLiteralValuesChoiceAttribute.reference().isEmpty()){
            ValuesReference valuesReference = new ValuesReference();
            URI uri = URI.create(possibleLiteralValuesChoiceAttribute.reference());
            valuesReference.setValue(uri.getPath());
            valuesReference.setReference(uri.toString());
            return valuesReference;
        }
        return new AnyValue();
    }

    public static void annotationToObject(ProcessAttribute processAttribute, ProcessOffering processOffering){
        processOffering.getProcess().setLang(Locale.forLanguageTag(processAttribute.language()).toString());
        processOffering.setProcessVersion(processAttribute.version());
        String[] properties = processAttribute.properties();
        List<MetadataType> metadataList = processOffering.getProcess().getMetadata();
        for(int i=0; i<properties.length; i+=2){
            MetadataType metadata = new MetadataType();
            metadata.setRole(properties[i]);
            metadata.setTitle(properties[i+1]);
            metadataList.add(metadata);
        }
    }

    public static DataStore annotationToObject(DataStoreAttribute dataStoreAttribute, List<Format> formatList) {
        try {
            DataStore dataStore = new DataStore(formatList);
            List<DataType> dataTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataStoreAttribute.dataStoreTypes())){
                dataTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            dataStore.setDataStoreTypeList(dataTypeList);
            List<DataType> excludedTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataStoreAttribute.excludedTypes())){
                excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            dataStore.setExcludedTypeList(excludedTypeList);
            return dataStore;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static DataField annotationToObject(DataFieldAttribute dataFieldAttribute, Format format, URI dataStoreUri) {
        try {
            format.setDefault(true);
            List<DataType> dataTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataFieldAttribute.fieldTypes())){
                dataTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<DataType> excludedTypeList = new ArrayList<>();
            for(String type : Arrays.asList(dataFieldAttribute.excludedTypes())){
                excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            DataField dataField = new DataField(formatList, dataTypeList, dataStoreUri);
            dataField.setExcludedTypeList(excludedTypeList);
            dataField.setMultiSelection(dataFieldAttribute.multiSelection());
            return dataField;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static FieldValue annotationToObject(FieldValueAttribute fieldvalueAttribute, Format format, URI dataFieldUri) {
        try {
            format.setDefault(true);
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            return new FieldValue(formatList, dataFieldUri, fieldvalueAttribute.multiSelection());
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static Enumeration annotationToObject(EnumerationAttribute enumAttribute, Format format) {
        try{
            format.setDefault(true);
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            Enumeration enumeration = new Enumeration(formatList, enumAttribute.values(), enumAttribute.selectedValues());
            enumeration.setEditable(enumAttribute.isEditable());
            enumeration.setMultiSelection(enumAttribute.multiSelection());

            String[] names = enumAttribute.names();
            if(names.length == 1) {
                String[] split = names[0].split(",");
                TranslatableString[] translatableStrings = new TranslatableString[split.length];
                for(int i=0; i<split.length; i++){
                    TranslatableString string = new TranslatableString();
                    LanguageStringType[] types = new LanguageStringType[1];
                    LanguageStringType type = new LanguageStringType();
                    type.setValue(split[i].trim());
                    types[0] = type;
                    string.setStrings(types);
                    translatableStrings[i] = string;
                }
                enumeration.setValuesNames(translatableStrings);
            }
            else if(names.length != 0) {
                TranslatableString[] translatableStrings = null;
                for(int i=0; i< names.length; i+=2) {
                    String language = names[i + 1];
                    String[] split = names[i].split(",");
                    if(translatableStrings == null) {
                        translatableStrings = new TranslatableString[split.length];
                    }
                    for (int j = 0; j < split.length; j++) {
                        TranslatableString string = translatableStrings[j];
                        if(string == null){
                            string = new TranslatableString();
                        }
                        LanguageStringType[] types = string.getStrings();
                        if(types == null || types.length == 0) {
                            types = new LanguageStringType[names.length/2];
                        }
                        LanguageStringType type = new LanguageStringType();
                        type.setValue(split[j].trim());
                        type.setLang(language);
                        types[i/2] = type;
                        string.setStrings(types);
                        translatableStrings[j] = string;
                    }
                }
                enumeration.setValuesNames(translatableStrings);
            }
            return enumeration;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }

    public static GeometryData annotationToObject(GeometryAttribute geometryAttribute, Format format) {
        try{
            format.setDefault(true);
            List<DataType> geometryTypeList = new ArrayList<>();
            //For each field type value from the groovy annotation, test if it is contain in the FieldType enumeration.
            for(String type : Arrays.asList(geometryAttribute.geometryTypes())){
                geometryTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<DataType> excludedTypeList = new ArrayList<>();
            //For each excluded type value from the groovy annotation, test if it is contain in the FieldType enumeration.
            for(String type : Arrays.asList(geometryAttribute.excludedTypes())){
                excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
            }
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            GeometryData geometryData = new GeometryData(formatList, geometryTypeList);
            geometryData.setDimension(geometryAttribute.dimension());
            geometryData.setExcludedTypeList(excludedTypeList);
            return geometryData;
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }
}
