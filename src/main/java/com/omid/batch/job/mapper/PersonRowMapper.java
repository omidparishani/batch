package com.omid.batch.job.mapper;

import com.omid.entity.Person;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

public class PersonRowMapper  implements RowMapper<Person> {
    @Override
    public Person mapRow(RowSet rowSet) {
        Person person=new Person();
        person.setFirstName(getRowSetValue(RowSetValue.FIRST_NAME, rowSet));
        person.setLastName(getRowSetValue(RowSetValue.LAST_NAME, rowSet));
        return person;
    }

    protected String getRowSetValue(RowValue rowSetValue, RowSet rowSet) {

        try {
            String value =rowSet.getCurrentRow()[rowSetValue.getValue()];
            Integer endIndex;
            endIndex = value.length() < rowSetValue.getLength() ? value.length() : rowSetValue.getLength();
            String str = rowSet.getCurrentRow()[rowSetValue.getValue()].substring(0, endIndex);
            return str.trim();
        } catch (Exception e) {
            return "";
        }
    }

    public enum RowSetValue implements RowValue {
        FIRST_NAME(0, 75, String.class, ""),
        LAST_NAME(1, 75, String.class, "");

        String defaultValue;
        private Integer value;
        private Integer length;
        private Class type;

        RowSetValue(Integer value, Integer length, Class type, String defaultValue) {
            this.value = value;
            this.length = length;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public Integer getLength() {
            return length;
        }

        @Override
        public Class getType() {
            return type;
        }
    }

    public interface RowValue {
        String getDefaultValue();

        Integer getValue();

        Integer getLength();

        Class getType();
    }
}
