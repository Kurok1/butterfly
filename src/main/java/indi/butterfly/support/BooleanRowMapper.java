package indi.butterfly.support;

import org.springframework.jdbc.core.SingleColumnRowMapper;

/**
 * 布尔类型的rowMapper, 用于sql中判断存在
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.25
 * @since 1.0.0
 * @see org.springframework.jdbc.core.RowMapper
 * @see SingleColumnRowMapper
 */
public class BooleanRowMapper extends SingleColumnRowMapper<Boolean> {
}
