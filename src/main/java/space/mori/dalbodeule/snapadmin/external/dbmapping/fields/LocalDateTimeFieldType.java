/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package space.mori.dalbodeule.snapadmin.external.dbmapping.fields;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import space.mori.dalbodeule.snapadmin.external.dto.CompareOperator;

public class LocalDateTimeFieldType extends DbFieldType {
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    @Override
    public String getFragmentName() {
        return "datetime";
    }

    @Override
    public Object parseValue(Object value) {
        if (value == null || value.toString().isBlank()) return null;

        // 이미 LocalDateTime 객체인 경우
        if (value instanceof LocalDateTime) {
            return value;
        }

        String stringValue = value.toString().trim();

        // 여러 형식으로 파싱 시도
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                // 날짜만 있는 형식인 경우 시간을 00:00:00으로 설정
                if (formatter.equals(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) {
                    return LocalDate.parse(stringValue, formatter).atStartOfDay();
                }
                return LocalDateTime.parse(stringValue, formatter);
            } catch (DateTimeParseException e) {
                // 이 형식으로 파싱 실패, 다음 형식 시도
                continue;
            }
        }

        // 모든 형식 파싱 실패 시 예외 발생
        throw new IllegalArgumentException("날짜/시간 형식을 파싱할 수 없습니다: " + stringValue);
    }

    @Override
    public Class<?> getJavaClass() {
        return LocalDateTime.class;
    }

    @Override
    public List<CompareOperator> getCompareOperators() {
        return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
    }
}