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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import space.mori.dalbodeule.snapadmin.external.dto.CompareOperator;

public class OffsetDateTimeFieldType extends DbFieldType {
	// 다양한 날짜/시간 형식을 처리할 수 있는 포맷터들
	private static final DateTimeFormatter[] FORMATTERS = {
			DateTimeFormatter.ISO_OFFSET_DATE_TIME,
			DateTimeFormatter.ISO_ZONED_DATE_TIME,
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX")
	};

	@Override
	public String getFragmentName() {
		return "datetime";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null) return null;

		// 이미 OffsetDateTime 타입인 경우
		if (value instanceof OffsetDateTime) {
			return value;
		}

		// ZonedDateTime에서 OffsetDateTime으로 변환
		if (value instanceof ZonedDateTime) {
			return ((ZonedDateTime) value).toOffsetDateTime();
		}

		// LocalDateTime에서 OffsetDateTime으로 변환 (시스템 기본 오프셋 사용)
		if (value instanceof LocalDateTime) {
			return ((LocalDateTime) value)
					.atZone(ZoneId.systemDefault())
					.toOffsetDateTime();
		}

		// Instant에서 OffsetDateTime으로 변환
		if (value instanceof Instant) {
			return ((Instant) value)
					.atZone(ZoneId.systemDefault())
					.toOffsetDateTime();
		}

		// LocalDate에서 OffsetDateTime으로 변환
		if (value instanceof LocalDate) {
			return ((LocalDate) value)
					.atStartOfDay()
					.atZone(ZoneId.systemDefault())
					.toOffsetDateTime();
		}

		// 문자열 처리
		String stringValue = value.toString();
		if (stringValue.isBlank()) return null;

		stringValue = stringValue.trim();

		// 직접 OffsetDateTime 파싱 시도
		try {
			return OffsetDateTime.parse(stringValue);
		} catch (DateTimeParseException e) {
			// 실패, 다른 방법으로 시도
		}

		// 여러 가지 형식으로 파싱 시도
		for (DateTimeFormatter formatter : FORMATTERS) {
			try {
				// ISO_OFFSET_DATE_TIME은 이미 위에서 시도했으므로 스킵
				if (formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME) {
					continue;
				}

				if (formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
					return ZonedDateTime.parse(stringValue, formatter).toOffsetDateTime();
				}

				return OffsetDateTime.parse(stringValue, formatter);
			} catch (DateTimeParseException e) {
				// 다음 형식 시도
				continue;
			}
		}

		// ISO 날짜/시간 형식에 시스템 기본 오프셋 추가 시도
		try {
			LocalDateTime ldt = LocalDateTime.parse(stringValue);
			return ldt.atZone(ZoneId.systemDefault()).toOffsetDateTime();
		} catch (DateTimeParseException e) {
			// 실패, 다음 방법 시도
		}

		// 날짜만 있는 경우 (UTC 자정으로 처리)
		try {
			LocalDate date = LocalDate.parse(stringValue);
			return date.atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime();
		} catch (DateTimeParseException e) {
			// 날짜 파싱 실패, 무시
		}

		// 밀리초 타임스탬프로 시도
		try {
			long timestamp = Long.parseLong(stringValue);
			return Instant.ofEpochMilli(timestamp)
					.atZone(ZoneId.systemDefault())
					.toOffsetDateTime();
		} catch (NumberFormatException e) {
			// 숫자 파싱 실패, 무시
		}

		// Z로 끝나는 UTC 시간 문자열 처리 시도
		if (stringValue.endsWith("Z")) {
			try {
				Instant instant = Instant.parse(stringValue);
				return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
			} catch (DateTimeParseException e) {
				// 실패, 무시
			}
		}

		// 모든 파싱 시도 실패
		return null;
	}

	@Override
	public Class<?> getJavaClass() {
		return OffsetDateTime.class;
	}

	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
	}
}