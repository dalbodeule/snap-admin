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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import space.mori.dalbodeule.snapadmin.external.dto.CompareOperator;
import java.time.*;
import java.time.format.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class InstantFieldType extends DbFieldType {
    // 다양한 날짜/시간 형식을 처리할 수 있는 포맷터들
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };

    @Override
    public String getFragmentName() {
        return "datetime";
    }

    @Override
    public Object parseValue(Object value) {
        if (value == null) return null;
        
        // 이미 Instant 타입인 경우
        if (value instanceof Instant) {
            return value;
        }
        
        // LocalDateTime에서 Instant로 변환
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        }
        
        // Date에서 Instant로 변환
        if (value instanceof Date) {
            return ((Date) value).toInstant();
        }
        
        // LocalDate에서 Instant로 변환
        if (value instanceof LocalDate) {
            return ((LocalDate) value)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
        }
        
        // 문자열 처리
        String stringValue = value.toString();
        if (stringValue.isBlank()) return null;
        
        stringValue = stringValue.trim();
        
        // 직접 Instant 파싱 시도
        try {
            return Instant.parse(stringValue);
        } catch (DateTimeParseException e) {
            // 실패, 다른 방법으로 시도
        }
        
        // 다른 날짜/시간 형식을 통해 변환 시도
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                // ISO_INSTANT의 경우 Instant.parse()와 동일하므로 스킵
                if (formatter == DateTimeFormatter.ISO_INSTANT) {
                    continue;
                }
                
                // ISO_DATE_TIME 형식은 ZoneId가 필요
                if (formatter == DateTimeFormatter.ISO_DATE_TIME) {
                    return ZonedDateTime.parse(stringValue, formatter).toInstant();
                }
                
                // Z로 끝나는 형식은 UTC 시간으로 파싱
                if (stringValue.endsWith("Z")) {
                    return ZonedDateTime.parse(stringValue, formatter.withZone(ZoneOffset.UTC))
                        .toInstant();
                }
                
                // 기타 형식은 시스템 기본 시간대 사용
                TemporalAccessor ta = formatter.parse(stringValue);
                return LocalDateTime.from(ta)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            } catch (DateTimeParseException e) {
                // 다음 형식 시도
                continue;
            }
        }
        
        // 밀리초 타임스탬프로 시도
        try {
            long timestamp = Long.parseLong(stringValue);
            return Instant.ofEpochMilli(timestamp);
        } catch (NumberFormatException e) {
            // 숫자 파싱 실패, 무시
        }
        
        // 모든 파싱 시도 실패
        return null;
    }

    @Override
    public Class<?> getJavaClass() {
        return Instant.class;
    }
    
    @Override
    public List<CompareOperator> getCompareOperators() {
        return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
    }
}