package indi.butterfly.core;

import org.springframework.util.Assert;

import java.util.*;

/**
 * 查询结果封装(一行)
 * 封装有 index(字段顺序) name(字段名称) value(字段值)
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.15
 */
public class QueryResultWrapper {

    private List<QueryResultElement> data = new LinkedList<>();

    public List<QueryResultElement> getData() {
        return data;
    }

    public QueryResultWrapper(List<QueryResultElement> data) {
        Assert.notNull(data, "error input");
        this.data = data;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (this.data != null)
            for (QueryResultElement element : data)
                map.put(element.getName(), element.getValue());
        return map;
    }

    public void setData(List<QueryResultElement> data) {
        this.data = data;
    }

    public static class QueryResultElement {
        private Integer index = 0;//从0开始

        private String name;

        private Object value;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public static QueryResultElement of (Integer index, String name, Object value) {
            QueryResultWrapper.QueryResultElement element = new QueryResultWrapper.QueryResultElement();
            element.setIndex(index);
            element.setName(name);
            element.setValue(value);
            return element;
        }
    }


}
