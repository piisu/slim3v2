package org.slim3plus.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;
import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.S3QueryResultList;
import org.slim3.util.StringUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by katsume on 2015/07/06.
 */
@Data
@NoArgsConstructor
public class QueryResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Data
    public static class Next implements Serializable {
        private static final long serialVersionUID = 1L;
        private String cursor;

        private String filter;

        private String sorts;

        public <T> QueryResult<T> asQueryResult(ModelMeta<T> meta, int limit) {
            return new QueryResult<>(asS3QueryResultList(meta, limit));
        }

        public <T> S3QueryResultList<T> asS3QueryResultList(ModelMeta<T> meta, int limit) {
            ModelQuery<T> q = Datastore.query(meta);
            if (!StringUtil.isEmpty(cursor)) {
                q.encodedStartCursor(cursor);
            }
            if (!StringUtil.isEmpty(filter)) {
                q.encodedFilter(filter);
            }
            if (!StringUtil.isEmpty(sorts)) {
                q.encodedSorts(sorts);
            }
            q.limit(limit);
            return q.asQueryResultList();
        }
    }

    List<T> list = Collections.emptyList();

    Next next;

    Map<String, Object> optional;

    public static QueryResult emptyList = new QueryResult<>();

    public QueryResult(S3QueryResultList<T> list) {
        this.list = list;
        if (list.hasNext()) {
            next = new Next();
            next.cursor = list.getEncodedCursor();
            next.filter = list.getEncodedFilter();
            next.sorts = list.getEncodedSorts();
        }
    }
}
