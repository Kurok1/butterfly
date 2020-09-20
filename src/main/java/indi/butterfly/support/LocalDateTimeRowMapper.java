package indi.butterfly.support;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 时间字段映射
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.08.16
 */
public class LocalDateTimeRowMapper implements RowMapper<LocalDateTime> {


    private final String FIELD_LABEL;

    private DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTimeRowMapper(String fieldLabel) {
        this.FIELD_LABEL = fieldLabel;
    }

    public LocalDateTimeRowMapper(String fieldLabel, DateTimeFormatter defaultFormatter) {
        this.FIELD_LABEL = fieldLabel;
        this.defaultFormatter = defaultFormatter;
    }

    @Override
    public LocalDateTime mapRow(ResultSet rs, int rowNum) throws SQLException {

        if (StringUtils.isEmpty(FIELD_LABEL))
            return null;

        String value = rs.getString(FIELD_LABEL);
        if (StringUtils.isEmpty(value))
            return null;
        LocalDateTime time = null;
        try {
            time = LocalDateTime.parse(value, defaultFormatter);
        } catch (DateTimeParseException ignored) {
        }
        return time;
    }
}
