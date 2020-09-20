package indi.butterfly.support;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期-字段映射解析
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.08.16
 */
public class LocalDateRowMapper implements RowMapper<LocalDate> {

    private final String FIELD_LABEL;

    private DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LocalDateRowMapper(String fieldLabel) {
        this.FIELD_LABEL = fieldLabel;
    }

    public LocalDateRowMapper(String fieldLabel, DateTimeFormatter defaultFormatter) {
        this.FIELD_LABEL = fieldLabel;
        this.defaultFormatter = defaultFormatter;
    }

    @Override
    public LocalDate mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (StringUtils.isEmpty(FIELD_LABEL))
            return null;

        String value = rs.getString(FIELD_LABEL);
        if (StringUtils.isEmpty(value))
            return null;

        LocalDate date = null;
        try {
            date = LocalDate.parse(value, defaultFormatter);
        } catch (DateTimeParseException ignored) {

        }
        return date;
    }
}
