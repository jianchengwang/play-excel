/**
 * Copyright (c) 2018, biezhi (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.jianchengwang.playexcel.converter;

import cn.jianchengwang.playexcel.exception.ConverterException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class LocalDateConverter implements Converter<String, LocalDate> {

    private DateTimeFormatter formatter;

    public LocalDateConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDate stringToR(String value) throws ConverterException {
        try {
            if(null == value){
                return null;
            }
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            try {
                YearMonth ym = YearMonth.parse(value, formatter);
                return ym.atDay(1);
            } catch (Exception e2) {
                throw new ConverterException("convert [" + value + "] to LocalDate error", e2);
            }
        } catch (Exception e) {
            throw new ConverterException("convert [" + value + "] to LocalDate error", e);
        }
    }

    @Override
    public String toString(LocalDate localDate) {
        if(null == localDate){
            return null;
        }
        return localDate.format(formatter);
    }

}
