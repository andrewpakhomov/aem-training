/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol.properiesdump.jcr;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;

/**
 *
 * @author Andrey_Pakhomov
 */
public class PropertyDto {

    private HashMap<String, Object> data = new HashMap<>();
    private final ValueFactory valueFactory;

    private PropertyDto(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
    }

    public static PropertyDto transform(HashMap<String, Object> properties, ValueFactory valueFactory) throws DtoTransformException {
        PropertyDto instance = new PropertyDto(valueFactory);
        for (PropertyDtoFields field : PropertyDtoFields.values()) {
            String fieldKey = field.toString();
            if (!properties.containsKey(fieldKey)) {
                throw new DtoTransformException("Bad input hashmap format, no " + fieldKey + " field");
            }
            instance.data.put(fieldKey, properties.get(fieldKey));
        }
        return instance;
    }

    public static PropertyDto transorm(ValueFactory valueFactory, String propertyPath, Value propertyValue) throws DtoTransformException {
        PropertyDto instance = new PropertyDto(valueFactory);
        instance.data.put(PropertyDtoFields.PROPERTY_PATH.toString(), propertyPath);
        instance.data.put(PropertyDtoFields.PROPERTY_TYPE.toString(), propertyValue.getType());
        instance.data.put(PropertyDtoFields.IS_PROPERTY_MULTIPLE.toString(), false);
        Serializable value = convertValueToSerializable(propertyValue);
        instance.data.put(PropertyDtoFields.PROPERTY_VALUE.toString(), value);
        return instance;
    }

    public static PropertyDto transorm(ValueFactory valueFactory, String propertyPath, Value[] values) throws DtoTransformException {
        PropertyDto instance = new PropertyDto(valueFactory);
        instance.data.put(PropertyDtoFields.PROPERTY_PATH.toString(), propertyPath);
        //here is asumption, that each value is same
        instance.data.put(PropertyDtoFields.PROPERTY_TYPE.toString(), values[0].getType());
        instance.data.put(PropertyDtoFields.IS_PROPERTY_MULTIPLE.toString(), false);
        Serializable value = convertValueArrayToSerializableArray(values);
        instance.data.put(PropertyDtoFields.PROPERTY_VALUE.toString(), value);
        return instance;
    }

    public String getPropertyPath() {
        return (String) this.data.get(PropertyDtoFields.PROPERTY_PATH.toString());
    }

    public Value getPropertyValue() throws DtoTransformException {
        if (this.isMultuple()){
            throw new RuntimeException("Trying to get array of value on non array dto");
        }
        int propertyType = (Integer)this.data.get(PropertyDtoFields.PROPERTY_TYPE.toString());
        Serializable propertyValue = (Serializable) this.data.get(PropertyDtoFields.PROPERTY_VALUE.toString());
        return this.convertSerializableToValue(propertyValue, propertyType);
    }

    public Value[] getPropertyValues() throws DtoTransformException{
        if (! this.isMultuple()) {
            throw new RuntimeException("Trying to get array of value on non array dto");
        }
        final int propertyType = (Integer)this.data.get(PropertyDtoFields.PROPERTY_TYPE.toString());
        Serializable[] valuesData = (Serializable[]) this.data.get(PropertyDtoFields.PROPERTY_VALUE.toString());
        Value[] result = new Value[valuesData.length];
        for (int i = 0; i < valuesData.length; i++){
            result[i] = this.convertSerializableToValue(valuesData[i], propertyType);
        }
        return result;
    }

    public boolean isMultuple() {
        return (Boolean) this.data.get(PropertyDtoFields.IS_PROPERTY_MULTIPLE.toString());
    }

    public Map<String, Object> serializeToJobProperies() {
        return new HashMap<>(this.data);
    }
    
    public HashMap<String, Object> getSlingJobPayload(){
        return new HashMap<>(this.data);
    }

    private static Serializable convertValueArrayToSerializableArray(Value[] values) throws DtoTransformException {
        int valueArrayLength = values.length;
        Serializable[] result = new Serializable[valueArrayLength];
        for (int i = 0; i < valueArrayLength; i++) {
            result[i] = convertValueToSerializable(values[i]);
        }
        return result;
    }

    private static Serializable convertValueToSerializable(Value value) throws DtoTransformException {
        Serializable result = null;
        try {

            switch (value.getType()) {
                case PropertyType.BINARY: {
                    try {
                        Binary binary = value.getBinary();
                        InputStream binaryInputStream = binary.getStream();
                        result = ByteStreams.toByteArray(binaryInputStream);
                        binary.dispose();
                    } catch (IOException ex) {
                        throw new DtoTransformException("error during converting binary property value to serializable form", ex);
                    }
                    break;
                }
                case PropertyType.BOOLEAN: {
                    result = value.getBoolean();
                    break;
                }
                case PropertyType.DATE: {
                    result = value.getDate();
                    break;
                }
                case PropertyType.DECIMAL: {
                    result = value.getDecimal();
                    break;
                }
                case PropertyType.DOUBLE: {
                    result = value.getDouble();
                    break;
                }
                case PropertyType.LONG: {
                    result = value.getLong();
                    break;
                }
                case PropertyType.STRING: {
                    result = value.getString();
                    break;
                }
                default:
                    throw new DtoTransformException("Unsupported value type:" + value.getType());
            }
        } catch (RepositoryException ex) {
            throw new DtoTransformException("error during getting value's value", ex);
        }
        return result;
    }

    private Value convertSerializableToValue(Serializable value, int type) throws DtoTransformException {
        Value result;
        try{
        switch (type) {
            case PropertyType.BINARY: {
                ByteArrayInputStream inpuStream = new ByteArrayInputStream((byte[]) value);
                Binary binary =  this.valueFactory.createBinary(inpuStream);
                return this.valueFactory.createValue(binary);
            }
            case PropertyType.BOOLEAN: {
                return this.valueFactory.createValue((Boolean) value);
            }
            case PropertyType.DATE: {
                return this.valueFactory.createValue((Calendar) value);
            }
            case PropertyType.DECIMAL: {
                return this.valueFactory.createValue((BigDecimal) value);
            }
            case PropertyType.DOUBLE: {
                return this.valueFactory.createValue((Double) value);
            }
            case PropertyType.LONG: {
                return this.valueFactory.createValue((Long) value);
            }
            case PropertyType.STRING: {
                return this.valueFactory.createValue((String) value);
            }
        }
        }catch (RepositoryException ex){
            throw new DtoTransformException("Value deserialiation exception" , ex);    
        }
         throw new DtoTransformException("Unsupported value type:" + type); 
    }

}
