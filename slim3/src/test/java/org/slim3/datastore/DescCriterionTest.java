/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.datastore;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.slim3.datastore.meta.HogeMeta;
import org.slim3.datastore.model.Hoge;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.SortPredicate;

/**
 * @author higa
 * 
 */
public class DescCriterionTest extends TestCase {

    private HogeMeta meta = new HogeMeta();

    /**
     * @throws Exception
     * 
     */
    public void testApply() throws Exception {
        Query query = new Query();
        DescCriterion c = new DescCriterion(meta.myString);
        c.apply(query);
        List<SortPredicate> predicates = query.getSortPredicates();
        assertEquals("myString", predicates.get(0).getPropertyName());
        assertEquals(SortDirection.DESCENDING, predicates.get(0).getDirection());
    }

    /**
     * @throws Exception
     */
    public void testCompare() throws Exception {
        DescCriterion c = new DescCriterion(meta.myString);
        assertEquals(0, c.compare(new Hoge(), new Hoge()));
        Hoge hoge = new Hoge();
        hoge.setMyString("aaa");
        assertEquals(-1, c.compare(new Hoge(), hoge));
        assertEquals(1, c.compare(hoge, new Hoge()));
        Hoge hoge2 = new Hoge();
        hoge2.setMyString("bbb");
        assertEquals(1, c.compare(hoge, hoge2));
        assertEquals(-1, c.compare(hoge2, hoge));
    }

    /**
     * @throws Exception
     */
    public void testCompareForCollection() throws Exception {
        DescCriterion c = new DescCriterion(meta.myIntegerList);
        Hoge hoge = new Hoge();
        hoge.setMyIntegerList(new ArrayList<Integer>());
        Hoge hoge2 = new Hoge();
        hoge2.setMyIntegerList(new ArrayList<Integer>());
        assertEquals(0, c.compare(hoge, hoge2));

        hoge.getMyIntegerList().add(1);
        assertEquals(1, c.compare(hoge, hoge2));
        assertEquals(-1, c.compare(hoge2, hoge));

        hoge2.getMyIntegerList().add(2);
        assertEquals(1, c.compare(hoge, hoge2));
        assertEquals(-1, c.compare(hoge2, hoge));
    }
}