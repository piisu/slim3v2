package org.slim3plus.service;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.taskqueue.TaskHandle;

import com.google.inject.Injector;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.DatastoreUtil;
import org.slim3.datastore.EntityQuery;
import org.slim3.datastore.ModelMeta;
import org.slim3plus.meta.mr.MapReduceMeta;
import org.slim3plus.model.mr.KeyRange;
import org.slim3plus.model.mr.MapReduce;
import org.slim3plus.model.mr.MapReduceTask;
import org.slim3plus.model.mr.MapWorker;
import org.slim3plus.model.mr.MapperContext;
import org.slim3plus.tq.Deferred;
import org.slim3plus.tx.Tx;
import org.slim3plus.util.QueryResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Singleton
public class MapReduceService {

    @Inject
    Injector injector;

    public QueryResult<MapReduce> list(QueryResult.Next next) {
        MapReduceMeta meta = MapReduceMeta.get();
        if (next != null) {
            return next.asQueryResult(meta, 20);
        } else {
            return new QueryResult<>(Datastore.query(meta).limit(20)
                    .sort(meta.startDate.desc).asQueryResultList());
        }
    }

    public MapReduce schedule(Class<? extends MapReduceTask<?>> taskClass,
                              int shardCount, Map<String, Object> params) throws Exception {

        if (shardCount < 1) {
            new IllegalArgumentException("分割数は1以上でなければいけません");
        }

        MapReduceTask<?> task = injector.getInstance(taskClass);

        List<Key> splitKeys = task.getSplitKeys(shardCount, params);

        final List<org.slim3plus.model.mr.KeyRange> keyRanges = new ArrayList<>();
        for (int i = 1; i < splitKeys.size(); i++) {
            keyRanges.add(new KeyRange(splitKeys.get(i - 1), splitKeys.get(i)));
        }

        if (keyRanges.size() < shardCount) {
            shardCount = keyRanges.size();
        }

        final MapReduce mapReduce = new MapReduce();
        mapReduce.setTaskClass(taskClass);
        mapReduce.setWorkerCount(shardCount);
        mapReduce.setParams(params);
        mapReduce.setStartDate(new Date());
        Datastore.put(mapReduce);

        final List<Future<Key>> shards = new ArrayList<>(shardCount);
        final List<Key> workerKeys = mapReduce.getWorkerKeys();
        for (int shardIndex = 0; shardIndex < shardCount; shardIndex++) {
            MapWorker worker = new MapWorker();
            worker.setKey(workerKeys.get(shardIndex));
            worker.setTaskClass(taskClass);
            worker.setKeyRange(keyRanges.get(shardIndex));
            worker.setProcessingKeyRange(keyRanges.get(shardIndex));
            worker.setParams(params);
            shards.add(Datastore.putWithoutTxAsync(worker));
        }

        for (Future<Key> shard : shards) {
            taskWorkerDeferred(mapReduce.getKey(), shard.get());
        }

        completeCheckerDeferred(mapReduce.getKey());
        return mapReduce;
    }

    @Deferred(queueName = "mr", tx = false)
    public TaskHandle taskWorkerDeferred(Key mapreduceKey, Key shardKey) {
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void taskWorker(Key mapreduceKey, Key shardKey)
            throws IllegalAccessException, InstantiationException {

        final MapReduce mapReduce = Datastore.getWithoutTx(MapReduce.class, mapreduceKey);
        if (mapReduce.isCancel()) {
            return;
        }

        long start = System.currentTimeMillis();

        final Transaction tx = Datastore.beginTransaction();

        final MapWorker worker = Datastore.get(tx, MapWorker.class, shardKey);
        final MapReduceTask task = injector.getInstance(worker.getTaskClass());

        Logger.getAnonymousLogger().warning("" + task.getClass());

        ModelMeta modelMeta = task.getModelMeta(worker.getParams());

        String kindName = task.getKind(worker.getParams());

        final EntityQuery q = Datastore.query(kindName);
        if (worker.getProcessingKeyRange().getLowerBound() != null) {
            q.filter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN,
                    worker.getProcessingKeyRange().getLowerBound());
        }
        if (worker.getProcessingKeyRange().getUpperBound() != null) {
            q.filter(Entity.KEY_RESERVED_PROPERTY,
                    FilterOperator.LESS_THAN_OR_EQUAL, worker
                            .getProcessingKeyRange().getUpperBound());
        }
        q.sort(Entity.KEY_RESERVED_PROPERTY, Query.SortDirection.ASCENDING);
        q.limit(1000);

        Key lastKey = null;

        if (task.isKeyOnly()) {
            for (Iterator it = q.asKeyIterator(); it.hasNext(); ) {
                lastKey = (Key) it.next();
                MapperContext ctx = worker.getContext();
                ctx.getCounter().increment("entityNum");
                boolean continueProcess = task.process(worker, lastKey, null);

                long timeElapsed = System.currentTimeMillis() - start;
                if (10000 < timeElapsed || !continueProcess) {
                    break;
                }
            }
        } else {
            for (Iterator<Entity> it = q.asIterator(); it.hasNext(); ) {
                Entity entity = it.next();
                Object model = DatastoreUtil.getModelMeta(modelMeta, entity)
                        .entityToModel(entity);
                lastKey = entity.getKey();

                MapperContext ctx = worker.getContext();
                ctx.getCounter().increment("entityNum");
                boolean continueProcess = task.process(worker, entity.getKey(), model);

                long timeElapsed = System.currentTimeMillis() - start;
                if (10000 < timeElapsed || !continueProcess) {
                    break;
                }
            }
        }

        task.onCompleteWorkerBatch(worker);

        if (lastKey != null) {
            worker.getProcessingKeyRange().setLowerBound(lastKey);
            Datastore.put(tx, worker);

            taskWorkerTxDeferred(mapreduceKey, worker.getKey());
        } else {
            worker.setComplete(true);
            Datastore.put(tx, worker);
        }

        tx.commit();
    }

    // taskWorkerDeferredはnon-txなのでtx版
    @Deferred(queueName = "mr", tx = true)
    public TaskHandle taskWorkerTxDeferred(Key mapreduceKey, Key shardKey) {
        return null;
    }

    public void taskWorkerTx(Key mapreduceKey, Key shardKey)
            throws IllegalAccessException, InstantiationException {
        taskWorker(mapreduceKey, shardKey);
    }

    @Deferred(countdownMillis = 5 * 1000, tx = true, queueName = "mr")
    public TaskHandle completeCheckerDeferred(Key mapReduceKey) {
        return null;
    }

    @Tx
    public void completeChecker(Key mapReduceKey) {
        final MapReduce mapReduce = Datastore
                .get(MapReduce.class, mapReduceKey);

        if (mapReduce.isCancel()) {
            return;
        }

        final List<MapWorker> workers = Datastore.getWithoutTx(MapWorker.class,
                mapReduce.getWorkerKeys());

        MapperContext context = new MapperContext();
        boolean isComplete = true;
        for (MapWorker worker : workers) {
            context.merge(worker.getContext());
            if (!worker.isComplete()) {
                isComplete = false;
            }
        }

        mapReduce.setContext(context);
        Datastore.put(mapReduce);
        if (isComplete) {
            completeMapReduceDeferred(mapReduceKey);
        } else {
            completeCheckerDeferred(mapReduceKey);
        }
    }

    @Tx
    public void cancel(String keyString) {
        Key mapperKey = Datastore.stringToKey(keyString);
        final MapReduce mapReduce = Datastore.get(MapReduce.class,
                mapperKey);

        mapReduce.setEndDate(new Date());
        mapReduce.setCancel(true);
        Datastore.put(mapReduce);
    }


    public Map<String, Object> getDetail(String keyString) {
        Key mapperKey = Datastore.stringToKey(keyString);
        Map<String, Object> map = new HashMap<String, Object>();

        final MapReduce mapReduce = Datastore.getWithoutTx(MapReduce.class,
                mapperKey);

        final List<MapWorker> workers = Datastore.getWithoutTx(MapWorker.class,
                mapReduce.getWorkerKeys());


        MapperContext context = new MapperContext();
        boolean isComplete = true;

        for (MapWorker worker : workers) {
            context.merge(worker.getContext());
            if (!worker.isComplete()) {
                isComplete = false;
            }
        }

        mapReduce.setContext(context);
        map.put("mapper", mapReduce);
        map.put("workers", Datastore.getWithoutTx(MapWorker.class,
                mapReduce.getWorkerKeys()));

        return map;
    }

    @Deferred(queueName = "mr", countdownMillis = 200)
    public TaskHandle completeMapReduceDeferred(Key mapReduceKey) {
        return null;
    }

    @Tx
    public void completeMapReduce(Key mapReduceKey) {
        final MapReduce mapReduce = Datastore
                .get(MapReduce.class, mapReduceKey);
        if (mapReduce.isComplete()) {
            return;
        }

        final List<MapWorker> workers = Datastore.getWithoutTx(MapWorker.class,
                mapReduce.getWorkerKeys());
        final MapperContext context = new MapperContext();
        for (MapWorker worker : workers) {
            context.merge(worker.getContext());
        }
        mapReduce.setComplete(true);
        mapReduce.setContext(context);
        mapReduce.setEndDate(new Date());
        Datastore.put(mapReduce);

        dispatchCompleteDeferred(mapReduceKey);
    }

    @Deferred(queueName = "mr", tx = true, countdownMillis = 200, randomCountdownMillis = 500)
    public TaskHandle dispatchCompleteDeferred(Key mapReduceKey) {
        return null;
    }

    public void dispatchComplete(Key mapReduceKey) {
        final MapReduce mapReduce = Datastore.getWithoutTx(MapReduce.class,
                mapReduceKey);
        MapReduceTask<?> task = injector.getInstance(mapReduce.getTaskClass());
        task.onComplete(mapReduce);
    }
}
