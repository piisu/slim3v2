package com.google.appengine.api.datastore.server.meta;

import com.google.appengine.api.datastore.shared.model.Bean;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2010-12-23 22:34:54")
/** */
public final class BeanMeta extends org.slim3.datastore.ModelMeta<Bean> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<Bean, java.lang.Boolean> b = new org.slim3.datastore.CoreAttributeMeta<Bean, java.lang.Boolean>(this, "b", "b", java.lang.Boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<Bean, java.lang.Double> d = new org.slim3.datastore.CoreAttributeMeta<Bean, java.lang.Double>(this, "d", "d", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<Bean, java.util.Date> da = new org.slim3.datastore.CoreAttributeMeta<Bean, java.util.Date>(this, "da", "da", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<Bean, java.lang.Float> f = new org.slim3.datastore.CoreAttributeMeta<Bean, java.lang.Float>(this, "f", "f", java.lang.Float.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<Bean, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<Bean, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringCollectionAttributeMeta<Bean, java.util.ArrayList<java.lang.String>> list = new org.slim3.datastore.StringCollectionAttributeMeta<Bean, java.util.ArrayList<java.lang.String>>(this, "list", "list", java.util.ArrayList.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<Bean, java.util.HashMap<java.lang.String,java.lang.String>> map = new org.slim3.datastore.UnindexedAttributeMeta<Bean, java.util.HashMap<java.lang.String,java.lang.String>>(this, "map", "map", java.util.HashMap.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<Bean> s = new org.slim3.datastore.StringAttributeMeta<Bean>(this, "s", "s");

    private static final BeanMeta slim3_singleton = new BeanMeta();

    /**
     * @return the singleton
     */
    public static BeanMeta get() {
       return slim3_singleton;
    }

    /** */
    public BeanMeta() {
        super("Bean", Bean.class);
    }

    @Override
    public Bean entityToModel(com.google.appengine.api.datastore.Entity entity) {
        Bean model = new Bean();
        model.setB((java.lang.Boolean) entity.getProperty("b"));
        model.setD((java.lang.Double) entity.getProperty("d"));
        model.setDa((java.util.Date) entity.getProperty("da"));
        model.setF(doubleToFloat((java.lang.Double) entity.getProperty("f")));
        model.setKey(entity.getKey());
        model.setList(toList(java.lang.String.class, entity.getProperty("list")));
        java.util.HashMap<java.lang.String,java.lang.String> _map = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("map"));
        model.setMap(_map);
        model.setS((java.lang.String) entity.getProperty("s"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        Bean m = (Bean) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("b", m.getB());
        entity.setProperty("d", m.getD());
        entity.setProperty("da", m.getDa());
        entity.setProperty("f", m.getF());
        entity.setProperty("list", m.getList());
        entity.setUnindexedProperty("map", serializableToBlob(m.getMap()));
        entity.setProperty("s", m.getS());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        Bean m = (Bean) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        Bean m = (Bean) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(Bean) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

    @Override
    protected boolean isCipherProperty(String propertyName) {
        return false;
    }
    
    @Override
    protected void postGet(Object model) {
        return;
    }
}