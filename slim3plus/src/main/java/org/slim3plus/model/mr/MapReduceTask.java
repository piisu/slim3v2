package org.slim3plus.model.mr;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class MapReduceTask<M> {

    public boolean isKeyOnly() {
        return false;
    }

    public String getKind(Map<String, Object> params) {
        return getModelMeta(params).getKind();
    }

    /**
     * 分割用のキーを取得する。(|A|B|C|のように分割する場合、分割点は|の数分なので分割数+1の分割点数となる)
     *
     * @param shardCount 　分割数。分割数+1分のキーが必要。
     * @param params
     * @return
     */
    public List<Key> getSplitKeys(int shardCount, Map<String, Object> params) {
        String kindName = getKind(params);
        int scatterPointNum = shardCount - 1;
        List<Key> keys = Datastore
                .query(kindName)
                .sort(Entity.SCATTER_RESERVED_PROPERTY,
                        Query.SortDirection.ASCENDING)
                .limit(scatterPointNum)
                .asKeyList();

        Collections.sort(keys);
        keys.add(0, null);
        keys.add(null);
        return keys;
    }

    public abstract ModelMeta<M> getModelMeta(Map<String, Object> params);

    /**
     * @param mapWorker
     * @param key
     * @param model
     * @return　処理を継続する場合はtrueを返す
     */
    public abstract boolean process(MapWorker mapWorker, Key key, M model);

    public abstract void onComplete(MapReduce mapReduce);

    /**
     * ひとまとまりの処理が終わったときに呼ばれる
     *
     * @param worker
     */
    public void onCompleteWorkerBatch(MapWorker worker) {
    }
}
